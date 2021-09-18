package com.alice.mel.utils;

import com.alice.mel.utils.collections.SnapshotArray;

import java.io.Serializable;
import java.util.HashMap;
import java.util.function.Consumer;

public class Event<T> implements Serializable {

    private final SnapshotArray<SerializableConsumer<T>> consumers = new SnapshotArray<>();

    public void add(SerializableConsumer<T> consumer) {
        consumers.add(consumer);
    }

    public void broadcast(T in) {
        for(SerializableConsumer<T> consumer : consumers)
            consumer.accept(in);
    }

    public void dispose() {
        consumers.clear();
    }
}
