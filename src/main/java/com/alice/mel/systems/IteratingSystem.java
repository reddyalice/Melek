package com.alice.mel.systems;

import com.alice.mel.components.Component;
import com.alice.mel.engine.Game;
import com.alice.mel.engine.RelationType;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.Window;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.ImmutableArray;

public abstract class IteratingSystem extends ComponentSystem{


    protected final Array<Integer> entities = new Array<>();
    private final Class<? extends Component>[] componentClasses;
    private final RelationType relation;


    @SafeVarargs
    public IteratingSystem(RelationType relation, Class<? extends Component>... componentClasses){
        this(0, relation, componentClasses);
    }

    @SafeVarargs
    public IteratingSystem(int priority, RelationType relation, Class<? extends Component>... componentClasses){
        super(priority);
        this.componentClasses = componentClasses;
        this.relation = relation;
    }

    @Override
    public void addedToScene(Scene scene) {

        if (componentClasses != null) {
            ImmutableArray<Integer> gotEntities = scene.getFor(relation, componentClasses);
            if(gotEntities != null) {
                entities.addAll(gotEntities.toArray(Integer.class));
            }

            entityManager.entityAdded.add(getClass().getName(), en -> {
                if(entityManager.hasComponents(en, relation, componentClasses))
                    entities.add(en);
            });

            entityManager.entityModified.add(getClass().getName(), entityComponentPair -> {
                int en = entityComponentPair.getValue0();
                if(entities.contains(en, false))
                    if(!entityManager.hasComponents(en, relation, componentClasses))
                        entities.removeValue(en, false);
                else
                    if(entityManager.hasComponents(en, relation, componentClasses))
                        entities.add(en);

            });

            entityManager.entityRemoved.add(getClass().getName(), en -> {
                if (entities.contains(en, false))
                    entities.removeValue(en, false);
            });
        }



    }

    @Override
    public void removedFromScene(Scene scene) {

    }

    @Override
    public void update(float deltaTime) {
        for(int en : entities)
            Game.forkJoinPool.submit(() ->  processEntityUpdate(en, deltaTime));
    }

    @Override
    public void render(Window window, float deltaTime) {
        for(int en : entities)
            processEntityRender(en, window, deltaTime);
    }

    public abstract void processEntityUpdate(int entity, float deltaTime);
    public abstract void processEntityRender(int entity, Window window, float deltaTime);


}
