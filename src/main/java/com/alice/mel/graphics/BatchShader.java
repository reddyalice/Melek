package com.alice.mel.graphics;

import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Scene;

public abstract class BatchShader extends Shader{
    /**
     * @param shaderOrShaderFilePath Shader Source or File Path to the shader file
     * @param isShaderSource         Is the String give shader source
     */
    public BatchShader(String shaderOrShaderFilePath, boolean isShaderSource) {
        super(shaderOrShaderFilePath, isShaderSource);
    }

    /**
     * Loads Values to the Shader
     * @param assetManager Asset Manager Shader registered to
     * @param scene Scene shader loaded to
     * @param window Window shader rendering to
     */
    public abstract void loadValues(AssetManager assetManager, Scene scene, Window window);

}
