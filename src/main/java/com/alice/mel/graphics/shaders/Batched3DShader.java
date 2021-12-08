package com.alice.mel.graphics.shaders;

import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.BatchShader;
import com.alice.mel.graphics.Window;
import org.joml.Matrix4f;

public class Batched3DShader extends BatchShader {

    private int location_viewMatrix;
    private int location_projectionMatrix;

    public Batched3DShader() {
        super("assets/shaders/Batched3DShader.glsl", false);
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
    }

    public void loadViewMatrix(Matrix4f viewMatrix){
        loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projectionMatrix){
        loadMatrix(location_projectionMatrix, projectionMatrix);
    }

    @Override
    public void loadValues(AssetManager assetManager, Scene scene, Window window) {
        loadViewMatrix(window.camera.viewMatrix);
        loadProjectionMatrix(window.camera.projectionMatrix);
    }
}
