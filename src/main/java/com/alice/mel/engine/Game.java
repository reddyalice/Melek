package com.alice.mel.engine;

import com.alice.mel.graphics.Window;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.SnapshotArray;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.remotery.Remotery;
import org.lwjgl.util.remotery.RemoteryGL;

import java.io.*;
import java.nio.IntBuffer;
import java.util.concurrent.ForkJoinPool;

/**
 * Game class that handles scenes
 * @author Bahar Demircan
 */
public class Game{

    public static float deltaTime = 1f/60f;
    public static final int coreCount = Runtime.getRuntime().availableProcessors();
    public static final ForkJoinPool forkJoinPool = new ForkJoinPool(coreCount - 1);
    public static final AssetManager assetManager = new AssetManager();
    public static Scene loaderScene = null;
    private static final SnapshotArray<Scene> activeScenes = new SnapshotArray<>();
    private static final Array<Scene> toDispose = new Array<>();
    public static boolean closeCondition = false;

    public static boolean hotReload = true;
    public static boolean remoteProfiler = false;

    /**
     * Add a scene to currently running active scene array
     * @param scene Scene to be added
     */
    public static void addScene(SceneAdaptor scene){
        addScene(scene.scene);
    }


    /**
     * Add a scene to currently running active scene array
     * @param scene Scene to be added
     */
    public static void addScene(Scene scene){
        if(!activeScenes.contains(scene, false)){
            activeScenes.add(scene);
            if(toDispose.contains(scene, false)) {
                toDispose.removeValue(scene, false);
                for(Window w : scene.getWindows())
                    w.show();
            }
            scene.Update(deltaTime);
        }
    }


    /**
     * Remove the scene from active running scenes
     * @param scene Scene to removed
     * @param destroy If Scene will be disposed after removing
     */
    public static void removeScene(SceneAdaptor scene, boolean destroy){
        removeScene(scene.scene, destroy);
    }

    /**
     * Remove the scene from active running scenes
     * @param scene Scene to removed
     * @param destroy If Scene will be disposed after removing
     */
    public static void removeScene(Scene scene, boolean destroy){
        if(destroy) scene.dispose();
        else{
            for(Window w : scene.getWindows())
                w.hide();
            toDispose.add(scene);
        }
        activeScenes.removeValue(scene, false);
    }


    public static boolean isActive(Scene scene){
        return activeScenes.contains(scene, false);
    }

    public static boolean isToDispose(Scene scene){
        return toDispose.contains(scene, false);
    }

    /**
     * Run the game with active scenes
     */
    public static void run(){

        PointerBuffer rmt = BufferUtils.createPointerBuffer(1);
        if(remoteProfiler) {
            Remotery.rmt_CreateGlobalInstance(rmt);
            RemoteryGL.rmt_BindOpenGL();
        }
        while(activeScenes.size > 0 && !closeCondition){

            long time = System.nanoTime(); // Lame delta Timing
            for(Scene scene : activeScenes)
                if (scene.getWindowCount() > 0)  // As long as scene has windows to update its state
                    scene.Update(deltaTime); // Scene update
                else
                    removeScene(scene, true); //Remove the scene

            time = System.nanoTime() - time;
            deltaTime = time / 1000000000f;
            System.out.println(1f / deltaTime);
        }
        if(remoteProfiler) {
            RemoteryGL.rmt_UnbindOpenGL();
            Remotery.nrmt_DestroyGlobalInstance(rmt.get());
        }
        dispose();
    }

    /**
     * Dispose All scenes scheduled for disposal
     * Clear Active Scenes and Terminate GLFW
     */
    public static void dispose(){
        for(Scene scene : activeScenes)
            removeScene(scene, true);
        for(Scene scene : toDispose)
            scene.dispose();
        Game.assetManager.dispose();
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
