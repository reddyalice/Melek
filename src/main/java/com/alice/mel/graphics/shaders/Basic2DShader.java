package com.alice.mel.graphics.shaders;

import com.alice.mel.graphics.Shader;
import org.joml.Matrix4f;

public class Basic2DShader extends Shader {


    private int location_transformationMatrix;
    private int location_combinedMatrix;



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
        location_combinedMatrix = getUniformLocation("combinedMatrix");
    }


    public void LoadTransformationMatrix(Matrix4f matrix){
        this.loadMatrix(location_transformationMatrix, matrix);
    }

    public void LoadCombinedMatrix(Matrix4f matrix){
        this.loadMatrix(location_combinedMatrix, matrix);
    }




}

