package com.alice.mel.engine;

import com.alice.mel.components.Component;
import com.alice.mel.components.ComponentType;
import com.alice.mel.utils.Event;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.Bag;
import com.alice.mel.utils.collections.Bits;
import com.alice.mel.utils.collections.ImmutableArray;
import org.joml.Vector3f;

/**
 * Entity that carries components and transformations
 * @author Bahar Demircan
 */
public class Entity {


    public final Event<Component> componentAdded = new Event<>();
    public final Event<Component> componentRemoved = new Event<>();

    public final Vector3f position = new Vector3f();
    public final Vector3f rotation = new Vector3f();
    public final Vector3f scale = new Vector3f(1,1,1);

    private final Bag<Component> components = new Bag<>();
    private final Array<Component> componentsArray = new Array<>();
    private final Bits componentBits = new Bits();

    /**
     * Add Component to the Entity
     * @param component Component that will be added
     * @return Component that added
     */
    public Component addComponent(Component component){
        Class<? extends Component> componentClass = component.getClass();
        Component oldComponent = getComponent(componentClass);

        if (component == oldComponent) {
            return component;
        }

        if (oldComponent != null) {
            removeComponent(componentClass);
        }

        int componentTypeIndex = ComponentType.getIndexFor(componentClass);
        components.set(componentTypeIndex, component);
        componentsArray.add(component);
        componentBits.set(componentTypeIndex);
        componentAdded.broadcast(getComponent(componentClass));
        return component;
    }

    /**
     * Remove component from the Entity
     * @param componentClass Class of the component that will be removed
     */
    public void removeComponent(Class<? extends Component> componentClass){
        ComponentType componentType = ComponentType.getFor(componentClass);
        int componentTypeIndex = componentType.getIndex();
        Component removeComponent = components.get(componentTypeIndex);
        if(components.isIndexWithinBounds(componentTypeIndex)) {
           if(removeComponent != null) {
               components.set(componentTypeIndex, null);
               componentsArray.removeValue(removeComponent, true);
               componentBits.clear(componentTypeIndex);
               componentRemoved.broadcast(removeComponent);
           }
        }
    }

    /**
     * Remove all components from the Entity
     */
    public void removeAllComponents () {
        while (componentsArray.size > 0) {
            removeComponent(componentsArray.get(0).getClass());
        }
    }

    /**
     * Get all the Components from the Entity
     * @return Immutable Array that carries the components
     */
    public ImmutableArray<Component> getComponents(){
        return new ImmutableArray<>(componentsArray);
    }


    /**
     * Get the component that entity has
     * @param componentClass Class of the component needed
     * @param <T> Type of the component
     * @return The Component that matches with the class, if entity doesn't have the component then null
     */
    public <T extends Component> T getComponent (Class<T> componentClass) {
        return getComponent(ComponentType.getFor(componentClass));
    }

    /**
     * Get the component that Entity has
     * @param componentType ComponentType that matches with the class of the Component
     * @param <T> Type of the component
     * @return The Component that matches with the ComponentType, if entity doesn't have the component then null
     */
    public <T extends Component> T getComponent (ComponentType componentType) {
        int componentTypeIndex = componentType.getIndex();

        if (componentTypeIndex < components.getCapacity()) {
            return (T)components.get(componentType.getIndex());
        } else {
            return null;
        }
    }

    /**
     * Checks if Entity has the component asked for
     * @param componentClass Class of the component
     * @param <T> Type of the component
     * @return True if Entity has the component else false
     */
    public <T extends Component> boolean hasComponent(Class<T> componentClass){
        return hasComponent(ComponentType.getFor(componentClass));
    }

    /**
     * Checks if Entity has the component asked for
     * @param componentType ComponentType that matches with the class of the Component
     * @return True if Entity has the component else false
     */
    public boolean hasComponent (ComponentType componentType) {
        return componentBits.get(componentType.getIndex());
    }




}
