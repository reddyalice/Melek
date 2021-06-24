package com.alice.mel.engine;

import com.alice.mel.graphics.Camera;
import com.alice.mel.graphics.CameraType;
import com.alice.mel.graphics.cameras.OrthographicCamera;
import com.alice.mel.graphics.cameras.PerspectiveCamera;
import com.alice.mel.utils.collections.Array;

/**
 * A Camera pool to avoid allocation
 * @author Bahar Demircan
 */
public final class CameraPool {

    public final int max;
    public int peak;

    private final Array<Camera> freedOrthoCameras;
    private final Array<Camera> freedPersCameras;


    public CameraPool () {
        this(4, Integer.MAX_VALUE);
    }

    /**
     * @param initialCapacity Initial capacity for freed Arrays
     */
    public CameraPool (int initialCapacity) {
        this(initialCapacity, Integer.MAX_VALUE);
    }

    /**
     * @param initialCapacity Initial capacity for freed Arrays
     * @param max Maximum capacity for freed Arrays
     */
    public CameraPool (int initialCapacity, int max) {
        freedOrthoCameras = new Array<>(false, initialCapacity);
        freedPersCameras = new Array<>(false, initialCapacity);
        this.max = max;
    }

    /**
     * Create or get a camera from freed ones
     * @param cameraType Type of the Camera (Perspective or Orthographic)
     * @param viewportWidth Width of the Viewport
     * @param viewportHeight Height of the Viewport
     * @return Camera that is obtained
     */
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

    /**
     * Free the camera that isn't used for recycling
     * @param camera Camera that will be freed
     */
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

    /**
     * Dispose all the freed Cameras and clear arrays
     */
    public void dispose() {
        for(Camera camera : freedOrthoCameras)
            camera.dispose();

        for(Camera camera : freedPersCameras)
            camera.dispose();

        freedOrthoCameras.clear();
        freedPersCameras.clear();
    }

}
