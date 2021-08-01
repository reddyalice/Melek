package com.alice.mel.engine;

import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class Element {
    public final Vector3f position = new Vector3f();
    public final Quaternionf rotation = new Quaternionf();
    public final Vector3f scale = new Vector3f(1,1,1);

    public final Vector3f lastPosition = new Vector3f();
    public final Quaternionf lastRotation = new Quaternionf();
    public final Vector3f lastScale = new Vector3f(1,1,1);

    public final boolean isDirty(){
        return !lastPosition.equals(position) || !lastRotation.equals(rotation) || !lastScale.equals(scale);
    }

    public final void doClean(){
        lastPosition.set(position);
        lastRotation.set(rotation);
        lastScale.set(scale);
    }

}
