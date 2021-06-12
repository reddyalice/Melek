package com.alice.mel.engine;

import com.alice.mel.graphics.*;
import com.alice.mel.utils.Disposable;
import com.alice.mel.utils.collections.Array;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Game {

    public static final WindowPool windowPool = new WindowPool();
    public static final CameraPool cameraPool = new CameraPool();

    private static final int coreCount = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService executor = Executors.newFixedThreadPool(coreCount);
    public static final Scene loaderScene = new Scene();
    public static final Window loaderWindow = loaderScene.createWindow(CameraType.Orthographic, "LoaderWindow", 640, 480);
    public static Window currentContext;
    public static Scene currentScene;
    public static float deltaTime = 1f/60f;

    public static final Array<Shader> shaders = new Array<>();
    public static final Array<Texture> textures = new Array<>();
    public static final Array<Mesh> meshes = new Array<>();
    public static final Array<Scene> scenes = new Array<>();


    public static void run() {

        loaderWindow.hide();
        loaderScene.init.add("load", x -> {
            for(Shader shader : shaders)
                shader.compile();

            for(Texture texture : textures)
                texture.genTexture();
        });

        loaderScene.multiInit.add("load", x -> {
            for(Mesh mesh :meshes)
                mesh.genMesh();
        });

        loaderScene.Update(1/60f);
        loaderScene.close();
        currentScene = scenes.get(0);

        if(currentScene != null){
            while(currentScene.getWindowCount() > 0){
                currentScene.Update(deltaTime);
            }
        }

        dispose();
        GLFW.glfwTerminate();

    }


    public static void dispose() {
        windowPool.dispose();
        cameraPool.dispose();

        loaderScene.dispose();

        for(Shader shader : shaders)
            shader.dispose();

        for(Texture texture : textures)
            texture.dispose();

        for(Mesh mesh : meshes)
            mesh.dispose();

        for(Scene scene : scenes)
            scene.dispose();

        shaders.clear();
        textures.clear();
        meshes.clear();
        scenes.clear();


    }


}
