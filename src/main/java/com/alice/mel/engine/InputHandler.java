package com.alice.mel.engine;

import com.alice.mel.graphics.Window;
import org.lwjgl.glfw.GLFW;

/**
 * General Input Handling Class
 * @author Bahar Demircan
 */
public final class InputHandler {

    /**
     * Get the key press from any window in the scene
     * @param scene Scene that Windows belong to
     * @param key Key that is pressed
     * @return true if key is pressed else false
     */
    public static boolean getKey(Scene scene, int key){
        boolean is = false;
        for(Window window : scene.getWindows()){
            is = getKey(window, key);
            if(is) break;
        }
        return is;
    }

    /**
     * Get the key press from a specific window
     * @param window Window that will be checked for the pressed
     * @param key Key that is pressed
     * @return true if key is pressed else false
     */
    public static boolean getKey(Window window, int key){
        int tmp =  GLFW.glfwGetKey(window.id, key);
        return tmp == GLFW.GLFW_TRUE;
    }
}
