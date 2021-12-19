package com.alice.mel.graphics.materials;

import com.alice.mel.graphics.Material;
import com.alice.mel.graphics.MaterialData;
import com.alice.mel.graphics.shaders.SpriteShader;

public class SpriteMaterial extends Material {

    public String textureName;

    public SpriteMaterial(String textureName) {
        super(SpriteShader.class, new MaterialData() {
            @Override
            protected boolean checkDirty() {
                return false;
            }

            @Override
            protected void clean() {

            }
        });
        this.textureName = textureName;
    }


}
