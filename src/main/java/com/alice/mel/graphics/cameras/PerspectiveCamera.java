package com.alice.mel.graphics.cameras;

import com.alice.mel.graphics.Camera;
import org.joml.Vector3f;

public class PerspectiveCamera extends Camera {
    public float fieldOfView;

    public PerspectiveCamera (float fieldOfViewY, float viewportWidth, float viewportHeight) {
        super(viewportWidth, viewportHeight);
        this.fieldOfView = fieldOfViewY;
        update();
    }

   private final Vector3f tmp = new Vector3f();

    @Override
    public void update () {

        float aspect = viewportWidth / viewportHeight;
        projectionMatrix.identity().setPerspective(fieldOfView, aspect, Math.abs(near), Math.abs(far));
        viewMatrix.identity().lookAt(position, tmp.set(position).add(direction), up);
    }

    @Override
    public void dispose() {
    }
}
