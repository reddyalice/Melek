package com.alice.mel.graphics.materials;

import com.alice.mel.engine.Entity;
import com.alice.mel.graphics.Camera;
import com.alice.mel.graphics.Material;
import com.alice.mel.graphics.Shader;
import com.alice.mel.graphics.shaders.Basic3DShader;
import com.alice.mel.utils.maths.MathUtils;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Objects;


public class Basic3DMaterial extends Material {

    public final Vector2f textureOffset = new Vector2f(0f,0f);
    public final Vector2f textureScale = new Vector2f(1f,1f);
    public final Vector4f color = new Vector4f(1,1,1,1);

    public Basic3DMaterial() {
        super(Basic3DShader.class);
    }

    @Override
    public void loadValues(Shader shader, Camera camera, Entity entity) {
        Basic3DShader shader3D = (Basic3DShader) shader;
        shader3D.loadOffset(textureOffset, textureScale);
        shader3D.loadCamera(camera);
        shader3D.loadColor(color);
        shader3D.loadTransformationMatrix(MathUtils.CreateTransformationMatrix(entity.position, entity.rotation, entity.scale));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Basic3DMaterial)) return false;
        Basic3DMaterial that = (Basic3DMaterial) o;
        return Objects.equals(textureOffset, that.textureOffset) && Objects.equals(textureScale, that.textureScale) && Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(textureOffset, textureScale, color);
    }
}