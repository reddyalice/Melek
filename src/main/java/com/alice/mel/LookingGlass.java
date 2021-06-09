package com.alice.mel;


import com.alice.mel.engine.Scene;
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

        Scene s = new Scene();
        Window w = s.createWindow("Test", 640, 480, null, true);
        Window w2 = s.createWindow("Test", 640, 480, w, false);

        while(s.getWindowCount() > 0){
            for(int i = 0; i < s.getWindowCount(); i++){

                Window window = s.getWindow(i);
                window.makeContextCurrent();
                window.swapBuffers();
                GLFW.glfwPollEvents();
                if(GLFW.glfwWindowShouldClose(window.id))
                    s.removeWindow(window);
            }
        }

        s.dispose();
        GLFW.glfwTerminate();


    }


}
