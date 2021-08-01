package com.alice.mel.graphics.materials;

import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Element;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.BatchMaterial;
import com.alice.mel.graphics.Shader;
import com.alice.mel.graphics.Window;
import com.alice.mel.graphics.shaders.BatchedSpriteShader;

public class GUIMaterial extends BatchMaterial {

    public GUIMaterial() {
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
