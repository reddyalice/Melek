package com.alice.mel.graphics;

import com.alice.mel.LookingGlass;
import com.alice.mel.utils.Disposable;
import com.alice.mel.utils.collections.Array;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh implements Disposable {

    public static Mesh Quad = new Mesh(
            new float[]{
                        -0.5f, 0.5f,
                        0.5f,  0.5f,
                        0.5f,  -0.5f,
                        -0.5f, -0.5f
            },
            new float[]{
                    0, 0,
                    1,  0,
                    1,  1,
                    0, 1
            },
            new int[]{
                    0,1,2,
                    2,3,0
            }
            );



    public int id = 0;
    public final int vertexCount;
    public final int dimension;
    public final float[] vertices;
    public final float[] textureCoords;
    public final float[] normals;
    public final int[] indices;

    private final Array<Integer> VBOS = new Array<>();

    public Mesh(float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        this.dimension = 3;
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.vertexCount = indices.length;
        VBOS.ordered = true;
    }

    public Mesh(float[] vertices, float[] textureCoords, int[] indices) {
        this.dimension = 2;
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = new float[0];
        this.indices = indices;
        this.vertexCount = indices.length;
        VBOS.ordered = true;
    }

    public void genMesh(){
        if(VBOS.size == 0) {
            id = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(id);
            storeDataInAttributeList(0, dimension, vertices);
            storeDataInAttributeList(1, 2, textureCoords);
            if (dimension == 3)
                storeDataInAttributeList(2, dimension, normals);
            bindIndices(indices);
        }else{
            id = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(id);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOS.get(0));
            GL20.glVertexAttribPointer(0, dimension, GL11.GL_FLOAT, false, 0, 0);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOS.get(1));
            GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
            if(dimension == 3){
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOS.get(2));
                GL20.glVertexAttribPointer(2, 1, GL11.GL_FLOAT, false, 0, 0);
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, VBOS.get(3));
            }else{
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, VBOS.get(2));
            }

        }
        GL30.glBindVertexArray(0);
    }

    public void bind(){
        GL30.glBindVertexArray(1);
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

    private void storeDataInAttributeList(int attributeNumber, int attributeSize, float[] data){
        int vboID = GL15.glGenBuffers();
        VBOS.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, attributeSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void bindIndices(int[] indices){
        int vboID = GL15.glGenBuffers();
        VBOS.add(vboID);
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

    @Override
    public void dispose() {
        LookingGlass.loaderWindow.makeContextCurrent();
        GL30.glDeleteVertexArrays(id);
        for(int vbo:VBOS)
            GL15.glDeleteBuffers(vbo);
        VBOS.clear();

    }
}
