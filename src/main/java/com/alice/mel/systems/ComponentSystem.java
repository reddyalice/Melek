package com.alice.mel.systems;

import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.Window;
import org.jetbrains.annotations.NotNull;

public abstract class ComponentSystem implements Comparable<ComponentSystem>{

    public Scene scene;
    public int priority = 0;

    public ComponentSystem(int priority){
        this.priority = priority;
    }

    public ComponentSystem(){ }

    public abstract void addedToScene(Scene scene);
    public abstract void removedFromScene(Scene scene);
    public abstract void update(float deltaTime);
    public abstract void render(Window window, float deltaTime);

    public final void addedToSceneInternal(Scene scene){
        this.scene = scene;
        addedToScene(scene);
    }

    public final void removedFromSceneInternal(Scene scene){
        this.scene = null;
        removedFromScene(scene);
    }

    public Scene getScene(){
        return scene;
    }

    @Override
    public int compareTo(@NotNull ComponentSystem o) {
        return Integer.compare(priority, o.priority);
    }
}
