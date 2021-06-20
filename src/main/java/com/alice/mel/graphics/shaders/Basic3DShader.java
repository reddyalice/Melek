package com.alice.mel.graphics.shaders;

import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.Camera;
import com.alice.mel.graphics.Shader;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Basic3DShader extends Shader {


    private int location_transformationMatrix;
    private int location_viewMatrix;
    private int location_projectionMatrix;
    private int location_color;
    private int location_textureOffset;
    private int location_textureSize;

    public Basic3DShader() {
        super("src/main/resources/shaders/Basic3DShader.glsl", false);
    }

    @Override
    protected void bindAttributes(Scene scene) {
        bindAttribute(scene,0, "position");
        bindAttribute(scene,1, "texCoords");
        bindAttribute(scene,2, "normal");
    }

    @Override
    protected void getAllUniformLocations(Scene scene) {
        location_transformationMatrix = getUniformLocation(scene,"transformationMatrix");
        location_viewMatrix = getUniformLocation(scene,"viewMatrix");
        location_projectionMatrix = getUniformLocation(scene,"projectionMatrix");
        location_color = getUniformLocation(scene,"color");
        location_textureOffset = getUniformLocation(scene, "textureOffset");
        location_textureSize = getUniformLocation(scene, "textureSize");
    }


    public void LoadTransformationMatrix(Matrix4f matrix){
        this.loadMatrix(location_transformationMatrix, matrix);
    }

    public void LoadCamera(Camera camera){
        this.loadMatrix(location_viewMatrix, camera.viewMatrix);
        this.loadMatrix(location_projectionMatrix, camera.projectionMatrix);
    }

    public void loadColor(Vector4f color){
        this.loadVector(location_color, color);
    }
    public void loadOffset(Vector2f offset, Vector2f size){
        this.loadVector(location_textureOffset, offset);
        this.loadVector(location_textureSize, size);
    }

}

