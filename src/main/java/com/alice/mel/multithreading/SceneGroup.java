package com.alice.mel.multithreading;

import com.alice.mel.engine.Scene;
import com.alice.mel.engine.SceneAdaptor;
import com.alice.mel.graphics.Monitor;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.SnapshotArray;
import com.alice.mel.utils.reflections.ClassReflection;
import com.alice.mel.utils.reflections.ReflectionException;
import org.javatuples.Pair;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.HashMap;
import java.util.Objects;

public final class SceneGroup extends Thread {

    private final HashMap<Class<? extends SceneAdaptor>, Scene> activeScenes = new HashMap<>();
    private final HashMap<Class<? extends SceneAdaptor>, Scene> toDispose = new HashMap<>();

    private final SnapshotArray<Class<? extends SceneAdaptor>> toBeAdded = new SnapshotArray<>();
    private final SnapshotArray<Pair<Class<? extends SceneAdaptor>, Boolean>> toBeRemoved = new SnapshotArray<>();


    private void initialize(){
        GLFWErrorCallback.createPrint(System.err).set();
        boolean isInitialized = GLFW.glfwInit();
        if (!isInitialized) {
            System.err.println("Failed To initialized!");
            System.exit(1);
        }

        PointerBuffer pf = GLFW.glfwGetMonitors();
        for(int i = 0; i < Objects.requireNonNull(pf).remaining();)
            new Monitor(pf.get());

        GLFW.glfwSetMonitorCallback((mon, event) -> {
            switch (event) {
                case GLFW.GLFW_CONNECTED -> new Monitor(mon);
                case GLFW.GLFW_DISCONNECTED -> Monitor.monitors.remove(mon);
            }
        });
    }

    private void checkToAddOrRemove() throws ReflectionException {
        if(!toBeAdded.isEmpty()){
            for(var sceneClass : toBeAdded){
                if(toDispose.containsKey(sceneClass)){
                    Scene scene = toDispose.get(sceneClass);
                    activeScenes.put(sceneClass, scene);
                    toDispose.remove(sceneClass);
                    scene.Update(deltaTime);
                }else{
                    Scene scene = ClassReflection.newInstance(sceneClass).scene;
                    activeScenes.put(sceneClass, scene);
                    scene.Update(deltaTime);
                }
                toBeAdded.removeValue(sceneClass, false);
            }
        }

        if(!toBeRemoved.isEmpty()){
            for(var sceneClass : toBeRemoved){
                if(activeScenes.containsKey(sceneClass.getValue0())){
                    Scene scene = activeScenes.get(sceneClass.getValue0());
                    if(sceneClass.getValue1()) scene.dispose();
                    else
                        toDispose.put(sceneClass.getValue0(), scene);
                    activeScenes.remove(sceneClass.getValue0());

                }
            }
        }
    }


    public void AddScene(Class<? extends SceneAdaptor> sceneAdaptorClass){
        toBeAdded.add(sceneAdaptorClass);
    }

    public void RemoveScene(Class<? extends SceneAdaptor> sceneAdaptorClass, boolean destroy){
        toBeRemoved.add(Pair.with(sceneAdaptorClass, destroy));
    }

    public void RemoveScene(Class<? extends SceneAdaptor> sceneAdaptorClass){
        RemoveScene(sceneAdaptorClass, true);
    }

    public float deltaTime = 1f/60f;
    @Override
    public void run() {
        initialize();
        try {
            checkToAddOrRemove();
        } catch (ReflectionException e) {
            e.printStackTrace();
        }
        while(activeScenes.size() > 0){
            long time = System.nanoTime(); // Lame delta Timing
            try {
                checkToAddOrRemove();
            } catch (ReflectionException e) {
                e.printStackTrace();
            }
            for(var sceneKey : activeScenes.keySet()) {
                Scene scene = activeScenes.get(sceneKey);
                if (scene.getWindowCount() > 0)  // As long as scene has windows to update its state
                    scene.Update(deltaTime); // Scene update
                else
                    RemoveScene(sceneKey, false); //Remove the scene without destroying it
            }
            time = System.nanoTime() - time;
            deltaTime = time / 1000000000f;
            System.out.println(1f / deltaTime);


        }
        dispose();
    }

     private void dispose(){
        for(Scene scene : activeScenes.values())
            scene.dispose();
        activeScenes.clear();
        for(Scene scene : toDispose.values())
            scene.dispose();
        toDispose.clear();
        GLFW.glfwTerminate();
    }

}
