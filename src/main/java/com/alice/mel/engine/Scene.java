package com.alice.mel.engine;

import com.alice.mel.graphics.Window;
import com.alice.mel.utils.Disposable;
import com.alice.mel.utils.Event;
import com.alice.mel.utils.collections.SnapshotArray;

public class Scene implements Disposable {

    private final SnapshotArray<Window> windows = new SnapshotArray<Window>();
    private final WindowPool pool = new WindowPool();

    public final Event<Float> PreUpdate = new Event<Float>();
    public final Event<Float> Update = new Event<Float>();
    public final Event<Float> PostUpdate = new Event<Float>();

    public final Event<Float> PreRender = new Event<Float>();
    public final Event<Float> Render = new Event<Float>();
    public final Event<Float> PostRender = new Event<Float>();

    public Scene(){

    }

    public Window createWindow(String title, int width, int height, Window shared, boolean transparentFrameBuffer){
        Window w = pool.obtain(title, width, height, shared, transparentFrameBuffer);
        addWindow(w);
        return w;
    }

    public Window createWindow(String title, int width, int height){
        return createWindow(title, width, height, null, false);
    }

    public Window createWindow(String title, int width, int height, boolean transparentFrameBuffer){
        return createWindow(title, width, height, null, transparentFrameBuffer);
    }

    public Window createWindow(String title, int width, int height, Window shared){
        return createWindow(title, width, height, shared, false);
    }

    public void addWindow(Window w){
        w.setScene(this);
        windows.add(w);
    }

    public void removeWindow(Window w){
        pool.free(w);
        windows.removeValue(w, true);
    }

    public Window getWindow(int index){
        return windows.get(index);
    }

    public int getWindowCount(){
        return windows.size;
    }

    @Override
    public void dispose() {
        for(Window window : windows)
            window.dispose();
        pool.dispose();
    }
}
