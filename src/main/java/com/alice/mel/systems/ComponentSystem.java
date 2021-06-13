package com.alice.mel.systems;

import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.Camera;
import com.alice.mel.utils.Disposable;

public abstract class ComponentSystem implements Disposable {

    public Scene scene;


    public abstract void addedToScene(Scene scene);
    public abstract void removedFromScene(Scene scene);
    public abstract void update(float deltaTime);
    public abstract void render(Camera camera, float deltaTime);

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


}
