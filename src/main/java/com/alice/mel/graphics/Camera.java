package com.alice.mel.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Camera Class
 * @author Bahar Demircan
 */
public abstract class Camera {

    public float near = 1;
    public float far = 10000;

    public float viewportWidth ;
    public float viewportHeight;

    public final Vector3f position = new Vector3f();
    public final Vector3f direction = new Vector3f(0,0,-1);
    public final Vector3f up = new Vector3f(0,1,0);

    public final Matrix4f viewMatrix = new Matrix4f();
    public final Matrix4f projectionMatrix = new Matrix4f();

    /**
     * @param viewportWidth Viewport Width
     * @param viewportHeight
     */
    public Camera(float viewportWidth, float viewportHeight){
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
    }

    /**
     * Rotate the Camera in 3D
     * @param angle Angle to rotate
     * @param axisX X Component of the rotation axis
     * @param axisY Y Component of the rotation axis
     * @param axisZ Z Component of the rotation axis
     */
    public void rotate (float angle, float axisX, float axisY, float axisZ) {
        direction.rotateAxis(angle, axisX, axisY, axisZ);
        up.rotateAxis(angle, axisX, axisY, axisZ);
    }

    /**
     * Rotate the Camera in 3D
     * @param axis Rotation axis
     * @param angle Angle to rotate
     */
    public void rotate (Vector3f axis, float angle) {
        rotate(angle, axis.x, axis.y, axis.z);
    }

    /**
     * Translate the Camera in 3D
     * @param x X Component of the translation
     * @param y Y Component of the translation
     * @param z Z Component of the translation
     */
    public void translate (float x, float y, float z) {
        position.add(x, y, z);
    }

    /**
     * Translate the Camera in 3D
     * @param vec Translation Vector
     */
    public void translate (Vector3f vec) {
        position.add(vec);
    }

    private final Vector3f tmpVec = new Vector3f();

    /**
     * Look at to a position
     * @param x X Component of the position gonna be looked
     * @param y Y Component of the position gonna be looked
     * @param z Z Component of the position gonna be looked
     */
    public void lookAt (float x, float y, float z) {
        tmpVec.set(x, y, z).sub(position).normalize();
        if (tmpVec.length() != 0) {
            float dot = tmpVec.dot(up);
            if (Math.abs(dot - 1) < 0.000000001f) {
                up.set(direction).mul(-1);
            } else if (Math.abs(dot + 1) < 0.000000001f) {
                up.set(direction);
            }
            direction.set(tmpVec);
            normalizeUp();
        }
    }

    /**
     * Normalize the Up Vector
     */
    public void normalizeUp () {
        tmpVec.set(direction).cross(up);
        up.set(tmpVec).cross(direction).normalize();
    }

    /**
     * Update the camera
     */
    public abstract void update ();

    /**
     * Dispose the camera
     */
    public abstract void dispose();
}
