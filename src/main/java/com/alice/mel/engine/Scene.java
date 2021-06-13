package com.alice.mel.engine;


import com.alice.mel.graphics.*;
import com.alice.mel.utils.Disposable;
import com.alice.mel.utils.Event;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.SnapshotArray;
import org.javatuples.Pair;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public final class Scene implements Disposable {

    private final SnapshotArray<Window> windows = new SnapshotArray<>();



    public final Event<Window> init = new Event<>();
    public final Event<Window> multiInit = new Event<>();
    public final Event<Float> preUpdate = new Event<>();
    public final Event<Float> update = new Event<>();
    public final Event<Float> postUpdate = new Event<>();

    public final Event<Pair<Camera, Float>> preRender = new Event<>();
    public final Event<Pair<Camera, Float>> render = new Event<>();
    public final Event<Pair<Camera, Float>> postRender = new Event<>();

    private final Event<Window> createWindow = new Event<>();
    private final Event<Window> removeWindow = new Event<>();


    private boolean initilized = false;

    public final Array<Shader> shaders = new Array<>();
    public final Array<Texture> textures = new Array<>();
    public final Array<Mesh> meshes = new Array<>();

    public Scene(){

        if(!Game.initialized){
            GLFWErrorCallback.createPrint(System.err).set();
            boolean isInitialized  = GLFW.glfwInit();
            if(!isInitialized){
                System.err.println("Failed To initialized!");
                System.exit(1);
            }
            Game.initialized = true;
        }


        init.add("load", x -> {
            for(Shader shader : shaders)
                shader.compile();

            for(Texture texture : textures)
                texture.genTexture();
        });

        multiInit.add("load", x -> {
            for(Mesh mesh :meshes)
                mesh.genMesh();
        });
    }

    public Window createWindow(CameraType cameraType, String title, int width, int height, boolean transparentFrameBuffer){

        Window w = Game.windowPool.obtain(cameraType, title, width, height, transparentFrameBuffer);
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

        for(Window window : windows)
        {

            if(!window.initialised) {
                window.init.broadcast(this);
                window.initialised = true;
            }

            window.preRender.broadcast(delta);
            Game.currentContext = window;




            if(!initilized){
                if(window == Game.loaderWindow)
                {
                    init.broadcast(window);
                    initilized = true;
                }
            }




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
        if(Game.currentContext != null)
            Game.currentContext.makeContextCurrent();



        windows.add(window);
    }

    public void removeWindow(Window window){
        windows.removeValue(window, false);
        preUpdate.remove("window" + window.id);
        update.remove("window" + window.id);
        postUpdate.remove("window" + window.id);
        window.render.remove("scene");
        Game.windowPool.free(window);

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


    public void close(){
        for(Window window : windows)
            removeWindow(window);
        dispose();
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
            shader.dispose();

        shaders.clear();

        for(Texture texture : textures)
            texture.dispose();

        textures.clear();

        for(Mesh mesh : meshes)
            mesh.dispose();

        meshes.clear();

        for(Window window : windows)
            removeWindow(window);

        windows.clear();

    }
}
