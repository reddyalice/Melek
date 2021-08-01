package com.alice.mel.graphics;

import com.alice.mel.engine.Scene;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import java.util.HashMap;

public final class VertexBufferObject {

    private final HashMap<Scene, Integer> ids = new HashMap<>();
    private int drawType;

    public final int attributeIndex;
    public final VertexData vertexData;

    public VertexBufferObject(int attributeIndex, int dimension, int size) {
        this.attributeIndex = attributeIndex;
        vertexData = new VertexData(size, dimension);
    }

    public VertexBufferObject(int attributeIndex, int dimension, int size, float[] data) {
        this.attributeIndex = attributeIndex;
        vertexData = new VertexData(size, dimension, data);
    }


    public void setVertex(int offset, float[] vertex){
        vertexData.setVertex(offset, vertex);
    }

    public void genVertex(Scene scene, int drawType){
        this.drawType = drawType;
        int vboID = GL15.glGenBuffers();
        ids.put(scene, vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData.data, drawType);
        GL20.glVertexAttribPointer(attributeIndex, vertexData.dimension, GL11.GL_FLOAT, false, 0, 0);
    }

    public void registerVertex(Scene scene){
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, ids.get(scene));
        GL20.glVertexAttribPointer(attributeIndex, vertexData.dimension, GL11.GL_FLOAT, false, 0, 0);
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
            case GL15.GL_STATIC_DRAW -> GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData.data, GL15.GL_STATIC_DRAW);
            case GL15.GL_DYNAMIC_DRAW -> GL20.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, vertexData.data);
        }
    }

    public HashMap<Scene, Integer> getIDs(){
        return ids;
    }

    public int getID(Scene scene){
        return ids.get(scene);
    }

}
