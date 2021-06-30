package com.alice.mel.scenes;

import com.alice.mel.components.RenderingComponent;
import com.alice.mel.engine.*;
import com.alice.mel.graphics.CameraType;
import com.alice.mel.graphics.Mesh;
import com.alice.mel.graphics.Texture;
import com.alice.mel.graphics.Window;
import com.alice.mel.graphics.materials.Basic2DMaterial;
import com.alice.mel.graphics.materials.Basic3DMaterial;
import com.alice.mel.graphics.shaders.Basic3DShader;
import com.alice.mel.systems.RenderingSystem;
import com.alice.mel.utils.maths.MathUtils;
import com.github.sarxos.webcam.Webcam;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class ExampleScene extends SceneAdaptor {

    public ExampleScene(Game game) {
        super(game);
    }

    @Override
    public void Init(Window loaderWindow) {

        Texture texture = new Texture("src/main/resources/textures/cactus.png");
        Mesh mesh = OBJLoader.loadOBJ("src/main/resources/models/cactus.obj");

        game.assetManager.addShader(Basic3DShader.class);
        game.assetManager.addTexture("Texture1", texture);
        game.assetManager.addMesh("Mesh1", mesh);

        Window w = createWindow(CameraType.Orthographic, "Test", 640, 480, false);
        Window w2 = createWindow(CameraType.Orthographic, "Test1", 640, 480, true);

        addSystem(new RenderingSystem(game.assetManager));
        Entity en = createEntity();
        en.scale.set(100, 100, 100);
        en.position.set(0,0, -100);
        en.addComponent(new RenderingComponent(new Basic3DMaterial(), "Mesh1", "Texture1"));
        Entity en1 = createEntity();
        en1.scale.set(50, 50, 50);
        en1.position.set(500,0, -99);
        en1.addComponent(new RenderingComponent(new Basic3DMaterial(), "Mesh1", "Texture1"));


        w2.update.add("move", x -> {
            MathUtils.LookRelativeTo(w2, w);
        });
    }

    @Override
    public void PreUpdate(float deltaTime) {

    }

    @Override
    public void Update(float deltaTime) {

    }

    @Override
    public void PostUpdate(float deltaTime) {

    }

    @Override
    public void PreRender(Window currentWindow, float deltaTime) {

    }

    @Override
    public void Render(Window currentWindow, float deltaTime) {

    }

    @Override
    public void PostRender(Window currentWindow, float deltaTime) {

    }

    @Override
    public void entityAdded(Entity entity) {

    }

    @Override
    public void entityModified(Entity entity) {

    }

    @Override
    public void entityRemoved(Entity entity) {
    }
}