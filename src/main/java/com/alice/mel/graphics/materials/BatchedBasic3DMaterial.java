package com.alice.mel.graphics.materials;

import com.alice.mel.graphics.BatchMaterial;
import com.alice.mel.graphics.BatchShader;
import com.alice.mel.graphics.shaders.Batched3DShader;

public class BatchedBasic3DMaterial extends BatchMaterial {

    public BatchedBasic3DMaterial() {
        super(Batched3DShader.class);
    }

    @Override
    protected boolean checkDirty() {
        return false;
    }

    @Override
    protected void clean() {

    }
}
