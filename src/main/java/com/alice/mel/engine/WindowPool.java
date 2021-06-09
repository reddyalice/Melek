package com.alice.mel.engine;

import com.alice.mel.graphics.Window;
import com.alice.mel.utils.Disposable;
import com.alice.mel.utils.collections.Array;

public class WindowPool implements Disposable {

    public final int max;
    public int peak;

    private final Array<Window> freedNonTransWindows;
    private final Array<Window> freedTransWindows;


    public WindowPool () {
        this(4, Integer.MAX_VALUE);
    }

    public WindowPool (int initialCapacity) {
        this(initialCapacity, Integer.MAX_VALUE);
    }

    public WindowPool (int initialCapacity, int max) {
        freedNonTransWindows = new Array<Window>(false, initialCapacity);
        freedTransWindows = new Array<Window>(false, initialCapacity);
        this.max = max;
    }

    public Window obtain (String title, int width, int height, Window shared, boolean transparentFrameBuffer) {

        if(transparentFrameBuffer){
            if(freedTransWindows.size > 0){
                Window window = freedTransWindows.pop();
                //TODO    window.camera = camera;
                window.setTitle(title);
                window.setSize(width, height);
                window.show();
                return window;
            }else
                return new Window(title, width, height, shared, true);
        }else{
            if(freedNonTransWindows.size > 0){
                Window window = freedNonTransWindows.pop();
                //TODO  window.camera = camera;
                window.setTitle(title);
                window.setSize(width, height);
                window.show();
                return window;
            }else
                return new Window(title, width, height, shared, false);
        }
    }

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
            if (freedTransWindows.size < max) {
                freedTransWindows.add(window);
                peak = Math.max(peak, freedTransWindows.size);
                reset(window);
            } else {
                discard(window);
            }
        }
    }

    private void discard(Window window) {
        window.dispose();
    }

    private void reset(Window window) {
       window.hide();
    }

    @Override
    public void dispose() {
        for(Window window : freedNonTransWindows)
            window.dispose();

        for(Window window : freedTransWindows)
            window.dispose();

        freedNonTransWindows.clear();;
        freedTransWindows.clear();
    }
}
