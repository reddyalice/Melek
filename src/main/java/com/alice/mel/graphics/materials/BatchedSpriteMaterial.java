package com.alice.mel.graphics.materials;

import com.alice.mel.engine.Game;
import com.alice.mel.graphics.Material;
import com.alice.mel.graphics.MaterialData;
import com.alice.mel.graphics.shaders.BatchedSpriteShader;

import java.io.Serializable;

public class BatchedSpriteMaterial extends Material implements Serializable {

    public BatchedSpriteMaterial() {
        super(BatchedSpriteShader.class, Game.assetManager.getMaterialBase("empty"));
    }


}
