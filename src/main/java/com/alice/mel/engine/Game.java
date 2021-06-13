package com.alice.mel.engine;

import com.alice.mel.graphics.*;
import com.alice.mel.utils.Disposable;
import com.alice.mel.utils.Event;
import com.alice.mel.utils.collections.Array;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Game {

    public static final WindowPool windowPool = new WindowPool();
    public static final CameraPool cameraPool = new CameraPool();

    private static final int coreCount = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService executor = Executors.newFixedThreadPool(coreCount);
    public static Window loaderWindow;
    public static Window currentContext;
    public static Scene currentScene;
    public static float deltaTime = 1f/60f;
    public static boolean initialized = false;


    public static final Array<Scene> scenes = new Array<>();
    public static final Event<Float> inputEvent = new Event<>();
    public static boolean close = false;


    public static void setScene(int index){
        if (currentScene != null)
            currentScene.close();
        currentScene = scenes.get(index);
        currentScene.Update(deltaTime);
    }

    public static void setScene(Scene scene){
        int index = scenes.indexOf(scene, false);
        setScene(index);
    }

    public static void run() {





                while (currentScene != null) {
                    inputEvent.broadcast(deltaTime);
                    currentScene.Update(deltaTime);

                }



        dispose();
        GLFW.glfwTerminate();

    }


    public static void dispose() {


        for(Scene scene : scenes)
            scene.close();

        windowPool.dispose();
        cameraPool.dispose();
        scenes.clear();


    }


}
