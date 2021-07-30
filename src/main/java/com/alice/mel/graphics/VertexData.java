package com.alice.mel.graphics;

import com.alice.mel.engine.Scene;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import java.util.HashMap;

public final class VertexData{

    private final HashMap<Scene, Integer> ids = new HashMap<>();
    private int drawType;

    public final int attributeIndex;
    public final int size;
    public final int dimension;
    public final int length;
    public final float[] data;

    public VertexData(int attributeIndex, int dimension, int size) {

        this.attributeIndex = attributeIndex;
        this.dimension = dimension;
        this.size = size;
        this.length = size * dimension;
        this.data = new float[length];

    }

    public VertexData(int attributeIndex, int dimension, int size, float[] data) {
        this.attributeIndex = attributeIndex;
        this.dimension = dimension;
        this.size = size;
        this.length = size * dimension;
        this.data = new float[length];
        System.arraycopy(data, 0, this.data, 0, this.data.length);
    }


    public void setVertex(int offset, float[] vertex){
        if(vertex.length >= dimension){
            int index = dimension * offset;
            if (index < length - dimension) {
                if (dimension >= 0) System.arraycopy(vertex, 0, data, index, dimension);
            }else
                System.err.println("There is no space left in the Vertex");
        }else
            System.err.println("Vertex Length has to be at least " + dimension);
    }

    public void genVertex(Scene scene, int drawType){
        this.drawType = drawType;
        int vboID = GL15.glGenBuffers();
        ids.put(scene, vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, drawType);
        GL20.glVertexAttribPointer(attributeIndex, dimension, GL11.GL_FLOAT, false, 0, 0);
    }

    public void registerVertex(Scene scene){
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, ids.get(scene));
        GL20.glVertexAttribPointer(attributeIndex, dimension, GL11.GL_FLOAT, false, 0, 0);
    }

    public void enable(){
        GL20.glEnableVertexAttribArray(attributeIndex);
    }

    public void disable(){
        GL20.glDisableVertexAttribArray(attributeIndex);
    }

    public void delete(Scene scene){
        GL15.glDeleteBuffers(ids.get(scene));
    }

    public void regenVertex(Scene scene){
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, ids.get(scene));
        switch (drawType) {
            case GL15.GL_STATIC_DRAW -> GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
            case GL15.GL_DYNAMIC_DRAW -> GL20.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, data);
        }
    }

    public int getID(Scene scene){
        return ids.get(scene);
    }

}
