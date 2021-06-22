package com.alice.mel.graphics.materials;

import com.alice.mel.engine.Entity;
import com.alice.mel.graphics.Camera;
import com.alice.mel.graphics.Material;
import com.alice.mel.graphics.Shader;
import com.alice.mel.graphics.shaders.Basic2DShader;
import com.alice.mel.utils.maths.MathUtils;
import org.joml.Vector2f;

public class Basic2DMaterial extends Material {

    public final Vector2f textureOffset = new Vector2f(0,0);
    public final Vector2f textureScale = new Vector2f(1,1);
    public Basic2DMaterial() {
        super(Basic2DShader.class);
    }

    @Override
    public void loadValues(Shader shader, Camera camera, Entity entity) {
        Basic2DShader shader2D = (Basic2DShader) shader;
        shader2D.loadOffset(textureOffset, textureScale);
        shader2D.loadCamera(camera);
        shader2D.loadTransformationMatrix(MathUtils.CreateTransformationMatrix(entity.position, entity.rotation, entity.scale));
    }
}
