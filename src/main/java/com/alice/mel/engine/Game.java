package com.alice.mel.engine;

import com.alice.mel.graphics.*;
import com.alice.mel.utils.Disposable;
import com.alice.mel.utils.collections.Array;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Game implements Disposable, Runnable {

    public static final WindowPool windowPool = new WindowPool();
    public static final CameraPool cameraPool = new CameraPool();

    private static final int coreCount = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService executor = Executors.newFixedThreadPool(coreCount);
    public static final Scene loaderScene = new Scene();
    public static final Window loaderWindow = loaderScene.createWindow(CameraType.Orthographic, "LoaderWindow", 640, 480);
    public static Window currentContext;

    public static Array<Shader> shaders = new Array<>();
    public static Array<Texture> textures = new Array<>();
    public static Array<Mesh> meshes = new Array<>();







    public Game(){

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


    }

    @Override
    public void run() {

    }


    @Override
    public void dispose() {
        windowPool.dispose();
        cameraPool.dispose();
    }


}
