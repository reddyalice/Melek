package com.alice.mel.utils;

import java.util.HashMap;
import java.util.function.Consumer;

public class Event<T>{

    public HashMap<String,Consumer<T>> consumers = new HashMap<String,Consumer<T>>();


    public void Add(String name, Consumer<T> c) {
        consumers.put(name,  c);
    }

    public void Broadcast(T t) {
        for(String c : consumers.keySet())
            if(consumers.containsKey(c))
                consumers.get(c).accept(t);
    }


    public void Remove(String name) {
        if(consumers.containsKey(name))
            consumers.remove(name);
    }

    public void Clear() {
        consumers.clear();
    }
}
