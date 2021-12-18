package com.alice.mel.graphics.materials;

import com.alice.mel.graphics.Material;
import com.alice.mel.graphics.shaders.Batched3DShader;

import java.io.Serializable;

public class BatchedBasic3DMaterial extends Material implements Serializable {

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

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
