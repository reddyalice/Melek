package com.alice.mel.engine;

import com.alice.mel.utils.collections.Array;

/**
 * And Entity Pool to avoid allocation
 * @author Bahar Demircan
 */
public class EntityPool {

    public final int max;
    public int peak;

    public final Array<Entity> freedEntites;


    public EntityPool () {
        this(4, Integer.MAX_VALUE);
    }

    /**
     * @param initialCapacity Initial Capacity for freed entity Array
     */
    public EntityPool (int initialCapacity) {
        this(initialCapacity, Integer.MAX_VALUE);
    }

    /**
     * @param initialCapacity Initial Capacity for freed entity Array
     * @param max Maximum Capacity for freed entity Array
     */
    public EntityPool (int initialCapacity, int max) {

        freedEntites = new Array<>(false, initialCapacity);
        this.max = max;
    }

    /**
     * Create or get an entity from freed Array
     * @return Obtained Entity
     */
    public Entity obtain () {
        if (freedEntites.size > 0)
            return freedEntites.pop();
        else
            return new Entity();

    }

    /**
     * Free Entity for recycling later
     * @param entity Entity to be freed
     */
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

    /**
     * Dispose all freed Arrays and clear them
     */
    public void dispose() {
        for(Entity entity : freedEntites)
            discard(entity);
        freedEntites.clear();
    }



}
