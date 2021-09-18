package com.alice.mel.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class KeyedEvent<T> implements Serializable{

    public final HashMap<String, SerializableConsumer<T>>  consumers = new HashMap<>();

    public void add(String key, SerializableConsumer<T> consumer) {
        consumers.put(key, consumer);
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
