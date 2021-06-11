package com.alice.mel.graphics;

import com.alice.mel.LookingGlass;
import com.alice.mel.engine.Scene;
import com.alice.mel.utils.Disposable;
import com.alice.mel.utils.Event;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.nio.IntBuffer;

public class Window implements Disposable {

    public final long id;
    public final boolean transparentFrameBuffer;
    public boolean initialised = false;
    public boolean active = true;

    private String title;
    private Scene scene;
    private final Vector2i size;
    private final Vector2i position;

    private final IntBuffer X = BufferUtils.createIntBuffer(1);
    private final IntBuffer Y = BufferUtils.createIntBuffer(1);
    private final IntBuffer WIDTH =  BufferUtils.createIntBuffer(1);
    private final IntBuffer HEIGHT = BufferUtils.createIntBuffer(1);

    public final Event<Scene> init = new Event<>();
    public final Event<Float> preUpdate = new Event<>();
    public final Event<Float> update = new Event<>();
    public final Event<Float> postUpdate = new Event<>();
    public final Event<Float> preRender = new Event<>();
    public final Event<Float> render = new Event<>();
    public final Event<Float> postRender = new Event<>();

    public Camera camera;
    private final Vector4f backgroundColor = new Vector4f(0,0,0,0);


    public Window(Camera camera, String title, int width, int height, boolean transparentFrameBuffer){
        GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, transparentFrameBuffer ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        id = GLFW.glfwCreateWindow(width, height, title, 0,  LookingGlass.loaderWindow == null ? 0 :  LookingGlass.loaderWindow.id);

        size = new Vector2i(width, height);
        this.camera = camera;
        this.title = title;
        this.transparentFrameBuffer = transparentFrameBuffer;
        GLFW.glfwGetWindowPos(id, X,Y);
        position = new Vector2i(X.get(),Y.get());
        makeContextCurrent();

        postUpdate.add("camera", x -> this.camera.update());
        preRender.add("makeCurrentAndClear", x -> {
             makeContextCurrent();
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glClearColor(this.backgroundColor.x, this.backgroundColor.y, this.backgroundColor.z, this.backgroundColor.w);
        });
        postRender.add("PollAndSwap", x -> {
            swapBuffers();
            GLFW.glfwPollEvents();
        });
        GL.createCapabilities();
        if(LookingGlass.loaderWindow == null) {
            LookingGlass.loaderWindow = this;

        }

        GLFW.glfwSetWindowSizeCallback(id, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                camera.viewportHeight = height;
                camera.viewportWidth = width;
                makeContextCurrent();
                GL11.glViewport(0,0,width,height);
                if(LookingGlass.currentContext != null)
                    LookingGlass.currentContext.makeContextCurrent();

            }
        });
        GL11.glViewport(0,0,640,480);
    }


    public void focus(){
        GLFW.glfwFocusWindow(id);
    }

    public void show(){
        GLFW.glfwShowWindow(id);
    }

    public void hide(){
        GLFW.glfwHideWindow(id);
    }

    public void makeContextCurrent(){
        GLFW.glfwMakeContextCurrent(id);
    }

    public void swapBuffers(){
        GLFW.glfwSwapBuffers(id);
    }

    public void translate(int x, int y){
        Vector2i pos = getPosition();
        pos.x += x;
        pos.y += y;
        setPosition(pos);
    }

    public void setScene(Scene scene){
        this.scene = scene;
    }

    public void setBackgroundColor(float r, float g, float b, float a){
        backgroundColor.set(r,g,b,a);
    }

    public void setTitle(String title){
        GLFW.glfwSetWindowTitle(id, title);
        this.title = title;
    }

    public void setDecorated(boolean decorated){
        GLFW.glfwSetWindowAttrib(id, GLFW.GLFW_DECORATED, decorated ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    public void setWindowOpacity(float opacity){
        GLFW.glfwSetWindowOpacity(id, opacity);
    }

    public void setPosition(int x, int y){
        GLFW.glfwSetWindowPos(id, x,y);
        position.set(x,y);
    }

    public void setPosition(Vector2i position){
        setPosition(position.x, position.y);
    }

    public void setSize(int width, int height){
        GLFW.glfwSetWindowSize(id, width,height);
        size.set(width,height);
    }

    public void setSize(Vector2i size){
        setSize(size.x, size.y);
    }

    public Scene getScene(){
        return scene;
    }

    public Vector4f getBackgroundColor() {
        return backgroundColor;
    }

    public String getTitle(){
        return title;
    }

    public Vector2i getPosition(){
        X.clear();
        Y.clear();
        GLFW.glfwGetWindowPos(id, X,Y);
        position.set(X.get(0), Y.get(0));
        return position;
    }

    public Vector2i getSize(){
        WIDTH.clear();
        HEIGHT.clear();
        GLFW.glfwGetWindowSize(id,WIDTH,HEIGHT);
        size.set(WIDTH.get(),HEIGHT.get());
        return size;
    }

    @Override
    public void dispose() {
        X.clear();
        Y.clear();
        WIDTH.clear();
        HEIGHT.clear();
        preUpdate.dispose();
        update.dispose();
        postUpdate.dispose();
        preRender.dispose();
        render.dispose();
        postRender.dispose();
    }
}
