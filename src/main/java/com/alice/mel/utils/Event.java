package com.alice.mel.utils;

import java.util.HashMap;
import java.util.function.Consumer;

public class Event<T>{

    private final HashMap<String,Consumer<T>> consumers = new HashMap<String,Consumer<T>>();

    public void add(String key, Consumer<T> consumer) {
        consumers.put(key,  consumer);
    }

    public void broadcast(T in) {
        for(String c : consumers.keySet())
            if(consumers.containsKey(c))
                consumers.get(c).accept(in);
    }

    public void call(String key, T in){
        if(consumers.containsKey(key))
            consumers.get(key).accept(in);
    }

    public void remove(String key) {
        consumers.remove(key);
    }

    public void dispose() {
        consumers.clear();
    }
}
