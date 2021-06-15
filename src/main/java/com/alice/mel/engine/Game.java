package com.alice.mel.engine;

import com.alice.mel.utils.collections.SnapshotArray;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Game {

    public static float deltaTime = 1f/60f;
    private static final int coreCount = Runtime.getRuntime().availableProcessors();
    public final ExecutorService executor = Executors.newFixedThreadPool(coreCount);
    public final AssetManager assetManager = new AssetManager();
    public static Scene loaderScene = null;
    private final SnapshotArray<Scene> activeScenes = new SnapshotArray<>();

    public void addActiveScene(Scene scene){
        if(!activeScenes.contains(scene, false)){
            activeScenes.add(scene);
            scene.Update(deltaTime);
        }
    }

    public void removeActiveScene(Scene scene, boolean destroy){
        activeScenes.removeValue(scene, false);
        if(destroy) scene.dispose();
    }


    public void Update(){


        while(activeScenes.size > 0){
            for(Scene scene : activeScenes)
                if(scene.getWindowCount() > 0)
                scene.Update(deltaTime);
                else
                    removeActiveScene(scene, false);
        }

    }

    public void dispose(){
        activeScenes.clear();
        GLFW.glfwTerminate();
    }


}
