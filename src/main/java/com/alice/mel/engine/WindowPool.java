package com.alice.mel.engine;

import com.alice.mel.graphics.CameraType;
import com.alice.mel.graphics.Window;
import com.alice.mel.utils.collections.Array;

/**
 * A Window pool to avoid allocation
 * @author Bahar Demircan
 */
public class WindowPool {

    public final int max;
    public int peak;
    public final Scene scene;
    private final Array<Window> freedNonTransWindows;
    private final Array<Window> freedTransWindows;

    /**
     * @param scene Scene It's loaded to
     */
    public WindowPool (Scene scene) {
        this(scene,4, Integer.MAX_VALUE);
    }

    /**
     * @param scene Scene It's loaded to
     * @param initialCapacity Initial Capacity of freed Window Arrays
     */
    public WindowPool (Scene scene, int initialCapacity) {
        this(scene, initialCapacity, Integer.MAX_VALUE);
    }

    /**
     * @param scene Scene It's loaded to
     * @param initialCapacity Initial Capacity of freed Window Arrays
     * @param max Maximum Capacity of the freed Window Arrays
     */
    public WindowPool (Scene scene, int initialCapacity, int max) {
        this.scene = scene;
        freedNonTransWindows = new Array<>(false, initialCapacity);
        freedTransWindows = new Array<>(false, initialCapacity);
        this.max = max;
    }

    /**
     * Create or recycle a Window from freed ones
     * @param cameraType CameraType window camera will have
     * @param title Window Title
     * @param width Window Width
     * @param height Window Height
     * @param transparentFrameBuffer Does Window has a transparent Frame Buffer
     * @return Window that is obtained
     */
    public Window obtain (CameraType cameraType, String title, int width, int height, boolean transparentFrameBuffer) {

        if(transparentFrameBuffer){
            if(freedTransWindows.size > 0){
                Window window = freedTransWindows.pop();
                window.camera = scene.cameraPool.obtain(cameraType, width, height);
                window.setTitle(title);
                window.setSize(width, height);
                window.show();
                window.active = true;
                return window;
            }else
                return new Window( scene.cameraPool.obtain(cameraType, width, height), scene, title, width, height, true);
        }else{
            if(freedNonTransWindows.size > 0){
                Window window = freedNonTransWindows.pop();
                window.camera = scene.cameraPool.obtain(cameraType, width, height);
                window.setTitle(title);
                window.setSize(width, height);
                window.show();
                window.active = true;
                return window;
            }else
                return new Window( scene.cameraPool.obtain(cameraType, width, height), scene, title, width, height, false);
        }
    }

    /**
     * Free the Window for later use
     * @param window Window to free
     */
    public void free (Window window) {
        if (window == null) throw new IllegalArgumentException("Window cannot be null.");

        if(window.transparentFrameBuffer){
            if (freedTransWindows.size < max) {
                freedTransWindows.add(window);
                peak = Math.max(peak, freedTransWindows.size);
                reset(window);
            } else {
                discard(window);
            }
        }else{
            if (freedNonTransWindows.size < max) {
                freedNonTransWindows.add(window);
                peak = Math.max(peak, freedNonTransWindows.size);
                reset(window);
            } else {
                discard(window);
            }
        }
    }

    private void discard(Window window) {
        window.active = false;
        if(window != scene.loaderWindow)
            window.dispose();
        else
            window.hide();

        scene.cameraPool.free(window.camera);
    }

    private void reset(Window window) {
       window.reset();
        scene.cameraPool.free(window.camera);

    }

    /**
     * Dispose All freed windows and clear freed arrays
     */
    public void dispose() {
        for(Window window : freedNonTransWindows)
            window.dispose();

        for(Window window : freedTransWindows)
            window.dispose();

        freedNonTransWindows.clear();
        freedTransWindows.clear();
    }
}
