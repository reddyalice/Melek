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
        Texture texture1 = new Texture("src/main/resources/textures/cactus.png");
        Mesh mesh = OBJLoader.loadOBJ("src/main/resources/models/cactus.obj");
        Mesh mesh1 = Mesh.Quad;
        Scene s = new Scene();
        Scene se = new Scene();

        s.textures.add(texture);
        s.meshes.add(mesh);
        s.shaders.add(shader);

        se.shaders.add(shader);
        se.textures.add(texture1);
        se.meshes.add(mesh1);

        Game.scenes.add(s);
        Game.scenes.add(se);
        Window w = s.createWindow(CameraType.Orthographic, "Test", 640, 480, false);


        se.init.add("t", t -> {
            Window w2 = se.createWindow(CameraType.Orthographic,"Test1", 640, 480, true);
            Vector4f color = new Vector4f(1,1,1,1f);
            System.out.println("hey!");

            se.render.add("x", x -> {

                shader.start();
                shader.LoadCamera(x.getValue0());
                shader.loadColor(color);
                GL20.glEnable(GL11.GL_TEXTURE);
                mesh.bind();
                GL20.glActiveTexture(GL20.GL_TEXTURE0);
                texture.bind();
                shader.LoadTransformationMatrix(MathUtils.CreateTransformationMatrix(new Vector3f(0,0,-100), new Vector3f(0,0,0), new Vector3f(100,100,100)));
                GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
                mesh.unbind();
                GL20.glDisable(GL11.GL_TEXTURE);
                texture.unbind();
                shader.stop();
            });


        });

        Game.inputEvent.add("x", x ->
        {
            if(InputHandler.getKey(GLFW.GLFW_KEY_F))
                Game.setScene(1);
        });


        s.init.add("t", t -> {
            Window w2 = s.createWindow(CameraType.Orthographic,"Test1", 640, 480, true);
            Window w3 = s.createWindow(CameraType.Orthographic,"Test1", 640, 480, true);
            Vector4f color = new Vector4f(1,1,1,1f);
            s.render.add("x", x -> {

                shader.start();
                shader.LoadCamera(x.getValue0());
                shader.loadColor(color);
                GL20.glEnable(GL11.GL_TEXTURE);
                mesh.bind();
                GL20.glActiveTexture(GL20.GL_TEXTURE0);
                texture.bind();
                shader.LoadTransformationMatrix(MathUtils.CreateTransformationMatrix(new Vector3f(0,0,-100), new Vector3f(0,0,0), new Vector3f(100,100,100)));
                GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
                mesh.unbind();
                GL20.glDisable(GL11.GL_TEXTURE);
                texture.unbind();
                shader.stop();
            });
            w2.update.add("move", x -> MathUtils.LookRelativeTo(w2, w));
            w3.update.add("move", x -> MathUtils.LookRelativeTo(w3, w));
        });

        Game.setScene(0);
        Game.setScene(1);




        Game.run();



    }
}
