package com.alice.mel.graphics;

import com.alice.mel.engine.Scene;
import com.alice.mel.utils.collections.Array;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

public class Mesh {





    public HashMap<Scene, HashMap<Window, Integer>> ids = new HashMap<>();
    public final int vertexCount;
    public final int dimension;
    public final float[] vertices;
    public final float[] textureCoords;
    public final float[] normals;
    public final int[] indices;

    private final HashMap<Scene, Array<Integer>> VBOS = new HashMap<>();

    public Mesh(float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        this.dimension = 3;
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.vertexCount = indices.length;

    }

    public Mesh(float[] vertices, float[] textureCoords, int[] indices) {
        this.dimension = 2;
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = new float[0];
        this.indices = indices;
        this.vertexCount = indices.length;

    }

    public void genMesh(Scene scene, Window window){
        if(!VBOS.containsKey(scene))
            VBOS.put(scene, new Array<>());
        if(!ids.containsKey(scene))
            ids.put(scene, new HashMap<>());

        if(VBOS.get(scene).size == 0) {
            int id = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(id);
            storeDataInAttributeList(scene,0, dimension, vertices);
            storeDataInAttributeList(scene,1, 2, textureCoords);
            if (dimension == 3)
                storeDataInAttributeList(scene,2, dimension, normals);
            bindIndices(scene, indices);
            ids.get(scene).put(window, id);
        }else{
            int id = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(id);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOS.get(scene).get(0));
            GL20.glVertexAttribPointer(0, dimension, GL11.GL_FLOAT, false, 0, 0);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOS.get(scene).get(1));
            GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
            if(dimension == 3){
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOS.get(scene).get(2));
                GL20.glVertexAttribPointer(2, 1, GL11.GL_FLOAT, false, 0, 0);
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, VBOS.get(scene).get(3));
            }else{
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, VBOS.get(scene).get(2));
            }
            ids.get(scene).put(window, id);
        }
        GL30.glBindVertexArray(0);
    }

    public void bind(Scene scene, Window window){
        GL30.glBindVertexArray(ids.get(scene).get(window));
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        if(dimension == 3) GL20.glEnableVertexAttribArray(2);
    }

    public void unbind(){
        if(dimension == 3) GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    private void storeDataInAttributeList(Scene scene, int attributeNumber, int attributeSize, float[] data){
        int vboID = GL15.glGenBuffers();
        VBOS.get(scene).add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, attributeSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void bindIndices(Scene scene, int[] indices){
        int vboID = GL15.glGenBuffers();
        VBOS.get(scene).add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

    }

    private IntBuffer storeDataInIntBuffer(int[] data){
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data)
    {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public void disposeVAO(Scene scene, Window window){
        GL30.glDeleteVertexArrays(ids.get(scene).get(window));
        ids.get(scene).remove(window);

    }

    public void dispose(Scene scene) {
        for (int vbo : VBOS.get(scene))
            GL15.glDeleteBuffers(vbo);
    }
}
