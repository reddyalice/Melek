package com.alice.mel.graphics;

import com.alice.mel.engine.Scene;
import com.alice.mel.utils.Disposable;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import java.nio.IntBuffer;

public class Window implements Disposable {

    public final long id;
    public final boolean transparentFrameBuffer;

    private String title;
    private Scene scene;
    private final Vector2i size;
    private final Vector2i position;

    private final IntBuffer X = BufferUtils.createIntBuffer(1);
    private final IntBuffer Y = BufferUtils.createIntBuffer(1);
    private final IntBuffer WIDTH =  BufferUtils.createIntBuffer(1);
    private final IntBuffer HEIGHT = BufferUtils.createIntBuffer(1);

    public Window(String title, int width, int height, Window shared, boolean transparentFrameBuffer){
        GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, transparentFrameBuffer ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        id = GLFW.glfwCreateWindow(width, height, title, 0, shared == null ? 0 : shared.id);
        size = new Vector2i(width, height);
        this.title = title;
        this.transparentFrameBuffer = transparentFrameBuffer;
        GLFW.glfwGetWindowPos(id, X,Y);
        position = new Vector2i(X.get(),Y.get());
        makeContextCurrent();
        GL.createCapabilities();
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

    public String getTitle(){
        return title;
    }

    public Vector2i getPosition(){
        GLFW.glfwGetWindowPos(id, X,Y);
        position.set(X.get(), Y.get());
        return position;
    }

    public Vector2i getSize(){
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
        GLFW.glfwDestroyWindow(id);
    }
}
