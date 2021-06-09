package com.alice.mel.utils.maths;

import org.joml.Vector3f;

public class Ray {

    public final Vector3f origin = new Vector3f();
    public final Vector3f direction = new Vector3f();

    public Ray () { }
    public Ray (Vector3f origin, Vector3f direction) {
        this.origin.set(origin);
        this.direction.set(direction).normalize();
    }

    public Ray set (Vector3f origin, Vector3f direction) {
        this.origin.set(origin);
        this.direction.set(direction).normalize();
        return this;
    }

    public Ray set (float x, float y, float z, float dx, float dy, float dz) {
        this.origin.set(x, y, z);
        this.direction.set(dx, dy, dz).normalize();
        return this;
    }

    public Ray set (Ray ray) {
        this.origin.set(ray.origin);
        this.direction.set(ray.direction).normalize();
        return this;
    }

}
