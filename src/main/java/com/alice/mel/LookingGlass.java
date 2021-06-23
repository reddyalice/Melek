package com.alice.mel;


import com.alice.mel.components.RenderingComponent;
import com.alice.mel.engine.*;
import com.alice.mel.graphics.CameraType;
import com.alice.mel.graphics.Mesh;
import com.alice.mel.graphics.Texture;
import com.alice.mel.graphics.Window;
import com.alice.mel.graphics.materials.Basic2DMaterial;
import com.alice.mel.graphics.materials.Basic3DMaterial;
import com.alice.mel.graphics.shaders.Basic2DShader;
import com.alice.mel.graphics.shaders.Basic3DShader;
import com.alice.mel.systems.RenderingSystem;
import com.alice.mel.utils.maths.MathUtils;
import com.alice.mel.utils.reflections.ClassReflection;
import com.github.sarxos.webcam.Webcam;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.CallbackI;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LookingGlass {





    public static void main(String[] args) {


            //Texture texture = new Texture("src/main/resources/textures/cactus.png");
            //Mesh mesh = OBJLoader.loadOBJ("src/main/resources/models/cactus.obj");
            Vector4f color = new Vector4f(1,1,1,1f);
            Game game = new Game();
            game.assetManager.addShader(Basic2DShader.class);
            Webcam webcam = Webcam.getDefault();
            webcam.open();
            Texture texture = new Texture(webcam.getImage());

            game.assetManager.addTexture("Texture1", texture);


            Scene s = new Scene(game);
        s.addSystem(new RenderingSystem(game.assetManager));
            Entity en = s.createEntity();
            en.scale.set(200, 200, 200);
            en.position.set(0,0, -100);
            en.addComponent(new RenderingComponent(new Basic2DMaterial(), "Quad", "Texture1"));
            Entity en1 = s.createEntity();
            en1.scale.set(100, 100, 100);
            en1.position.set(500,0, -99);
            en1.addComponent(new RenderingComponent(new Basic2DMaterial(), "Quad", "Texture1"));

            en.addToScene();
            en1.addToScene();



            s.init.add("t", t -> {
                Window w = s.createWindow(CameraType.Orthographic, "Test", 640, 480, false);

                Window w2 = s.createWindow(CameraType.Orthographic, "Test1", 640, 480, true);

                w2.update.add("move", x -> {
                    MathUtils.LookRelativeTo(w2, w);
                    if (InputHandler.getKey(s, GLFW.GLFW_KEY_A)) {
                        s.unloadTexture("Texture1");
                        Texture ter = new Texture(webcam.getImage());
                        game.assetManager.removeTexture("Texture1");
                        game.assetManager.addTexture("Texture1", ter);
                        s.loaderWindow.makeContextCurrent();
                        s.loadTexture("Texture1");
                        if (s.currentContext != null)
                            s.currentContext.makeContextCurrent();
                    }

                });

            });



        game.addActiveScene(s);
        game.Update();

    }
}
