package com.alice.mel.graphics;

import com.alice.mel.engine.Game;
import com.alice.mel.engine.Scene;
import com.alice.mel.utils.KeyedEvent;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

/**
 * Window data handler
 * @author Bahar Demircan
 */
public class Window {

    public final long id;
    public final boolean transparentFrameBuffer;
    public boolean initialised = false;
    public boolean active = true;

    private String title;
    private Scene scene;
    private boolean focus;
    private boolean hidden;
    private boolean enableImGui;
    private final Vector2i size;
    private final Vector2i position;

    private final IntBuffer X = BufferUtils.createIntBuffer(1);
    private final IntBuffer Y = BufferUtils.createIntBuffer(1);
    private final IntBuffer WIDTH =  BufferUtils.createIntBuffer(1);
    private final IntBuffer HEIGHT = BufferUtils.createIntBuffer(1);
    private final DoubleBuffer CURSORX = BufferUtils.createDoubleBuffer(1);
    private final DoubleBuffer CURSORY = BufferUtils.createDoubleBuffer(1);


    public final KeyedEvent<Scene> init = new KeyedEvent<>();
    public final KeyedEvent<Float> preUpdate = new KeyedEvent<>();
    public final KeyedEvent<Float> update = new KeyedEvent<>();
    public final KeyedEvent<Float> postUpdate = new KeyedEvent<>();
    public final KeyedEvent<Float> preRender = new KeyedEvent<>();
    public final KeyedEvent<Float> render = new KeyedEvent<>();
    public final KeyedEvent<Float> postRender = new KeyedEvent<>();

    public Camera camera;
    private final Vector4f backgroundColor = new Vector4f(0,0,0,0);
    /**
     * @param camera Camera that window uses
     * @param scene Scene It's created to
     * @param title Window Title
     * @param width Window width
     * @param height Window height
     * @param transparentFrameBuffer Does the Window have a transparent frame buffer
     */
    public Window(Camera camera, Scene scene, String title, int width, int height, boolean transparentFrameBuffer){

        GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, transparentFrameBuffer ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        id = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL,  scene.loaderWindow == null ? MemoryUtil.NULL :  scene.loaderWindow.id);

        size = new Vector2i(width, height);
        this.camera = camera;
        this.title = title;
        this.transparentFrameBuffer = transparentFrameBuffer;
        GLFW.glfwGetWindowPos(id, X,Y);
        position = new Vector2i(X.get(),Y.get());
        makeContextCurrent();
        this.scene = scene;

        GLFW.glfwSetWindowFocusCallback(id, new GLFWWindowFocusCallback() {
            @Override
            public void invoke(long window, boolean focused) {
                focus = focused;
            }
        });

