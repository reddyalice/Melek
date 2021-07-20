package com.alice.mel.engine;

import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.SnapshotArray;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Game class that handles scenes
 * @author Bahar Demircan
 */
public class Game implements Runnable{

    public static float deltaTime = 1f/60f;

    private static final int coreCount = Runtime.getRuntime().availableProcessors();
    public final ExecutorService executor = Executors.newFixedThreadPool(coreCount);
    public final AssetManager assetManager = new AssetManager();
    public Scene loaderScene = null;
    private final SnapshotArray<Scene> activeScenes = new SnapshotArray<>();
    private final Array<Scene> toDispose = new Array<>();


    /**
     * Add a scene to currently running active scene array
     * @param scene Scene to be added
     */
    public void addActiveScene(Scene scene){
        if(!activeScenes.contains(scene, false)){
            if(toDispose.contains(scene, false))
                toDispose.removeValue(scene, false);
            activeScenes.add(scene);
            scene.Update(deltaTime);
        }
    }

    /**
     * Remove the scene from active running scenes
     * @param scene Scene to removed
     * @param destroy If Scene will be disposed after removing
     */
    public void removeActiveScene(Scene scene, boolean destroy){
        activeScenes.removeValue(scene, false);
        if(destroy) scene.dispose();
        else toDispose.add(scene);
    }

    /**
     * Run the game with active scenes
     */
    @Override
    public void run(){
        System.out.println(OSType.DETECTED);

        while(activeScenes.size > 0){
            long time = System.nanoTime(); // Lame delta Timing
            for(Scene scene : activeScenes)
                if(scene.getWindowCount() > 0) // As long as scene has windows to update its state
                scene.Update(deltaTime); // Scene update
                else
                    removeActiveScene(scene, false); //Remove the scene without destroying it
            time = System.nanoTime() - time;
            deltaTime = time / 1000000000f;

        }
        dispose();
    }

    /**
     * Dispose All scenes scheduled for disposal
     * Clear Active Scenes and Terminate GLFW
     */
    public void dispose(){
        for(Scene scene : toDispose)
            scene.dispose();
        activeScenes.clear();
        GLFW.glfwTerminate();
    }


}
