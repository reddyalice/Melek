package com.alice.mel.graphics.shaders;

import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.Shader;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL45;

public class BatchedSpriteShader extends Shader {

    private int location_viewMatrix;
    private int location_projectionMatrix;
    private int location_textureSampler;

    public BatchedSpriteShader() {
        super("src/main/java/com/alice/mel/graphics/shaders/BatchedSpriteShader.glsl", false);

    }

    @Override
    protected void bindAttributes(Scene scene) {
        bindAttribute(scene, 0, "position");
        bindAttribute(scene, 1, "textureCoords");
        bindAttribute(scene, 2, "color");
        bindAttribute(scene, 3, "texID");
    }

    @Override
    public void compile(Scene scene) {
        super.compile(scene);
    }

    @Override
    protected void getAllUniformLocations(Scene scene) {
        location_viewMatrix = getUniformLocation(scene, "viewMatrix");
        location_projectionMatrix = getUniformLocation(scene, "projectionMatrix");
        location_textureSampler = getUniformLocation(scene, "textureSampler");
    }

    public void loadViewMatrix(Matrix4f viewMatrix){
        loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projectionMatrix){
        loadMatrix(location_projectionMatrix, projectionMatrix);
    }

}
