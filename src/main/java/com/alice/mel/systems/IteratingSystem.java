package com.alice.mel.systems;

import com.alice.mel.components.Component;
import com.alice.mel.engine.Entity;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.Window;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.ImmutableArray;

public abstract class IteratingSystem extends ComponentSystem{

    protected final Family requiredFamily;
    protected final Array<Entity> entites = new Array<>();

    public IteratingSystem(Family requiredFamily){
        this.requiredFamily = requiredFamily;
    }

    public IteratingSystem(int priority, Family requiredFamily){
        super(priority);
        this.requiredFamily = requiredFamily;
    }

    @Override
    public void addedToScene(Scene scene) {

        if (requiredFamily != null) {
            entites.addAll(scene.getEntitiesFor(requiredFamily).toArray());

            scene.entityAdded.add(getClass().getName(), en -> {
                if(requiredFamily.matches(en))
                    entites.add(en);
            });

            scene.entityModified.add(getClass().getName(), en -> {
                if(entites.contains(en, false))
                    if(!requiredFamily.matches(en))
                        entites.removeValue(en, false);
                else
                    if(requiredFamily.matches(en))
                        entites.add(en);

            });

            scene.entityRemoved.add(getClass().getName(), en -> {
                if (entites.contains(en, false))
                    entites.removeValue(en, false);
            });
        }



    }

    @Override
    public void removedFromScene(Scene scene) {

    }

    @Override
    public void update(float deltaTime) {
        for(Entity en : entites)
            processEntityUpdate(en, deltaTime);
    }

    @Override
    public void render(Window window, float deltaTime) {
        for(Entity en : entites)
            processEntityRender(en, window, deltaTime);
    }

    public abstract void processEntityUpdate(Entity entity, float deltaTime);
    public abstract void processEntityRender(Entity entity, Window window, float deltaTime);


}
