package com.alice.mel.engine;


import com.alice.mel.components.ComponentType;
import com.alice.mel.graphics.*;
import com.alice.mel.utils.Event;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.SnapshotArray;
import com.alice.mel.utils.maths.MathUtils;
import com.alice.mel.utils.reflections.ClassReflection;
import com.alice.mel.utils.reflections.ReflectionException;
import org.javatuples.Pair;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.HashMap;
import java.util.Objects;

public final class Scene {


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

    public final Event<Pair<Window, Float>> preRender = new Event<>();
    public final Event<Pair<Window, Float>> render = new Event<>();
    public final Event<Pair<Window, Float>> postRender = new Event<>();



    private boolean initilized = false;

    private final SnapshotArray<Class<? extends Shader>> shaders = new SnapshotArray<>();
    private final SnapshotArray<String> textures = new SnapshotArray<>();
    private final SnapshotArray<String> meshes = new SnapshotArray<>();
    private final HashMap<ComponentType, Entity> componentEntityMap = new HashMap<ComponentType, Entity>();



    private final Game game;

    public Scene(Game game){
        this.game = game;
        if(game.loaderScene == null) {
            GLFWErrorCallback.createPrint(System.err).set();
            boolean isInitialized = GLFW.glfwInit();
            if (!isInitialized) {
                System.err.println("Failed To initialized!");
                System.exit(1);
            }
            game.loaderScene = this;
        }

        loaderWindow = createWindow(CameraType.Orthographic, "Loader", 640, 480);
        removeWindow(loaderWindow);

    }

    public void loadTexture(String name){
        Texture texture = game.assetManager.getTexture(name);
        assert texture != null;
        if(!textures.contains(name, false)) {
            textures.add(name);
            loaderWindow.makeContextCurrent();
            texture.genTexture(this);
            if (currentContext != null)
                currentContext.makeContextCurrent();
        }else
            System.err.println("Texture already loaded!");
    }

    public void loadMesh(String name){
        Mesh mesh = game.assetManager.getMesh(name);
        assert mesh != null;
        if(!meshes.contains(name, false)) {
            meshes.add(name);
            loaderWindow.makeContextCurrent();
            mesh.genMesh(this, loaderWindow);
            for (Window window : windows) {
                window.makeContextCurrent();
                mesh.genMesh(this, window);
            }
            if (currentContext != null)
                currentContext.makeContextCurrent();

            multiInit.add("mesh" + mesh.ids.get(this).get(loaderWindow), x -> mesh.genMesh(this, x));
        }else
            System.err.println("Mesh already loaded!");
    }

    public void loadShader(Class<? extends Shader> shaderClass){
        Shader shader = game.assetManager.getShader(shaderClass);
        assert shader != null;
        if(!shaders.contains(shaderClass, false)) {
            shaders.add(shaderClass);
            loaderWindow.makeContextCurrent();
            shader.compile(this);
            if (currentContext != null)
                currentContext.makeContextCurrent();
        }else
            System.err.println("Shader already loaded!");
    }

    public void unloadTexture(String name){
        Texture texture = game.assetManager.getTexture(name);
        assert texture != null;
        if(textures.contains(name, false)) {
            textures.removeValue(name, false);
            loaderWindow.makeContextCurrent();
            texture.dispose(this);
            if (currentContext != null)
                currentContext.makeContextCurrent();
        }else
            System.err.println("No such Texture already loaded!");
    }

    public void unloadMesh(String name){
        Mesh mesh = game.assetManager.getMesh(name);
        assert mesh != null;
        if(meshes.contains(name, false)) {
            meshes.removeValue(name, false);
            loaderWindow.makeContextCurrent();
            mesh.disposeVAO(this, loaderWindow);
            mesh.dispose(this);
            for (Window window : windows) {
                window.makeContextCurrent();
                mesh.disposeVAO(this, window);
            }
            if (currentContext != null)
                currentContext.makeContextCurrent();

            multiInit.remove("mesh" + mesh.ids.get(this).get(loaderWindow));
        }else
            System.err.println("No such Mesh already loaded!");

    }

    public void unloadShader(Class<? extends Shader> shaderClass){
        Shader shader = game.assetManager.getShader(shaderClass);
        assert shader != null;
        if(shaders.contains(shaderClass, false)) {
            shaders.removeValue(shaderClass, false);
            loaderWindow.makeContextCurrent();
            shader.dispose(this);
            if (currentContext != null)
                currentContext.makeContextCurrent();
        }else
            System.err.println("No such Shader already loaded!");

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

        if(!initilized){
            loaderWindow.makeContextCurrent();
            init.broadcast(loaderWindow);
            initilized = true;
            if(currentContext != null)
                currentContext.makeContextCurrent();
        }


        preUpdate.broadcast(delta);
        update.broadcast(delta);



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

    private void addWindow(Window window){
        window.setScene(this);
        preUpdate.add("window" + window.id, window.preUpdate::broadcast);
        update.add("window" + window.id, window.update::broadcast);
        postUpdate.add("window" + window.id, window.postUpdate::broadcast);

        window.render.add("scene",  x -> {
            if(GLFW.glfwWindowShouldClose(window.id)) {
                removeWindow(window);
                return;
            }

            Pair<Window, Float> renderPass = Pair.with(window,x);
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

    public void dispose() {
        preUpdate.dispose();
        update.dispose();
        postUpdate.dispose();
        preRender.dispose();
        render.dispose();
        postRender.dispose();

        for(Class<? extends Shader> shader : shaders)
            unloadShader(shader);


        shaders.clear();

        for(String texture : textures)
            unloadTexture(texture);
        textures.clear();

        for(String mesh : meshes)
           unloadMesh(mesh);
        meshes.clear();

        for(Window window : windows)
            removeWindow(window);
        windows.clear();

        windowPool.dispose();
        cameraPool.dispose();
    }

}
