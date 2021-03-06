package com.alice.mel.utils.maths;

import com.alice.mel.graphics.Window;
import org.joml.*;

import java.lang.Math;

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

    public static long roundUpPow2X(long n, long x) {
        return ((n + x - 1) & (-x));
    }

    public static Matrix4f CreateTransformationMatrix(Vector3f translation, Quaternionf rotation, Vector3f scale){
        Matrix4f matrix = new Matrix4f();
        matrix.identity().translate(translation).
                rotate(rotation).
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