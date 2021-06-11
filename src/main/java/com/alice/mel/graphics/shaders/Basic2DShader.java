package com.alice.mel.graphics.shaders;

import com.alice.mel.graphics.Camera;
import com.alice.mel.graphics.Shader;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class Basic2DShader extends Shader {


    private int location_transformationMatrix;
    private int location_viewMatrix;
    private int location_projectionMatrix;
    private int location_color;



    public Basic2DShader() {
        super("src/main/resources/shaders/Basic2DShader.glsl", false);
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");
        bindAttribute(1, "texCoords");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = getUniformLocation("transformationMatrix");
        location_viewMatrix = getUniformLocation("viewMatrix");
        location_projectionMatrix = getUniformLocation("projectionMatrix");
        location_color = getUniformLocation("color");
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




}

