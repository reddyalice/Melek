package com.alice.mel.graphics;

import org.joml.Vector2i;

/**
 * Workarea data class for a monitor
 */
public final class Workarea {
    public final Vector2i position;
    public final Vector2i size;

    /**
     * @param position Work area position
     * @param size Work area size
     */
    public Workarea(Vector2i position, Vector2i size){
        this.position = position;
        this.size = size;
    }
}
