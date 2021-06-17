package com.alice.mel.engine;

import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.SnapshotArray;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Game {

    public static float deltaTime = 1f/60f;
    private static final int coreCount = Runtime.getRuntime().availableProcessors();
    public final ExecutorService executor = Executors.newFixedThreadPool(coreCount);
    public final AssetManager assetManager = new AssetManager();
    public Scene loaderScene = null;
    private final SnapshotArray<Scene> activeScenes = new SnapshotArray<>();
    private final Array<Scene> toDispose = new Array<>();

    public void addActiveScene(Scene scene){
        if(!activeScenes.contains(scene, false)){
            if(toDispose.contains(scene, false))
                toDispose.removeValue(scene, false);
            activeScenes.add(scene);
            scene.Update(deltaTime);
        }
    }

    public void removeActiveScene(Scene scene, boolean destroy){
        activeScenes.removeValue(scene, false);
        if(destroy) scene.dispose();
        else toDispose.add(scene);
    }


    public void Update(){


        while(activeScenes.size > 0){
            long time = System.nanoTime();
            for(Scene scene : activeScenes)
                if(scene.getWindowCount() > 0)
                scene.Update(deltaTime);
                else
                    removeActiveScene(scene, false);
            time = System.nanoTime() - time;
            deltaTime = time / 1000000000f;
        }

        dispose();

    }

    public void dispose(){
        for(Scene scene : toDispose)
            scene.dispose();
        activeScenes.clear();
        GLFW.glfwTerminate();
    }


}
