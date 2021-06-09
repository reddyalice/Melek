package com.alice.mel.utils;


public interface Listener<T> {
    public void receive (Signal<T> signal, T object);
}
