package com.alice.mel.systems;

import com.alice.mel.components.Component;
import com.alice.mel.engine.Entity;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.Window;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.ImmutableArray;

public abstract class IteratingSystem extends ComponentSystem{

    protected final ImmutableArray<Class<? extends Component>> requiredComponents;
    protected final Array<Entity> entites = new Array<>();

    public IteratingSystem(Class<? extends Component>... requiredComponents){
        this.requiredComponents = new ImmutableArray<>(new Array<>(requiredComponents));
    }

    public IteratingSystem(int priority, Class<? extends Component>... requiredComponents){
        super(priority);
        this.requiredComponents = new ImmutableArray<>(new Array<>(requiredComponents));
    }

    @Override
    public void addedToScene(Scene scene) {
        for(Class<? extends Component> component : requiredComponents){
            ImmutableArray<Entity> entArray = scene.getEntitiesFor(component);
            if(entArray != null){
                for(Entity en : entArray)
                    if(!entites.contains(en, false))
                        if(en.hasComponent(component))
                           entites.add(en);
            }
        }

        scene.entityAdded.add(getClass().getName(), en -> {
            for(Class<? extends Component> component : requiredComponents){
                if(!entites.contains(en, false))
                    if(en.hasComponent(component))
                        entites.add(en);
            }
        });

        scene.entityModified.add(getClass().getName(), en -> {
            for(Class<? extends Component> component : requiredComponents){
                if(!entites.contains(en, false))
                    if(en.hasComponent(component))
                        entites.add(en);
            }
        });

        scene.entityRemoved.add(getClass().getName(), en -> {
           if(entites.contains(en, false))
               entites.removeValue(en, false);
        });

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
