package com.alice.mel.scenes;

import com.alice.mel.components.RenderingComponent;
import com.alice.mel.engine.Entity;
import com.alice.mel.engine.Game;
import com.alice.mel.engine.InputHandler;
import com.alice.mel.engine.SceneAdaptor;
import com.alice.mel.graphics.CameraType;
import com.alice.mel.graphics.Window;
import com.alice.mel.graphics.materials.Basic2DMaterial;
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
        Webcam webcam = Webcam.getDefault();
        webcam.open();

        scene.addSystem(new RenderingSystem(game.assetManager));
        Entity en = scene.createEntity();
        en.scale.set(200, 200, 200);
        en.position.set(0,0, -100);
        en.addComponent(new RenderingComponent(new Basic2DMaterial(), "Quad", "Texture1"));
        Entity en1 = scene.createEntity();
        en1.scale.set(100, 100, 100);
        en1.position.set(500,0, -99);
        en1.addComponent(new RenderingComponent(new Basic2DMaterial(), "Quad", "Texture1"));
        en.addToScene();
        en1.addToScene();
        Window w = scene.createWindow(CameraType.Orthographic, "Test", 640, 480, false);

        Window w2 = scene.createWindow(CameraType.Orthographic, "Test1", 640, 480, true);

        w2.update.add("move", x -> {
            MathUtils.LookRelativeTo(w2, w);
            if (InputHandler.getKey(scene, GLFW.GLFW_KEY_A)) {
                Objects.requireNonNull(game.assetManager.getTexture("Texture1")).regenTexture(scene, webcam.getImage());
            }

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
}
