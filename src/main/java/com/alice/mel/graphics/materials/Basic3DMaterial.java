package com.alice.mel.graphics.materials;

import com.alice.mel.engine.Game;
import com.alice.mel.graphics.shaders.Basic3DShader;
import com.alice.mel.graphics.Material;


public class Basic3DMaterial extends Material {

    public Basic3DMaterial() {
        super(Basic3DShader.class, Game.assetManager.getMaterialBase("empty"));
    }



}
