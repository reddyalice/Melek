package com.alice.mel.systems;

import com.alice.mel.components.BatchRenderingComponent;
import com.alice.mel.engine.Entity;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.Window;
import com.alice.mel.utils.collections.ImmutableArray;

public class BatchedRenderingSystem extends ComponentSystem{

    @Override
    public void addedToScene(Scene scene) {
        ImmutableArray<Entity> entities = scene.getEntitiesFor(BatchRenderingComponent.class);

    }

    @Override
    public void removedFromScene(Scene scene) {

    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void render(Window window, float deltaTime) {

    }
}
