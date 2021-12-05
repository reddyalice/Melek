package com.alice.mel.components;

import java.io.Serializable;

public abstract class Component implements Serializable {

    public abstract Component Clone();
    public abstract boolean isDirty();
    public abstract void doClean();

}
