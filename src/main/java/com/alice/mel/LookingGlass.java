package com.alice.mel;


import com.alice.mel.engine.*;
import com.alice.mel.graphics.CameraType;
import com.alice.mel.graphics.Mesh;
import com.alice.mel.graphics.Texture;
import com.alice.mel.graphics.Window;
import com.alice.mel.graphics.shaders.Basic2DShader;
import com.alice.mel.graphics.shaders.Basic3DShader;
import com.alice.mel.utils.maths.MathUtils;
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

            Basic3DShader shader = new Basic3DShader();
            Texture texture = new Texture("src/main/resources/textures/cactus.png");
            Mesh mesh = OBJLoader.loadOBJ("src/main/resources/models/cactus.obj");
            Vector4f color = new Vector4f(1,1,1,1f);
            Game game = new Game();


                Scene s = new Scene();

                s.loadTexture(texture);
                s.loadMesh(mesh);
                s.loadShader(shader);




                s.init.add("t", t -> {
                    Window w = s.createWindow(CameraType.Orthographic, "Test", 640, 480, false);

                    Window w2 = s.createWindow(CameraType.Orthographic,"Test1", 640, 480, true);
                    w2.update.add("move", x -> {
                        MathUtils.LookRelativeTo(w2, w);
                        if(InputHandler.getKey(s, GLFW.GLFW_KEY_A))
                            s.unloadTexture(texture);
                    });
                });

                    s.render.add("x", x -> {
                    shader.start(s);
                    shader.LoadCamera(x.getValue0().camera);
                    shader.loadColor(color);
                    GL20.glEnable(GL11.GL_TEXTURE);
                    mesh.bind(s, x.getValue0());
                    GL20.glActiveTexture(GL20.GL_TEXTURE0);
                    texture.bind(s);
                    shader.LoadTransformationMatrix(MathUtils.CreateTransformationMatrix(new Vector3f(0,0,-100), new Vector3f(0,0,0), new Vector3f(100,100,100)));
                    GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
                    mesh.unbind();
                    GL20.glDisable(GL11.GL_TEXTURE);
                    texture.unbind();
                    shader.stop();
                });

        game.addActiveScene(s);


        Scene s2 = new Scene();

        s2.loadTexture(texture);
        s2.loadMesh(mesh);
        s2.loadShader(shader);




        s2.init.add("t", t -> {
            Window w = s2.createWindow(CameraType.Orthographic, "Test2", 640, 480, false);

            Window w2 = s2.createWindow(CameraType.Orthographic,"Test3", 640, 480, true);
            w2.update.add("move", x -> {
                MathUtils.LookRelativeTo(w2, w);
                if(InputHandler.getKey(s2, GLFW.GLFW_KEY_A))
                    s2.unloadTexture(texture);
            });
        });

        s2.render.add("x", x -> {
            shader.start(s2);
            shader.LoadCamera(x.getValue0().camera);
            shader.loadColor(color);
            GL20.glEnable(GL11.GL_TEXTURE);
            mesh.bind(s2, x.getValue0());
            GL20.glActiveTexture(GL20.GL_TEXTURE0);
            texture.bind(s2);
            shader.LoadTransformationMatrix(MathUtils.CreateTransformationMatrix(new Vector3f(0,0,-100), new Vector3f(0,0,0), new Vector3f(100,100,100)));
            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
            mesh.unbind();
            GL20.glDisable(GL11.GL_TEXTURE);
            texture.unbind();
            shader.stop();
        });

        game.addActiveScene(s2);
        game.Update();


        s.dispose();
        s2.dispose();


        GLFW.glfwTerminate();

    }
}
