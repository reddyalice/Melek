package com.alice.mel.graphics.materials;

import com.alice.mel.components.TransformComponent;
import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.Material;
import com.alice.mel.graphics.Window;
import com.alice.mel.graphics.shaders.SpriteShader;
import com.alice.mel.utils.maths.MathUtils;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Objects;

public class SpriteMaterial extends Material {

    public String textureName;

    public SpriteMaterial(String textureName) {
        super(SpriteShader.class);
        this.textureName = textureName;
    }


    @Override
    protected boolean checkDirty() {
        return false;
    }

    @Override
    protected void clean() {

    }

}
