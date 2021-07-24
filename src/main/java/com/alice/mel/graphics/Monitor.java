package com.alice.mel.graphics;


import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

/**
 * Monitor Data carrier class
 * @author Bahar Demircan
 */
public final class Monitor {

    public static final HashMap<Long, Monitor> monitors = new HashMap<>();
    public final long id;
    public final String name;
    public final Workarea workarea;
    public final Vector2f contentScale;
    public final Vector2i position;
    public final Vector2i physicalSize;

    /**
     * Register a monitor with an ID
     * @param id Monitor ID
     */
    public Monitor(long id)
    {
        this.id = id;
        this.name = GLFW.glfwGetMonitorName(id);
        IntBuffer XPOS = BufferUtils.createIntBuffer(1);
        IntBuffer YPOS = BufferUtils.createIntBuffer(1);
        IntBuffer WIDTH = BufferUtils.createIntBuffer(1);
        IntBuffer HEIGHT = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetMonitorWorkarea(id, XPOS, YPOS, WIDTH, HEIGHT);
        this.workarea = new Workarea(new Vector2i(XPOS.get(0), YPOS.get(0)), new Vector2i(WIDTH.get(0), HEIGHT.get(0)));

        FloatBuffer SCALEX = BufferUtils.createFloatBuffer(1);
        FloatBuffer SCALEY = BufferUtils.createFloatBuffer(1);
        GLFW.glfwGetMonitorContentScale(id, SCALEX, SCALEY);
        contentScale = new Vector2f(SCALEX.get(0), SCALEY.get(0));

        XPOS.clear();
        YPOS.clear();
        WIDTH.clear();
        HEIGHT.clear();

        GLFW.glfwGetMonitorPos(id, XPOS, YPOS);
        position = new Vector2i(XPOS.get(0), YPOS.get(0));

        GLFW.glfwGetMonitorPhysicalSize(id, WIDTH, HEIGHT);
        physicalSize = new Vector2i(WIDTH.get(0), HEIGHT.get(0));

        monitors.put(id, this);
    }










}
