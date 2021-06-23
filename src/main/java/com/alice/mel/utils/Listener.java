package com.alice.mel.utils;


public interface Listener<T> {
    void receive (Signal<T> signal, T object);
}
