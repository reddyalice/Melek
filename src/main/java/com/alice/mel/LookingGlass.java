package com.alice.mel;


import com.alice.mel.graphics.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class LookingGlass {

    public static void main(String[] args) {
        GLFWErrorCallback.createPrint(System.err).set();
        boolean isInitialized  = GLFW.glfwInit();
        if(!isInitialized){
            System.err.println("Failed To initialized!");
            System.exit(1);
        }

        Window w = new Window("Test", 640, 480, null, true);
        //w.setTransparent(true);
        while(!GLFW.glfwWindowShouldClose(w.id)){
            GLFW.glfwPollEvents();

            w.swapBuffers();
        }

        w.dispose();
        GLFW.glfwTerminate();


    }


}
