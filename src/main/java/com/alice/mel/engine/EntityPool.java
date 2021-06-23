package com.alice.mel.engine;

import com.alice.mel.utils.collections.Array;

public class EntityPool {

    public final int max;
    public int peak;
    public final Scene scene;
    public final Array<Entity> freedEntites;


    public EntityPool (Scene scene) {
        this(scene,4, Integer.MAX_VALUE);
    }

    public EntityPool (Scene scene, int initialCapacity) {
        this(scene, initialCapacity, Integer.MAX_VALUE);
    }

    public EntityPool (Scene scene, int initialCapacity, int max) {
        this.scene = scene;
        freedEntites = new Array<>(false, initialCapacity);
        this.max = max;
    }

    public Entity obtain () {
        if (freedEntites.size > 0)
            return freedEntites.pop();
        else
            return new Entity();

    }

    public void free (Entity entity) {
        if (entity == null) throw new IllegalArgumentException("Entity cannot be null.");


            if (freedEntites.size < max) {
                freedEntites.add(entity);
                peak = Math.max(peak, freedEntites.size);
                reset(entity);
            } else {
                discard(entity);
            }



    }

    private void reset(Entity entity) {
        entity.removeAllComponents();
        entity.componentAdded.dispose();
        entity.componentRemoved.dispose();
    }

    private void discard(Entity entity) {
        entity.removeAllComponents();
        entity.componentAdded.dispose();
        entity.componentRemoved.dispose();
    }


    public void dispose() {
        for(Entity entity : freedEntites)
            discard(entity);
        freedEntites.clear();
    }



}
