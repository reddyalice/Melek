package com.alice.mel;


import com.alice.mel.engine.*;
import com.alice.mel.graphics.CameraType;
import com.alice.mel.graphics.Mesh;
import com.alice.mel.graphics.Texture;
import com.alice.mel.graphics.Window;
import com.alice.mel.graphics.shaders.Basic2DShader;
import com.alice.mel.graphics.shaders.Basic3DShader;
import com.alice.mel.utils.maths.MathUtils;
import com.alice.mel.utils.reflections.ClassReflection;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.CallbackI;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LookingGlass {





    public static void main(String[] args) {


            Texture texture = new Texture("src/main/resources/textures/cactus.png");
            Mesh mesh = OBJLoader.loadOBJ("src/main/resources/models/cactus.obj");
            Vector4f color = new Vector4f(1,1,1,1f);
            Game game = new Game();
            game.assetManager.addShader(Basic3DShader.class);
            game.assetManager.addTexture("Texture1", texture);
            game.assetManager.addMesh("Mesh1", mesh);


            Scene s = new Scene(game);

                s.loadTexture("Texture1");
                s.loadMesh("Mesh1");
                s.loadShader(Basic3DShader.class);


                s.init.add("t", t -> {
                    Window w = s.createWindow(CameraType.Orthographic, "Test", 640, 480, false);

                    Window w2 = s.createWindow(CameraType.Orthographic, "Test1", 640, 480, true);

                    w2.update.add("move", x -> {
                        MathUtils.LookRelativeTo(w2, w);
                        if (InputHandler.getKey(s, GLFW.GLFW_KEY_A))
                            s.unloadTexture("Texture1");
                    });

                });

                s.render.add("x", x -> {
                    game.assetManager.getShader(Basic3DShader.class).start(s);
                    game.assetManager.getShader(Basic3DShader.class).LoadCamera(x.getValue0().camera);
                    game.assetManager.getShader(Basic3DShader.class).loadColor(color);
                    GL20.glEnable(GL11.GL_TEXTURE);
                    mesh.bind(s, x.getValue0());
                    GL20.glActiveTexture(GL20.GL_TEXTURE0);
                    texture.bind(s);
                    game.assetManager.getShader(Basic3DShader.class).LoadTransformationMatrix(MathUtils.CreateTransformationMatrix(new Vector3f(0,0,-100), new Vector3f(0,0,0), new Vector3f(100,100,100)));
                    GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
                    mesh.unbind();
                    GL20.glDisable(GL11.GL_TEXTURE);
                    texture.unbind();
                    game.assetManager.getShader(Basic3DShader.class).stop();
                });

        game.addActiveScene(s);
        game.Update();

    }
}
