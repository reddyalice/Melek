package com.alice.mel.engine;

import com.alice.mel.components.Component;
import com.alice.mel.utils.Event;
import com.alice.mel.utils.KeyedEvent;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.ImmutableArray;
import com.alice.mel.utils.collections.SnapshotArray;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;

public final class EntityManager implements Serializable {

    private int lastEntityID = 0;
    private final HashMap<Class<? extends Component>, HashMap<Integer, Component>> componentEntityMap = new HashMap<>();

    private final SnapshotArray<Integer> entities = new SnapshotArray<>();
    private final HashMap<Integer, Array<Component>> components = new HashMap<>();

    public final KeyedEvent<Integer> entityAdded = new KeyedEvent<>();
    public final KeyedEvent<Pair<Integer, Component>> entityModified = new KeyedEvent<>();
    public final KeyedEvent<Integer> entityRemoved = new KeyedEvent<>();

    public final HashMap<Integer, Event<Component>> componentAdded = new HashMap<>();
    public final HashMap<Integer, Event<Component>> componentRemoved = new HashMap<>();

    /**
     * Create and Add entity to manager
     */
    public int createEntity(Component... components){
        int id = lastEntityID++;
        entities.add(id);
        if(components != null) {
            this.components.put(id, new Array<Component>(components));
            for (Component component : components) {
                HashMap<Integer, Component> hs = componentEntityMap.get(component.getClass());
                if(hs == null) {
                    componentEntityMap.put(component.getClass(), new HashMap<>());
                    componentEntityMap.get(component.getClass()).put(id, component);
                }else
                    componentEntityMap.get(component.getClass()).put(id, component);
            }
        }
        componentAdded.put(id, new Event<>());
        componentAdded.get(id).add(component -> entityModified.broadcast(Pair.with(id, component)));

        componentRemoved.put(id, new Event<>());
        componentRemoved.get(id).add(component -> entityModified.broadcast(Pair.with(id, component)));

        entityAdded.broadcast(id);
        return id;
    }

    public Pair<Integer, Component> addComponent(int entity, Component component){
        Class<? extends Component> componentClass = component.getClass();
        Component oldComponent = componentEntityMap.get(componentClass).get(entity);

        if (component == oldComponent) {
            return Pair.with(entity, component);
        }

        if (oldComponent != null) {
            removeComponent(entity, componentClass);
        }

        components.get(entity).add(component);
        HashMap<Integer, Component> hs = componentEntityMap.get(component.getClass());
        if(hs == null) {
            componentEntityMap.put(component.getClass(), new HashMap<>());
            componentEntityMap.get(component.getClass()).put(entity, component);
        }else
            componentEntityMap.get(component.getClass()).put(entity, component);


        componentAdded.get(entity).broadcast(component);
        return Pair.with(entity, component);
    }


    public <T extends Component> T getComponent(int entity, Class<T> componentClass){
        return (T) componentEntityMap.get(componentClass).get(entity);
    }

    public Pair<Integer, Component> removeComponent(int entity, Class<? extends Component> componentClass){

        Component removeComponent = componentEntityMap.get(componentClass).get(entity);

        if(components.get(entity).contains(removeComponent, false))
            components.get(entity).removeValue(removeComponent, false);

        componentEntityMap.get(componentClass).remove(entity);
        componentRemoved.get(entity).broadcast(removeComponent);
        return Pair.with(entity, removeComponent);
    }



    @SafeVarargs
    public final boolean hasComponents(int entity, RelationType relation, Class<? extends Component>... componentClasses){
        boolean val = true;
         switch (relation){
            case All -> {
                for( Class<? extends Component> componentClass : componentClasses)
                {
                    val &= hasComponent(entity, componentClass);
                }
                return val;
            }
            case Any -> {
                val = false;
                for( Class<? extends Component> componentClass : componentClasses)
                {
                    if(hasComponent(entity, componentClass)) {
                        val = true;
                        break;
                    }

                }
                return val;
            }
            case None -> {
                for( Class<? extends Component> componentClass : componentClasses)
                {
                    if(hasComponent(entity, componentClass)) {
                        val = false;
                        break;
                    }
                }
                return val;
            }

        }
        return false;
    }

    public final boolean hasComponent(int entity, Class<? extends Component> componentClass){
        if(componentEntityMap.containsKey(componentClass)){
            return componentEntityMap.get(componentClass).containsKey(entity);
        }else
            return false;
    }

    /**
     * Remove entity from the Scene and free the entity to scenes pool
     * @param entity Entity to be removed
     */
    public void removeEntity(int entity){
        entityRemoved.broadcast(entity);
        components.remove(entity);
        componentAdded.remove(entity);
        componentRemoved.remove(entity);
        for(Class<? extends Component> conponentClass : componentEntityMap.keySet()){
            HashMap<Integer, Component> cp = componentEntityMap.get(conponentClass);
            if(cp.containsKey(entity))
                cp.remove(entity);
        }
        entities.removeValue(entity, false);
    }

    @SafeVarargs
    public final ImmutableArray<Integer> getFor(RelationType relation, Class<? extends Component>... componentClasses){
        return switch (relation){
            case All -> getForAll(componentClasses);
            case Any -> getForAny(componentClasses);
            case None -> getForNone(componentClasses);
        };
    }


    @SafeVarargs
    public final ImmutableArray<Integer> getForAll(Class<? extends Component>... componentClasses){
        Array<Integer> entityList = new Array<>();
        if(componentClasses != null) {
            for (int entity : entities) {
                boolean def = true;
                for (Class<? extends Component> comps : componentClasses) {
                    def = componentEntityMap.get(comps).containsKey(entity);
                    if (!def)
                        break;
                }
                if (def)
                    entityList.add(entity);
            }
        }
        return new ImmutableArray<>(entityList);
    }

    @SafeVarargs
    public final ImmutableArray<Integer> getForAny(Class<? extends Component>... componentClasses){
        Array<Integer> entityList = new Array<>();
        if(componentClasses != null) {
            for (int entity : entities) {
                boolean def = false;
                for (Class<? extends Component> comps : componentClasses) {
                    def = componentEntityMap.get(comps).containsKey(entity);
                    if (def)
                        break;
                }
                if (def)
                    entityList.add(entity);
            }
        }
        return new ImmutableArray<>(entityList);
    }

    @SafeVarargs
    public final ImmutableArray<Integer> getForNone(Class<? extends Component>... componentClasses) {

        if(componentClasses != null) {
            Array<Integer> entityList = new Array<>();

            for (int entity : entities) {
                boolean def = false;
                for (Class<? extends Component> comps : componentClasses) {
                    def = componentEntityMap.get(comps).containsKey(entity);
                    if (def)
                        break;
                }
                if (!def)
                    entityList.add(entity);
            }
            return new ImmutableArray<>(entityList);
        }else
            return new ImmutableArray<>(entities);

        }


}
