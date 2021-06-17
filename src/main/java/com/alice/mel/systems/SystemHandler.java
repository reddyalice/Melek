package com.alice.mel.systems;

import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.ImmutableArray;
import com.alice.mel.utils.collections.ObjectMap;

import java.util.Comparator;

public class SystemHandler {

    private final SystemComparator systemComparator = new SystemComparator();
    private final Array<ComponentSystem> systems = new Array<ComponentSystem>(true, 16);
    private final ImmutableArray<ComponentSystem> immutableSystems = new ImmutableArray<ComponentSystem>(systems);
    private final ObjectMap<Class<?>, ComponentSystem> systemsByClass = new ObjectMap<Class<?>, ComponentSystem>();
    private final SystemListener listener;

    public SystemHandler(SystemListener listener) {
        this.listener = listener;
    }

    public void addSystem(ComponentSystem system){
        Class<? extends ComponentSystem> systemType = system.getClass();
        ComponentSystem oldSytem = getSystem(systemType);

        if (oldSytem != null) {
            removeSystem(oldSytem);
        }

        systems.add(system);
        systemsByClass.put(systemType, system);
        systems.sort(systemComparator);
        listener.systemAdded(system);
    }

    public void removeSystem(ComponentSystem system){
        if(systems.removeValue(system, true)) {
            systemsByClass.remove(system.getClass());
            listener.systemRemoved(system);
        }
    }

    public void removeAllSystems() {
        while(systems.size > 0) {
            removeSystem(systems.first());
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ComponentSystem> T getSystem(Class<T> systemType) {
        return (T) systemsByClass.get(systemType);
    }

    public ImmutableArray<ComponentSystem> getSystems() {
        return immutableSystems;
    }


    private static class SystemComparator implements Comparator<ComponentSystem> {
        @Override
        public int compare(ComponentSystem a, ComponentSystem b) {
            return Integer.compare(a.priority, b.priority);
        }
    }

    public interface SystemListener {
        void systemAdded(ComponentSystem system);
        void systemRemoved(ComponentSystem system);
    }
}

