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
    protected void BindAttributes() {
        BindAttribute(0, "position");
        BindAttribute(1, "texCoords");
    }

    @Override
    protected void GetAllUniformLocations() {
        location_transformationMatrix = GetUniformLocation("transformationMatrix");
        location_combinedMatrix = GetUniformLocation("combinedMatrix");
    }


    public void LoadTransformationMatrix(Matrix4f matrix){
        this.LoadMatrix(location_transformationMatrix, matrix);
    }

    public void LoadCombinedMatrix(Matrix4f matrix){
        this.LoadMatrix(location_combinedMatrix, matrix);
    }




}

