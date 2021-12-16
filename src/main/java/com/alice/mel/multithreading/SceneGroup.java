package com.alice.mel.multithreading;

import com.alice.mel.engine.Scene;
import com.alice.mel.engine.SceneAdaptor;
import com.alice.mel.graphics.Monitor;
import com.alice.mel.utils.collections.SnapshotArray;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.Objects;

public final class SceneGroup extends Thread {

    private final SnapshotArray<Scene> scenes = new SnapshotArray<>();
    private final SnapshotArray<Class<? extends SceneAdaptor>> toBeAdded = new SnapshotArray<>();
    private final SnapshotArray<Class<? extends SceneAdaptor>> toBeRemoved = new SnapshotArray<>();


    private void Initialize(){
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


    @Override
    public void run() {
        Initialize();
    }

}
