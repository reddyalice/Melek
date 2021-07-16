package com.alice.mel.systems;

import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.Window;
import org.jetbrains.annotations.NotNull;

/**
 * Component System
 * @author Bahar Demircan
 */
public abstract class ComponentSystem implements Comparable<ComponentSystem>{

    public Scene scene;
    public int priority = 0;

    /**
     * @param priority Priority of the system
     */
    public ComponentSystem(int priority){
        this.priority = priority;
    }

    public ComponentSystem(){ }

    /**
     * Called when the System added to the scene
     * @param scene Scene System added to
     */
    public abstract void addedToScene(Scene scene);

    /**
     * Called when the System removed from the scene
     * @param scene Scene System removed from
     */
    public abstract void removedFromScene(Scene scene);

    /**
     * Update loop of the system
     * @param deltaTime Delta Time
     */
    public abstract void update(float deltaTime);

    /**
     * Rendering loop of the System
     * @param window Window It's rendering to
     * @param deltaTime Delta Time
     */
    public abstract void render(Window window, float deltaTime);

    /**
     * This is only called by the scene when the system added to the scene
     * @param scene Scene It's added to
     */
    public final void addedToSceneInternal(Scene scene){
        this.scene = scene;
        addedToScene(scene);
    }

    /**
     * This is only called by the scene when the system removed from the scene
     * @param scene Scene It's removed from
     */
    public final void removedFromSceneInternal(Scene scene){
        this.scene = null;
        removedFromScene(scene);
    }

    /**
     * Get the scene It's attached to
     * @return Scene that It's attached to
     */
    public Scene getScene(){
        return scene;
    }

    @Override
    public int compareTo(@NotNull ComponentSystem o) {
        return Integer.compare(priority, o.priority);
    }
}
