package com.alice.mel.engine;

import com.alice.mel.components.Component;
import com.alice.mel.components.ComponentType;
import com.alice.mel.utils.Signal;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.Bag;
import com.alice.mel.utils.collections.Bits;
import com.alice.mel.utils.collections.ImmutableArray;
import org.joml.Vector3f;

public class Entity {

    public final Signal<Entity> componentAdded = new Signal<>();
    public final Signal<Entity> componentRemoved = new Signal<>();

    public final Vector3f position = new Vector3f();
    public final Vector3f rotation = new Vector3f();
    public final Vector3f scale = new Vector3f(1,1,1);

    private final Bag<Component> components = new Bag<>();
    private final Array<Component> componentsArray = new Array<>();
    private final Bits componentBits = new Bits();

    private Scene scene;

    public Entity(Scene scene){
        this.scene = scene;
    }

    public void setScene(Scene scene){
        this.scene = scene;
    }

    public Scene getScene(){
        return scene;
    }

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
        notifyComponentAdded();
        return component;
    }

    public void removeComponent(Class<? extends Component> componentClass){
        ComponentType componentType = ComponentType.getFor(componentClass);
        int componentTypeIndex = componentType.getIndex();
        Component removeComponent = components.get(componentTypeIndex);
        if(components.isIndexWithinBounds(componentTypeIndex)) {
           if(removeComponent != null) {
               components.set(componentTypeIndex, null);
               componentsArray.removeValue(removeComponent, true);
               componentBits.clear(componentTypeIndex);
               notifyComponentRemoved();
           }
        }
    }

    public void removeAllComponents () {
        while (componentsArray.size > 0) {
            removeComponent(componentsArray.get(0).getClass());
        }
    }
    
    public ImmutableArray<Component> getComponents(){
        return new ImmutableArray<>(componentsArray);
    }

    public void addToScene(){
        scene.addEntity(this);
    }

    public void removeFromScene(){
        scene.removeEntity(this);
    }

    public <T extends Component> T getComponent (Class<T> componentClass) {
        return getComponent(ComponentType.getFor(componentClass));
    }

    public <T extends Component> T getComponent (ComponentType componentType) {
        int componentTypeIndex = componentType.getIndex();

        if (componentTypeIndex < components.getCapacity()) {
            return (T)components.get(componentType.getIndex());
        } else {
            return null;
        }
    }

    public <T extends Component> boolean hasComponent(Class<T> componentClass){
        return hasComponent(ComponentType.getFor(componentClass));
    }

    public boolean hasComponent (ComponentType componentType) {
        return componentBits.get(componentType.getIndex());
    }

    public void notifyComponentAdded() {
        componentAdded.dispatch(this);
    }

    public void notifyComponentRemoved() {
        componentRemoved.dispatch(this);
    }


}