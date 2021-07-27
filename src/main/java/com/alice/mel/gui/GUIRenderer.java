package com.alice.mel.gui;

import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.Mesh;
import com.alice.mel.graphics.Shader;
import com.alice.mel.graphics.Window;
import com.alice.mel.graphics.materials.SpriteMaterial;
import com.alice.mel.graphics.shaders.GUIShader;
import org.joml.Vector2f;

import java.util.Objects;

public class GUIRenderer {

    private final Canvas canvas;
    private final GUIShader shader;
    private final AssetManager assetManager;
    private final Scene scene;

    public GUIRenderer(AssetManager assetManager, Scene scene){
        this.scene = scene;
        this.assetManager = assetManager;
        scene.loadShader(GUIShader.class);
        scene.loadMesh("Quad");
        scene.loadTexture("null");
        shader = assetManager.getShader(GUIShader.class);
        canvas = new Canvas(scene, assetManager, shader);
    }

    public void Update(float deltaTime){
        canvas.update(deltaTime);
    }

    public void Render(Window window, float deltaTime){
        canvas.render(window, deltaTime);
    }

    public void addUIElement(UIElement element){
        canvas.addChild(element);
    }

    public void removeUIElement(UIElement element){
        canvas.removeChild(element);
    }


    private class Canvas extends UIElement{

        private Canvas(Scene scene, AssetManager assetManager, GUIShader shader){
            this.scene = scene;
            this.assetManager = assetManager;
            this.shader = shader;
        }

        private final Vector2f size = new Vector2f();
        @Override
        void render(Window window, float deltaTime) {
            shader.start(scene);
            size.set(window.getSize());
            shader.loadScreenSize(size);
            Objects.requireNonNull(assetManager.getMesh("Quad")).bind(scene, window);
            super.render(window, deltaTime);
            shader.stop();
        }

        @Override
        protected void Update(float deltaTime) { }
        @Override
        protected void Render(Window window, float deltaTime) { }
    }
}



