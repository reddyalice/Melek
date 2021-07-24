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
    public static boolean getKeyPressed(Scene scene, int key){
        boolean is = false;
        for(Window window : scene.getWindows()){
            is = getKeyPressed(window, key);
            if(is) break;
        }
        return is;
    }


    /**
     * Get the key release from any window in the scene
     * @param scene Scene that Windows belong to
     * @param key Key that is released
     * @return true if key is released else false
     */
    public static boolean getKeyReleased(Scene scene, int key){
        boolean is = false;
        for(Window window : scene.getWindows()){
            is = getKeyReleased(window, key);
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
    public static boolean getKeyPressed(Window window, int key){
        int tmp =  GLFW.glfwGetKey(window.id, key);
        return tmp == GLFW.GLFW_PRESS;
    }

    /**
     * Get the key release from a specific window
     * @param window Window that will be checked for the pressed
     * @param key Key that is released
     * @return true if key is released else false
     */
    public static boolean getKeyReleased(Window window, int key){
        int tmp =  GLFW.glfwGetKey(window.id, key);
        return tmp == GLFW.GLFW_RELEASE;
    }

    /**
     * Get the key press from any window in the scene
     * @param scene Scene that Windows belong to
     * @param button Mouse Button that is pressed
     * @return true if key is pressed else false
     */
    public static boolean  getMouseButtonPressed(Scene scene, int button){
        boolean is = false;
        for(Window window : scene.getWindows()){
            is = getMouseButtonPressed(window, button);
            if(is) break;
        }
        return is;
    }

    /**
     * Get the key release from any window in the scene
     * @param scene Scene that Windows belong to
     * @param button Mouse Button that is released
     * @return true if key is released else false
     */
    public static boolean  getMouseButtonReleased(Scene scene, int button){
        boolean is = false;
        for(Window window : scene.getWindows()){
            is = getMouseButtonReleased(window, button);
            if(is) break;
        }
        return is;
    }

    /**
     * Get the mouse press from a specific window
     * @param window Window that will be checked for the pressed
     * @param button Mouse Button that is pressed
     * @return true if key is pressed else false
     */
    public static boolean getMouseButtonPressed(Window window, int button){
        int tmp =  GLFW.glfwGetMouseButton(window.id, button);
        return tmp == GLFW.GLFW_PRESS;
    }

    /**
     * Get the mouse release from a specific window
     * @param window Window that will be checked for the pressed
     * @param button Mouse Button that is released
     * @return true if key is released else false
     */
    public static boolean getMouseButtonReleased(Window window, int button){
        int tmp =  GLFW.glfwGetMouseButton(window.id, button);
        return tmp == GLFW.GLFW_RELEASE;
    }


}
