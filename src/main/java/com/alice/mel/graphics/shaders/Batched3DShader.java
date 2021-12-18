package com.alice.mel.graphics.shaders;

import com.alice.mel.components.Component;
import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.Material;
import com.alice.mel.graphics.Shader;
import com.alice.mel.graphics.Window;
import org.joml.Matrix4f;

public class Batched3DShader extends Shader {

    private int location_viewMatrix;
    private int location_projectionMatrix;

    public Batched3DShader() {
        super("assets/shaders/Batched3DShader.glsl", false);
    }

    @Override
    protected void bindAttributes(Scene scene) {
        bindAttribute(scene, 0, "position");
        bindAttribute(scene, 1,"normals");
        bindAttribute(scene, 2, "textureCoords");
        bindAttribute(scene, 3, "color");
        bindAttribute(scene, 4, "texID");

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
    public void loadValues(Material material, Scene scene, Window window) {
        loadViewMatrix(window.camera.viewMatrix);
        loadProjectionMatrix(window.camera.projectionMatrix);
    }

    @Override
    public void loadElement(Scene scene, Window window, Component... components) {

    }

}
