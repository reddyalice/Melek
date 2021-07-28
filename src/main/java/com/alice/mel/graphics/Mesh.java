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

/**
 * Mesh data holder class
 * @author Bahar Demircan
 */
public final class Mesh {

    private final HashMap<Scene, HashMap<Window, Integer>> ids = new HashMap<>();
    private int vertexCount;
    private final int dimension;
    private float[] vertices;
    private float[] textureCoords;
    private float[] normals;
    private int[] indices;

    private final HashMap<Scene, Array<Integer>> VBOS = new HashMap<>();
    private final HashMap<Scene, Integer> EBOS = new HashMap<>();

    /**
     * Constructor for a 3D Mesh
     * @param vertices Vertex Array of the Mesh
     * @param textureCoords Texture Coordinates of the Mesh
     * @param normals Normals of the Mesh
     * @param indices Index Array of the Mesh
     */
    public Mesh(float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        this.dimension = 3;
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.vertexCount = indices.length;

    }

    /**
     * Constructor for a 2D Mesh
     * @param vertices Vertex Array of Mesh
     * @param textureCoords Texture Coordinates of the Mesh
     * @param indices Index Array of the Mesh
     */
    public Mesh(float[] vertices, float[] textureCoords, int[] indices) {
        this.dimension = 2;
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = new float[0];
        this.indices = indices;
        this.vertexCount = indices.length;

    }

    /**
     * Load mesh values to the gpu
     * @param scene Scene to be loaded to
     * @param window Window that VAOs are going to be generated for
     */
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
            if(dimension >= 3){
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOS.get(scene).get(2));
                GL20.glVertexAttribPointer(2, 1, GL11.GL_FLOAT, false, 0, 0);
            }
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBOS.get(scene));
            ids.get(scene).put(window, id);
        }
        GL30.glBindVertexArray(0);
    }

    /**
     * Regenerate the 3D mesh
     * @param scene Scene the Mesh loaded to
     * @param vertices New Vertex Array of the Mesh
     * @param textureCoords New Texture Coordinates of the Mesh
     * @param normals New Normals of the Mesh
     * @param indices New Index Array of the Mesh
     */
    public void regenMesh(Scene scene, float[] vertices, float[] textureCoords, float[] normals, int[] indices){
        if(dimension == 3) {
            this.vertices = vertices;
            this.textureCoords = textureCoords;
            this.normals = normals;
            this.indices = indices;
            this.vertexCount = indices.length;
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOS.get(scene).get(0));
            FloatBuffer ver = storeDataInFloatBuffer(vertices);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, ver, GL15.GL_STATIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOS.get(scene).get(1));
            FloatBuffer tex = storeDataInFloatBuffer(textureCoords);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tex, GL15.GL_STATIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOS.get(scene).get(2));
            FloatBuffer norm = storeDataInFloatBuffer(normals);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, norm, GL15.GL_STATIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBOS.get(scene));
            IntBuffer buffer = storeDataInIntBuffer(indices);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        }else
        {
            System.err.println("A Non-3D Mesh cannot be transformed to a 3D one");
            System.err.println("You can only regen meshes using same dimensions");
        }
    }

    /**
     * Regenerate a 2D Mesh
     * @param scene Scene the Mesh loaded to
     * @param vertices New Vertex Array of the Mesh
     * @param textureCoords New Texture Coordinates of the Mesh
     * @param indices New Index Array of the Mesh
     */
    public void regenMesh(Scene scene, float[] vertices, float[] textureCoords, int[] indices){
        if(dimension == 2) {
            this.vertices = vertices;
            this.textureCoords = textureCoords;
            this.indices = indices;
            this.vertexCount = indices.length;
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOS.get(scene).get(0));
            FloatBuffer ver = storeDataInFloatBuffer(vertices);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, ver, GL15.GL_STATIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOS.get(scene).get(1));
            FloatBuffer tex = storeDataInFloatBuffer(textureCoords);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tex, GL15.GL_STATIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBOS.get(scene));
            IntBuffer buffer = storeDataInIntBuffer(indices);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        }else
        {
            System.err.println("A Non-2D Mesh cannot be transformed to a 2D one");
            System.err.println("You can only regen meshes using same dimensions");

        }
    }


    /**
     * Bind Mesh
     * @param scene Scene that is loaded to
     * @param window Window that is currently rendering
     */
    public void bind(Scene scene, Window window){
        GL30.glBindVertexArray(ids.get(scene).get(window));
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        if(dimension == 3) GL20.glEnableVertexAttribArray(2);
    }

    /**
     * Unbind the mesh
     */
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
        int eboID = GL15.glGenBuffers();
        EBOS.put(scene, eboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboID);
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

    /**
     * Dispose VAO
     * @param scene Scene it is loaded to
     * @param window Window its rendering to
     */
    public void disposeVAO(Scene scene, Window window){
        GL30.glDeleteVertexArrays(ids.get(scene).get(window));
        ids.get(scene).remove(window);

    }


    public int getVertexCount() {
        return vertexCount;
    }

    public float[] getNormals() {
        return normals;
    }

    public int getDimension() {
        return dimension;
    }

    public float[] getTextureCoords() {
        return textureCoords;
    }

    public float[] getVertices() {
        return vertices;
    }

    public int[] getIndices() {
        return indices;
    }

    public int getVAOid(Scene scene, Window window){
        if(ids.get(scene).get(window) != null)
            return ids.get(scene).get(window);
        else
            return 0;
    }


    /**
     * Dispose the Mesh and clear
     * @param scene
     */
    public void dispose(Scene scene) {
        for (int vbo : VBOS.get(scene))
            GL15.glDeleteBuffers(vbo);
        GL15.glDeleteBuffers(EBOS.get(scene));
        VBOS.clear();
        EBOS.clear();
    }
}
