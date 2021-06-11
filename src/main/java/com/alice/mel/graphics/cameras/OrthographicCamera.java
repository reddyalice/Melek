package com.alice.mel.graphics.cameras;

import com.alice.mel.graphics.Camera;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class OrthographicCamera extends Camera {

    public float zoom = 1;


    public OrthographicCamera (float viewportWidth, float viewportHeight) {
        super(viewportWidth, viewportHeight);
        //setToOrtho(false, viewportWidth, viewportHeight);
        this.near = 0;
        update();
    }

    private final Vector3f tmp = new Vector3f();


    @Override
    public void update () {
        projectionMatrix.identity().setOrtho(zoom * -viewportWidth / 2, zoom * (viewportWidth / 2), zoom * -(viewportHeight / 2), zoom
                * viewportHeight / 2, near, far);
        viewMatrix.identity().lookAt(position, tmp.set(position).add(direction), up);
    }

    public void setToOrtho (boolean yDown, float viewportWidth, float viewportHeight) {
        if (yDown) {
            up.set(0, -1, 0);
            direction.set(0, 0, 1);
        } else {
            up.set(0, 1, 0);
            direction.set(0, 0, -1);
        }
        position.set(zoom * viewportWidth / 2.0f, zoom * viewportHeight / 2.0f, 0);
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        update();
    }

    public void rotate (float angle) {
        rotate(direction, angle);
    }

    public void translate (float x, float y) {
        translate(x, y, 0);
    }

    public void translate (Vector2f vec) {
        translate(vec.x, vec.y, 0);
    }

    @Override
    public void dispose() {
    }
}
