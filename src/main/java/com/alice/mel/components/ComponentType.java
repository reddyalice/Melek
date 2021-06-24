package com.alice.mel.components;

import com.alice.mel.utils.collections.Bits;
import com.alice.mel.utils.collections.ObjectMap;

/** A nice seperator for Components based on Class
 * Has nice features like indexing
 * @author Bahar Demircan
 */
public class ComponentType {
    private static final ObjectMap<Class<? extends Component>, ComponentType> assignedComponentTypes = new ObjectMap<>();
    private static int typeIndex = 0;

    private final int index;

    private ComponentType () {
        index = typeIndex++;
    }

    public int getIndex () {
        return index;
    }

    public static ComponentType getFor (Class<? extends Component> componentType) {
        ComponentType type = assignedComponentTypes.get(componentType);

        if (type == null) {
            type = new ComponentType();
            assignedComponentTypes.put(componentType, type);
        }

        return type;
    }


    public static int getIndexFor (Class<? extends Component> componentType) {
        return getFor(componentType).getIndex();
    }


    @SafeVarargs
    public static Bits getBitsFor (Class<? extends Component>... componentTypes) {
        Bits bits = new Bits();

        int typesLength = componentTypes.length;
        for (Class<? extends Component> componentType : componentTypes) {
            bits.set(ComponentType.getIndexFor(componentType));
        }

        return bits;
    }

    @Override
    public int hashCode () {
        return index;
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ComponentType other = (ComponentType)obj;
        return index == other.index;
    }
}
