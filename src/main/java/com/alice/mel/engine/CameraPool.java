package com.alice.mel.engine;

import com.alice.mel.LookingGlass;
import com.alice.mel.graphics.Camera;
import com.alice.mel.graphics.CameraType;
import com.alice.mel.graphics.Window;
import com.alice.mel.graphics.cameras.OrthographicCamera;
import com.alice.mel.graphics.cameras.PerspectiveCamera;
import com.alice.mel.utils.Disposable;
import com.alice.mel.utils.collections.Array;

public class CameraPool implements Disposable {

    public final int max;
    public int peak;

    private final Array<Camera> freedOrthoCameras;
    private final Array<Camera> freedPersCameras;

    public CameraPool () {
        this(4, Integer.MAX_VALUE);
    }

    public CameraPool (int initialCapacity) {
        this(initialCapacity, Integer.MAX_VALUE);
    }

    public CameraPool (int initialCapacity, int max) {
        freedOrthoCameras = new Array<Camera>(false, initialCapacity);
        freedPersCameras = new Array<Camera>(false, initialCapacity);
        this.max = max;
    }

    public Camera obtain (CameraType cameraType, float viewportWidth, float viewportHeight) {
        Camera cam = null;
        switch (cameraType) {
            case Orthographic:
                if (freedOrthoCameras.size > 0)
                    cam = freedOrthoCameras.pop();
                else
                   cam = new OrthographicCamera(viewportWidth, viewportHeight);
                break;
            case Perspective:
                 if (freedPersCameras.size > 0)
                    cam = freedPersCameras.pop();
                else
                    cam = new PerspectiveCamera(67, viewportWidth, viewportHeight);
                break;
        }
        return cam;
    }

    public void free (Camera camera) {
        if (camera == null) throw new IllegalArgumentException("Camera cannot be null.");

        if(camera instanceof OrthographicCamera){
            if (freedOrthoCameras.size < max) {
                freedOrthoCameras.add(camera);
                peak = Math.max(peak, freedOrthoCameras.size);

            } else {
                discard(camera);
            }
        }else if(camera instanceof  PerspectiveCamera){
            if (freedPersCameras.size < max) {
                freedPersCameras.add(camera);
                peak = Math.max(peak, freedPersCameras.size);

            } else {
                discard(camera);
            }
        }else
            discard(camera);
    }

    private void discard(Camera camera) {
            camera.dispose();
    }


    @Override
    public void dispose() {
        for(Camera camera : freedOrthoCameras)
            camera.dispose();

        for(Camera camera : freedPersCameras)
            camera.dispose();

        freedOrthoCameras.clear();;
        freedPersCameras.clear();
    }

}
