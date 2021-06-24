package com.alice.mel.utils;

import com.alice.mel.utils.collections.SnapshotArray;

import java.util.HashMap;
import java.util.function.Consumer;

public class Event<T>{

    private final SnapshotArray<Consumer<T>> consumers = new SnapshotArray<>();

    public void add(Consumer<T> consumer) {
        consumers.add(consumer);
    }

    public void broadcast(T in) {
        for(Consumer<T> consumer : consumers)
            consumer.accept(in);
    }

    public void dispose() {
        consumers.clear();
    }
}
