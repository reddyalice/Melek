package com.alice.mel.engine;

import com.alice.mel.graphics.Window;
import org.lwjgl.glfw.GLFW;

public final class InputHandler {

    public static boolean getKey(Scene scene, int key){
        boolean is = false;
        for(Window window : scene.getWindows()){
            int tmp =  GLFW.glfwGetKey(window.id, key);
            is = tmp == GLFW.GLFW_TRUE;
            if(is) break;
        }
        return is;
    }

    public static boolean getKey(int key, Window window){
        int tmp =  GLFW.glfwGetKey(window.id, key);
        return tmp == GLFW.GLFW_TRUE;
    }
}
