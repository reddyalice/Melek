package com.alice.mel.graphics.materials;

import com.alice.mel.graphics.Material;
import com.alice.mel.graphics.shaders.BatchedSpriteShader;

import java.io.Serializable;

public class BatchedSpriteMaterial extends Material implements Serializable {

    public BatchedSpriteMaterial() {
        super(BatchedSpriteShader.class);
    }

    @Override
    protected boolean checkDirty() {
        return false;
    }

    @Override
    protected void clean() {

    }
}