        GLFW.glfwSetWindowCloseCallback(id, new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long window) {
                if(enableImGui) {
                    ImGui.endFrame();
                }
            }
        });


        GL.createCapabilities();

        init.add("imgui", x -> Game.imGuiImplGlfw.init(id, true));
        postUpdate.add("camera", x -> this.camera.update());
        preRender.add("makeCurrentAndClear", x -> {
            makeContextCurrent();
            if(enableImGui) Game.imGuiImplGlfw.init(id, false);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glClearColor(this.backgroundColor.x, this.backgroundColor.y, this.backgroundColor.z, this.backgroundColor.w);
            if(enableImGui) {
                Game.imGuiImplGlfw.newFrame();
                ImGui.newFrame();
            }
        });

        postRender.add("PollAndSwap", x -> {
            if(enableImGui) {
                ImGui.render();
                Game.imGuiImplGl3.renderDrawData(ImGui.getDrawData());
            }
            swapBuffers();
            GLFW.glfwPollEvents();
        });

        GLFW.glfwSetWindowSizeCallback(id, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                camera.viewportHeight = height;
                camera.viewportWidth = width;
                makeContextCurrent();
                GL11.glViewport(0,0,width,height);
                if(scene.currentContext != null)
                    scene.currentContext.makeContextCurrent();

            }
        });



        GL11.glViewport(0,0,width,height);

    }


    /**
     * Focus on the Window
     */
    public void focus(){
        GLFW.glfwFocusWindow(id);
    }

    /**
     * Show Window
     */
    public void show(){
        GLFW.glfwShowWindow(id);
        hidden = false;
    }

    /**
     * Hide Window
     */
    public void hide(){
        GLFW.glfwHideWindow(id);
        hidden = true;
    }

    /**
     * Reset Window properties
     */
    public void reset(){
        hide();
        setDecorated(true);
        setWindowOpacity(1f);
        setBackgroundColor(0,0,0,1);
        active = false;
        if(enableImGui) {
            ImGui.endFrame();
        }
        enableImGui = false;
        init.dispose();
        preUpdate.dispose();
        update.dispose();
        postUpdate.dispose();
        preRender.dispose();
        render.dispose();
        postRender.dispose();
        init.add("imgui", x -> Game.imGuiImplGlfw.init(id, true));
        postUpdate.add("camera", x -> this.camera.update());
        preRender.add("makeCurrentAndClear", x -> {
            makeContextCurrent();
            if(enableImGui) Game.imGuiImplGlfw.init(id, false);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glClearColor(this.backgroundColor.x, this.backgroundColor.y, this.backgroundColor.z, this.backgroundColor.w);
            if(enableImGui){
                Game.imGuiImplGlfw.newFrame();
                ImGui.newFrame();
            }


        });
        postRender.add("PollAndSwap", x -> {
            if(enableImGui) {
                ImGui.render();
                Game.imGuiImplGl3.renderDrawData(ImGui.getDrawData());
            }
            swapBuffers();
            GLFW.glfwPollEvents();
        });
    }

    /**
     * Make Window Context current
     */
    public void makeContextCurrent(){
        GLFW.glfwMakeContextCurrent(id);
    }

    /**
     * Swap Buffers
     */
    public void swapBuffers(){

        GLFW.glfwSwapBuffers(id);
    }

    /**
     * Translate (Move) Window
     * @param x Amount of translation horizontally
     * @param y Amount of translation vertically
     */
    public void translate(int x, int y){
        Vector2i pos = getPosition();
        pos.x += x;
        pos.y += y;
        setPosition(pos);
    }

    /**
     * Set Current scene to the window
     * @param scene
     */
    public void setScene(Scene scene){
        this.scene = scene;
    }

    /**
     * Set Window background color
     * @param r Red Component of the background color
     * @param g Green Component of the background color
     * @param b Blue Component of the background color
     * @param a Alpha Component of the background
     */
    public void setBackgroundColor(float r, float g, float b, float a){
        backgroundColor.set(r,g,b,a);
    }

    /**
     * Set the Title of the Window
     * @param title Window Title
     */
    public void setTitle(String title){
        GLFW.glfwSetWindowTitle(id, title);
        this.title = title;
    }

    /**
     * Set if window is decorated
     * @param decorated Is Decorated
     */
    public void setDecorated(boolean decorated){
        GLFW.glfwSetWindowAttrib(id, GLFW.GLFW_DECORATED, decorated ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    /**
     * Set Window Opacity
     * @param opacity Window opacity
     */
    public void setWindowOpacity(float opacity){
        GLFW.glfwSetWindowOpacity(id, opacity);
    }

    /**
     * Set Window Position
     * @param x X coordinate of the new position
     * @param y Y coordinate of the new position
     */
    public void setPosition(int x, int y){
        GLFW.glfwSetWindowPos(id, x,y);
        position.set(x,y);
    }

    /**
     * Set Window Position
     * @param position Vector of the new position
     */
    public void setPosition(Vector2i position){
        setPosition(position.x, position.y);
    }

    /**
     * Set Window Size
     * @param width New Window Width
     * @param height New Window Height
     */
    public void setSize(int width, int height){
        GLFW.glfwSetWindowSize(id, width,height);
        size.set(width,height);
    }

    /**
     * Set Window Size
     * @param size New Window size vector
     */
    public void setSize(Vector2i size){
        setSize(size.x, size.y);
    }

    /**
     * Set Cursor Position
     * @param x X coordinate of the new position
     * @param y Y coordinate of the new position
     */
    public void setCursorPosition(float x, float y){
        GLFW.glfwSetCursorPos(id, x, y);
    }

    /**
     * Set Cursor Position
     * @param cursorPosition Vector of the new position
     */
    public void setCursorPosition(Vector2f cursorPosition){
        setCursorPosition(cursorPosition.x,cursorPosition.y);
    }

    /**
     * Set Window FullScreen
     * @param monitorID ID of the Monitor to be fullcreen
     * @param width Resolution Width
     * @param height Resolution Height
     * @param freshRate Refresh Rate
     */
    public void setFullScreen(long monitorID, int width, int height, int freshRate){
        GLFW.glfwSetWindowMonitor(id, monitorID, 0,0, width, height, freshRate);
    }

    /**
     * Set Window FullScreen
     * @param monitor Monitor to be fullscreen
     * @param width Resolution Width
     * @param height Resolution Height
     * @param freshRate Refresh Rate
     */
    public void setFullScreen(Monitor monitor, int width, int height, int freshRate){
        GLFW.glfwSetWindowMonitor(id, monitor.id, 0,0, width, height, freshRate);
    }

    /**
     * Set Windowed
     * @param width Resolution Width
     * @param height Resolution Height
     * @param freshRate Refresh Rate
     */
    public void setWindowed(int width, int height, int freshRate){
        GLFW.glfwSetWindowMonitor(id, 0, 0,0, width, height, freshRate);
    }

    public void enableImGui(boolean enableImGui){
        this.enableImGui = enableImGui;
    }

    public boolean isImGuiEnabled(){
        return enableImGui;
    }

    /**
     * Get the Scene Window belongs to
     * @return Scene that window belongs to
     */
    public Scene getScene(){
        return scene;
    }

    /**
     * Get the current background color of the window
     * @return Background color of the Window
     */
    public Vector4f getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Get the current title of the window
     * @return Title of the Window
     */
    public String getTitle(){
        return title;
    }

    /**
     * Get Window position
     * @return Window Position
     */
    public Vector2i getPosition(){
        X.clear();
        Y.clear();
        GLFW.glfwGetWindowPos(id, X,Y);
        position.set(X.get(0), Y.get(0));
        return position;
    }

    /**
     * Get the current size of the window
     * @return Size of the Window
     */
    public Vector2i getSize(){
        WIDTH.clear();
        HEIGHT.clear();
        GLFW.glfwGetWindowSize(id,WIDTH,HEIGHT);
        size.set(WIDTH.get(),HEIGHT.get());
        return size;
    }

    /**
     * Get Monitor Window is in
     * @return Current MonitorID
     */
    public Monitor getMonitor(){
       return Monitor.monitors.get(GLFW.glfwGetWindowMonitor(id));
    }

    public boolean isFocused(){
        return focus;
    }

    public boolean isHidden() {
        return hidden;
    }

    /**
     * Get Cursor Position relative to the Window
     * @return Cursor Position
     */
    public Vector2f getCursorPosition(){
        GLFW.glfwGetCursorPos(id, CURSORX, CURSORY);
        return new Vector2f((float)CURSORX.get(0), (float) CURSORY.get(0));
    }

    /**
     * close the window
     */
    public void close(){
        scene.removeWindow(this);
    }

    /**
     * Dispose the window
     */
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
        GLFW.glfwDestroyWindow(id);
    }
}
