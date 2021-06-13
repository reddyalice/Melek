package com.alice.mel.utils.maths;

import com.alice.mel.graphics.Window;
import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class MathUtils {

    public static Random random = new Random();

    public static int nextPowerOfTwo (int value) {
        if (value == 0) return 1;
        value--;
        value |= value >> 1;
        value |= value >> 2;
        value |= value >> 4;
        value |= value >> 8;
        value |= value >> 16;
        return value + 1;
    }

    public static Matrix4f CreateTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale){
        Matrix4f matrix = new Matrix4f();
        matrix.identity().translate(translation).
                rotateX((float)Math.toRadians(rotation.x)).
                rotateY((float)Math.toRadians(rotation.y)).
                rotateZ((float)Math.toRadians(rotation.z)).
                scale(scale);
        return matrix;
    }

    public static void LookRelativeTo(Window window, Window relativeTo){
        if(relativeTo.active) {
            Vector2i wPos = relativeTo.getPosition();
            Vector2i w2Pos = window.getPosition();
            Vector2i wSize = relativeTo.getSize();
            Vector2i w2Size = window.getSize();
            float xDistance = (w2Pos.x + w2Size.x / 2f) - (wPos.x + wSize.x / 2f);
            float yDistance = (w2Pos.y + w2Size.y / 2f) - (wPos.y + wSize.y / 2f);
            window.camera.position.set(relativeTo.camera.position).add(xDistance, -yDistance, 0);
        }
    }


}