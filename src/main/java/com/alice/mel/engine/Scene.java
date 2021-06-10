package com.alice.mel.engine;

import com.alice.mel.LookingGlass;
import com.alice.mel.graphics.Camera;
import com.alice.mel.graphics.CameraType;
import com.alice.mel.graphics.Window;
import com.alice.mel.utils.Disposable;
import com.alice.mel.utils.Event;
import com.alice.mel.utils.collections.SnapshotArray;
import org.javatuples.Pair;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.CallbackI;

public final class Scene implements Disposable {

    private final SnapshotArray<Window> windows = new SnapshotArray<>();

    public final Event<Window> init = new Event<>();

    public final Event<Float> preUpdate = new Event<>();
    public final Event<Float> update = new Event<>();
    public final Event<Float> postUpdate = new Event<>();

    public final Event<Pair<Camera, Float>> preRender = new Event<>();
    public final Event<Pair<Camera, Float>> render = new Event<>();
    public final Event<Pair<Camera, Float>> postRender = new Event<>();

    private boolean initilized = false;

    public Scene(){

    }

    public Window createWindow(CameraType cameraType, String title, int width, int height, boolean transparentFrameBuffer){
        Window w = LookingGlass.windowPool.obtain(cameraType, title, width, height, transparentFrameBuffer);
        addWindow(w);
        return w;
    }

    public Window createWindow(CameraType cameraType, String title, int width, int height){
        return createWindow(cameraType, title, width, height, false);
    }


    public void Update(float delta){
        preUpdate.broadcast(delta);
        update.broadcast(delta);
        postUpdate.broadcast(delta);

        for(Window window : windows)
        {

            window.preRender.broadcast(delta);


            if(window == windows.first())
                if(!initilized) {
                    init.broadcast(window);
                    initilized = true;
                }

            if(!window.initialised)
                window.init.broadcast(this);


            window.render.broadcast(delta);
            window.postRender.broadcast(delta);
        }

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

        windows.add(window);
    }

    public void removeWindow(Window window){
        windows.removeValue(window, false);
        preUpdate.remove("window" + window.id);
        update.remove("window" + window.id);
        postUpdate.remove("window" + window.id);
        window.render.remove("scene");
        LookingGlass.windowPool.free(window);

    }

    public Window getWindow(int index){
        return windows.get(index);
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
        for(Window window : windows)
            window.dispose();
        windows.clear();

    }
}
