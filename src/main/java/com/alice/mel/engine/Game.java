package com.alice.mel.engine;

import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.SnapshotArray;
import org.lwjgl.glfw.GLFW;

import javax.script.ScriptEngineManager;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Game class that handles scenes
 * @author Bahar Demircan
 */
public class Game{

    public static float deltaTime = 1f/60f;
    private static final int coreCount = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService executor = Executors.newFixedThreadPool(coreCount);
    public static final ScriptEngineManager scriptManager = new ScriptEngineManager();
    public static final AssetManager assetManager = new AssetManager();
    public static Scene loaderScene = null;
    private static final SnapshotArray<Scene> activeScenes = new SnapshotArray<>();
    private static final Array<Scene> toDispose = new Array<>();


    /**
     * Add a scene to currently running active scene array
     * @param scene Scene to be added
     */
    public static void addActiveScene(Scene scene){
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
    public static void removeActiveScene(Scene scene, boolean destroy){
        activeScenes.removeValue(scene, false);
        if(destroy) scene.dispose();
        else toDispose.add(scene);
    }

    /**
     * Run the game with active scenes
     */
    public static void run(){

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
    public static void dispose(){
        for(Scene scene : toDispose)
            scene.dispose();
        activeScenes.clear();
        GLFW.glfwTerminate();
    }


    public static void Serialize(Serializable obj,
                                  String outputPath)
            throws IOException
    {
        File outputFile = new File(outputPath);
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }
        try (ObjectOutputStream outputStream
                     = new ObjectOutputStream(
                new FileOutputStream(outputFile))) {
            outputStream.writeObject(obj);
        }
    }

    public static Object Deserialize(String inputPath)
            throws IOException, ClassNotFoundException
    {
        File inputFile = new File(inputPath);
        try (ObjectInputStream inputStream
                     = new ObjectInputStream(
                new FileInputStream(inputFile))) {
            return inputStream.readObject();
        }
    }


}
