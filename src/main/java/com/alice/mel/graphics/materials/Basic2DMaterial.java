package com.alice.mel.graphics.materials;

import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Entity;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.Camera;
import com.alice.mel.graphics.Material;
import com.alice.mel.graphics.Shader;
import com.alice.mel.graphics.shaders.Basic2DShader;
import com.alice.mel.utils.maths.MathUtils;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Objects;

public class Basic2DMaterial extends Material {

    public String textureName;
    public final Vector2f textureOffset = new Vector2f(0,0);
    public final Vector2f textureScale = new Vector2f(1,1);
    public final Vector4f color = new Vector4f(1,1,1,1);

    public Basic2DMaterial(String textureName) {
        super(Basic2DShader.class);
        this.textureName = textureName;
        this.textureNames.add(textureName);
    }


    @Override
    public void loadValues(AssetManager assetManager, Scene scene, Camera camera) {
        Basic2DShader shader2D = (Basic2DShader) assetManager.getShader(shaderClass);
        assetManager.getTexture(textureName).bind(scene);
        shader2D.loadOffset(textureOffset, textureScale);
        shader2D.loadCamera(camera);
        shader2D.loadColor(color);
    }

    @Override
    public void loadEntity(AssetManager assetManager, Scene scene, Camera camera, Entity entity) {
        Basic2DShader shader2D = (Basic2DShader) assetManager.getShader(shaderClass);
        shader2D.loadTransformationMatrix(MathUtils.CreateTransformationMatrix(entity.position, entity.rotation, entity.scale));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Basic2DMaterial)) return false;
        Basic2DMaterial that = (Basic2DMaterial) o;
        return Objects.equals(textureOffset, that.textureOffset) && Objects.equals(textureScale, that.textureScale) && Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(textureName, textureOffset, textureScale, color);
    }
}
