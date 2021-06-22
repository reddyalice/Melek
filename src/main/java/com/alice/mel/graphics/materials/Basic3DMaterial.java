package com.alice.mel.graphics.materials;

import com.alice.mel.engine.Entity;
import com.alice.mel.graphics.Camera;
import com.alice.mel.graphics.Material;
import com.alice.mel.graphics.Shader;
import com.alice.mel.graphics.shaders.Basic3DShader;
import com.alice.mel.utils.maths.MathUtils;
import org.joml.Vector2f;

public class Basic3DMaterial extends Material {

    public final Vector2f textureOffset = new Vector2f(0f,0f);
    public final Vector2f textureScale = new Vector2f(1f,1f);
    public Basic3DMaterial() {
        super(Basic3DShader.class);
    }

    @Override
    public void loadValues(Shader shader, Camera camera, Entity entity) {
        Basic3DShader shader3D = (Basic3DShader) shader;
        shader3D.loadOffset(textureOffset, textureScale);
        shader3D.loadCamera(camera);
        shader3D.loadTransformationMatrix(MathUtils.CreateTransformationMatrix(entity.position, entity.rotation, entity.scale));
    }
}
