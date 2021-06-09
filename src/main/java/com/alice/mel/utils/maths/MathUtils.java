package com.alice.mel.utils.maths;

import org.joml.Matrix4f;
import org.joml.Random;
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
        matrix.identity().translate(translation).rotateX((float)Math.toRadians(rotation.x)).
                rotateY((float)Math.toRadians(rotation.y)).
                rotateZ((float)Math.toRadians(rotation.z)).scale(scale.x, scale.y, scale.z);
        return matrix;
    }


}