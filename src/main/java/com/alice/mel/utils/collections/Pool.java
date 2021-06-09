package com.alice.mel.utils.collections;

public abstract class Pool<T> {

    public final int max;
    public int peak;

    protected final Array<T> freeObjects;

    public Pool () {
        this(16, Integer.MAX_VALUE);
    }

    public Pool (int initialCapacity) {
        this(initialCapacity, Integer.MAX_VALUE);
    }

    public Pool (int initialCapacity, int max) {
        freeObjects = new Array(false, initialCapacity);
        this.max = max;
    }

    abstract protected T newObject ();

    public T obtain () {
        return freeObjects.size == 0 ? newObject() : freeObjects.pop();
    }

    public void free (T object) {
        if (object == null) throw new IllegalArgumentException("object cannot be null.");
        if (freeObjects.size < max) {
            freeObjects.add(object);
            peak = Math.max(peak, freeObjects.size);
            reset(object);
        } else {
            discard(object);
        }
    }

    public void fill (int size) {
        for (int i = 0; i < size; i++)
            if (freeObjects.size < max) freeObjects.add(newObject());
        peak = Math.max(peak, freeObjects.size);
    }

    protected void reset (T object) {
        if (object instanceof Poolable) ((Poolable)object).reset();
    }

    protected void discard (T object) {
    }

    public void freeAll (Array<T> objects) {
        if (objects == null) throw new IllegalArgumentException("objects cannot be null.");
        Array<T> freeObjects = this.freeObjects;
        int max = this.max;
        for (int i = 0, n = objects.size; i < n; i++) {
            T object = objects.get(i);
            if (object == null) continue;
            if (freeObjects.size < max) {
                freeObjects.add(object);
                reset(object);
            } else {
                discard(object);
            }
        }
        peak = Math.max(peak, freeObjects.size);
    }

    public void clear () {
        for (int i = 0; i < freeObjects.size; i++) {
            T obj = freeObjects.pop();
            discard(obj);
        }
    }

    public int getFree () {
        return freeObjects.size;
    }

    public static interface Poolable {
        public void reset ();
    }

}
