package com.alice.mel.engine;


import com.alice.mel.graphics.*;
import com.alice.mel.utils.Disposable;
import com.alice.mel.utils.Event;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.SnapshotArray;
import org.javatuples.Pair;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Scene implements Disposable {


    private final SnapshotArray<Window> windows = new SnapshotArray<>();
    public final Window loaderWindow;
    public Window currentContext;

    public final WindowPool windowPool = new WindowPool(this);
    public final CameraPool cameraPool = new CameraPool();

    public final Event<Window> init = new Event<>();
    private final Event<Window> multiInit = new Event<>();
    public final Event<Float> preUpdate = new Event<>();
    public final Event<Float> update = new Event<>();
    public final Event<Float> postUpdate = new Event<>();

    public final Event<Pair<Camera, Float>> preRender = new Event<>();
    public final Event<Pair<Camera, Float>> render = new Event<>();
    public final Event<Pair<Camera, Float>> postRender = new Event<>();



    private boolean initilized = false;

    private final SnapshotArray<Shader> shaders = new SnapshotArray<>();
    private final SnapshotArray<Texture> textures = new SnapshotArray<>();
    private final SnapshotArray<Mesh> meshes = new SnapshotArray<>();

    private boolean active = false;


    public Scene(){
        GLFWErrorCallback.createPrint(System.err).set();
        boolean isInitialized  = GLFW.glfwInit();
        if(!isInitialized){
                System.err.println("Failed To initialized!");
                System.exit(1);
        }

        loaderWindow = createWindow(CameraType.Orthographic, "Loader", 640, 480);
        removeWindow(loaderWindow);

    }

    public void loadTexture(Texture texture){
        textures.add(texture);
        loaderWindow.makeContextCurrent();
        texture.genTexture();
        if(currentContext != null)
            currentContext.makeContextCurrent();
    }

    public void loadMesh(Mesh mesh){
        meshes.add(mesh);
        loaderWindow.makeContextCurrent();
        mesh.genMesh();
        for(Window window : windows) {
            window.makeContextCurrent();
            mesh.genMesh();
        }
        if(currentContext != null)
            currentContext.makeContextCurrent();

        multiInit.add("mesh" + mesh.id, x -> mesh.genMesh());
    }

    public void loadShader(Shader shader){
        shaders.add(shader);
        loaderWindow.makeContextCurrent();
        shader.compile();
        if(currentContext != null)
            currentContext.makeContextCurrent();
    }

    public void unloadTexture(Texture texture){
        textures.removeValue(texture, false);
        loaderWindow.makeContextCurrent();
        texture.dispose();
        if(currentContext != null)
            currentContext.makeContextCurrent();
    }

    public void unloadMesh(Mesh mesh){
        meshes.removeValue(mesh, false);
        loaderWindow.makeContextCurrent();
        mesh.disposeVAO();
        mesh.dispose();
        for(Window window : windows) {
            window.makeContextCurrent();
            mesh.disposeVAO();
        }
        if(currentContext != null)
            currentContext.makeContextCurrent();

        multiInit.remove("mesh" + mesh.id);

    }

    public void unloadShader(Shader shader){
        shaders.removeValue(shader, false);
        loaderWindow.makeContextCurrent();
        shader.dispose();
        if(currentContext != null)
            currentContext.makeContextCurrent();

    }

    public Window createWindow(CameraType cameraType, String title, int width, int height, boolean transparentFrameBuffer){

        Window w = windowPool.obtain(cameraType, title, width, height, transparentFrameBuffer);
        w.show();
        addWindow(w);
        return w;
    }

    public Window createWindow(CameraType cameraType, String title, int width, int height){
        return createWindow(cameraType, title, width, height, false);
    }


    public void Update(float delta){

        preUpdate.broadcast(delta);
        update.broadcast(delta);

        if(!initilized){
            loaderWindow.makeContextCurrent();
                init.broadcast(loaderWindow);
                initilized = true;
                if(currentContext != null)
                    currentContext.makeContextCurrent();
        }

        for(Window window : windows)
        {
            if(!window.initialised) {
                window.init.broadcast(this);
                window.initialised = true;
            }

            window.preRender.broadcast(delta);
            currentContext = window;

            window.render.broadcast(delta);
            window.postRender.broadcast(delta);
        }
        postUpdate.broadcast(delta);

    }

    public void addWindow(Window window){
        window.setScene(this);
        preUpdate.add("window" + window.id, window.preUpdate::broadcast);
        update.add("window" + window.id, window.update::broadcast);
        postUpdate.add("window" + window.id, window.postUpdate::broadcast);

        window.render.add("scene",  x -> {
            if(GLFW.glfwWindowShouldClose(window.id)) {
                removeWindow(window);
                return;
            }

            Pair<Camera, Float> renderPass = Pair.with(window.camera,x);
            preRender.broadcast(renderPass);
            render.broadcast(renderPass);
            postRender.broadcast(renderPass);
        });


        window.makeContextCurrent();
        multiInit.broadcast(window);
        if(currentContext != null)
            currentContext.makeContextCurrent();



        windows.add(window);
    }

    public void removeWindow(Window window){
        windows.removeValue(window, false);
        preUpdate.remove("window" + window.id);
        update.remove("window" + window.id);
        postUpdate.remove("window" + window.id);
        window.render.remove("scene");
        windowPool.free(window);

    }

    public Window getWindow(int index){
        return windows.get(index);
    }

    public Array<Window> getWindows(){
        return windows;
    }

    public int getWindowCount(){
        return windows.size;
    }

    @Override
    public void dispose() {
        preUpdate.dispose();
        update.dispose();
        postUpdate.dispose();
        preRender.dispose();
        render.dispose();
        postRender.dispose();

        for(Shader shader : shaders)
            unloadShader(shader);
        shaders.clear();

        for(Texture texture : textures)
            unloadTexture(texture);
        textures.clear();

        for(Mesh mesh : meshes)
           unloadMesh(mesh);
        meshes.clear();

        for(Window window : windows)
            removeWindow(window);
        windows.clear();

        windowPool.dispose();
        cameraPool.dispose();
        GLFW.glfwTerminate();
    }
}
