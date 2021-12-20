package com.alice.mel.graphics.materials;

import com.alice.mel.engine.Game;
import com.alice.mel.graphics.Material;
import com.alice.mel.graphics.MaterialData;
import com.alice.mel.graphics.shaders.SpriteShader;

public class SpriteMaterial extends Material {

    public SpriteMaterial() {
        super(SpriteShader.class, Game.assetManager.getMaterialBase("empty"));
    }


}
