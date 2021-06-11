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
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LookingGlass {

    public static final WindowPool windowPool = new WindowPool();
    public static final CameraPool cameraPool = new CameraPool();

    private static final int coreCount = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService executor = Executors.newFixedThreadPool(coreCount);
    public static Window loaderWindow = null;
    public static Window currentContext = null;



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
        //Mesh mesh = Mesh.Quad;


        Scene s = new Scene();

        s.init.add("load", x -> {
            texture.genTexture();
            shader.compile();

        });

        s.multiInit.add("mesh", x -> mesh.genMesh());

        Window w = s.createWindow(CameraType.Orthographic, "Test", 640, 480, false);
        Window w2 = s.createWindow(CameraType.Orthographic,"Test1", 640, 480, true);

        Vector4f color = new Vector4f(1,1,1,0.5f);
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


        w2.update.add("move", x -> {

            if(w.active) {
                Vector2i wPos = w.getPosition();
                Vector2i w2Pos = w2.getPosition();
                Vector2i wSize = w.getSize();
                Vector2i w2Size = w2.getSize();


                float xDistance = (w2Pos.x + w2Size.x / 2f) - (wPos.x + wSize.x / 2f);
                float yDistance = (w2Pos.y + w2Size.y / 2f) - (wPos.y + wSize.y / 2f);

                w2.camera.position.set(w.camera.position).add(xDistance, -yDistance, 0);
                //System.out.println(w2.camera.position.x + " " + w2.camera.position.y);
            }
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
