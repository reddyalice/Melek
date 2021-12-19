package com.alice.mel.graphics.materials;

import com.alice.mel.graphics.Material;
import com.alice.mel.graphics.MaterialData;
import com.alice.mel.graphics.shaders.Batched3DShader;

import java.io.Serializable;

public class BatchedBasic3DMaterial extends Material implements Serializable {

    public BatchedBasic3DMaterial() {
        super(Batched3DShader.class, new MaterialData() {
            @Override
            protected boolean checkDirty() {
                return false;
            }

            @Override
            protected void clean() {

            }
        });
    }


}
