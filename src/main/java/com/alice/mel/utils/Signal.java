package com.alice.mel.utils;

import com.alice.mel.utils.collections.SnapshotArray;

public class Signal<T> {
    private SnapshotArray<Listener<T>> listeners;

    public Signal () {
        listeners = new SnapshotArray<Listener<T>>();
    }


    public void add (Listener<T> listener) {
        listeners.add(listener);
    }


    public void remove (Listener<T> listener) {
        listeners.removeValue(listener, true);
    }


    public void removeAllListeners () {
        listeners.clear();
    }


    public void dispatch (T object) {
        final Object[] items = listeners.begin();
        for (int i = 0, n = listeners.size; i < n; i++) {
            Listener<T> listener = (Listener<T>)items[i];
            listener.receive(this, object);
        }
        listeners.end();
    }
}
