package com.alice.mel;


import com.alice.mel.engine.CameraPool;
import com.alice.mel.engine.OBJLoader;
import com.alice.mel.engine.Scene;
import com.alice.mel.engine.WindowPool;
import com.alice.mel.graphics.CameraType;
import com.alice.mel.graphics.Mesh;
import com.alice.mel.graphics.Texture;
import com.alice.mel.graphics.Window;
import com.alice.mel.graphics.shaders.Basic2DShader;
import com.alice.mel.graphics.shaders.Basic3DShader;
import com.alice.mel.utils.maths.MathUtils;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.CallbackI;

import java.nio.IntBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LookingGlass {

    public static final WindowPool windowPool = new WindowPool();
    public static final CameraPool cameraPool = new CameraPool();

    private static final int coreCount = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService executor = Executors.newFixedThreadPool(coreCount);
    public static Window firstWindow = null;

    public static void main(String[] args) {
        GLFWErrorCallback.createPrint(System.err).set();
        boolean isInitialized  = GLFW.glfwInit();
        if(!isInitialized){
            System.err.println("Failed To initialized!");
            System.exit(1);
        }

        Basic3DShader shader = new Basic3DShader();
        Texture texture = new Texture("src/main/resources/textures/cactus.png");

        Mesh mesh = OBJLoader.loadOBJ("src/main/resources/models/cactus.obj");

        /*Mesh mesh = new Mesh(
                new float[]{
                     -0.5f, 0.5f, 0,
                    0.5f,  0.5f, 0,
                    0.5f,  -0.5f, 0,
                   -0.5f, -0.5f, 0,
                },
                new float[]{
                        0, 0,
                        1,  0,
                        1,  1,
                        0, 1
                },
                new float[]{},
                new int[]{
                    0,1,2,2,3,0
                }
        );*/

        Scene s = new Scene();

        s.init.add("load", x -> {
            texture.genTexture();
            shader.compile();
            mesh.genMesh();
        });


        s.render.add("x", x -> {

            x.getValue0().update();
            shader.start();
            shader.LoadCamera(x.getValue0());
            shader.LoadTransformationMatrix(MathUtils.CreateTransformationMatrix(new Vector3f(0,0,-10), new Vector3f(0,0,0), new Vector3f(100,100,100)));

            GL20.glEnable(GL11.GL_TEXTURE);
            GL20.glActiveTexture(GL20.GL_TEXTURE0);
            texture.bind();

            mesh.bind();
            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
            mesh.unbind();
            GL20.glDisable(GL11.GL_TEXTURE);
            texture.unbind();
            shader.stop();
        });


        Window w = s.createWindow(CameraType.Perspective, "Test", 640, 480, false);


        Window w2 = s.createWindow(CameraType.Perspective,"Test1", 640, 480, false);
        w2.init.add("mesh", x -> mesh.genMesh());
        w2.update.add("move", x -> {
            Vector2i diff = new Vector2i(w2.getPosition());
            diff.sub(w.getPosition());
            w2.camera.position.set(w.camera.position).add(diff.x, diff.y, 0);
        });


        while(s.getWindowCount() > 0){
            s.Update(1f/60f);
        }

        s.dispose();
        windowPool.dispose();
        cameraPool.dispose();
        GLFW.glfwTerminate();


    }
}
