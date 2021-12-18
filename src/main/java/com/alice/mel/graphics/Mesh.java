package com.alice.mel.graphics;

import com.alice.mel.utils.loaders.OBJLoader;
import com.alice.mel.engine.Scene;
import com.alice.mel.utils.collections.Array;
import org.javatuples.Pair;
import org.lwjgl.assimp.AIFile;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import java.io.File;
import java.util.HashMap;

/**
 * Mesh data holder class
 * @author Bahar Demircan
 */
public final class Mesh extends Asset {

    private final HashMap<Scene, HashMap<Window, Integer>> ids = new HashMap<>();
    private final HashMap<String, VertexBufferObject> vertices = new HashMap<>();
    private final HashMap<Scene, Integer> indexIDs = new HashMap<>();
    private int[] indices;
    private int vertexCount;

    public boolean drawWireframe = false;
    public Mesh(String fileName){
        this(new File(fileName));
    }

    public Mesh(File file){
        fileInfo = Pair.with(file, file.lastModified());
        Pair<HashMap<String, VertexBufferObject>, int[]> pair = OBJLoader.loadOBJ(file);
        this.vertices.putAll(pair.getValue0());
        this.indices = pair.getValue1();
        this.vertexCount = indices.length;
    }


    public Mesh(HashMap<String, VertexBufferObject> vertices, int[] indices, String fileName){
        this(vertices, indices, new File(fileName));
    }

    public Mesh(HashMap<String, VertexBufferObject> vertices, int[] indices, File file){
        fileInfo = Pair.with(file, file.lastModified());
        Pair<HashMap<String, VertexBufferObject>, int[]> pair = OBJLoader.loadOBJ(file);
        this.vertices.putAll(pair.getValue0());
        this.indices = pair.getValue1();
        this.vertexCount = indices.length;
    }


    /**
     * Constructor for a 3D Mesh
     * @param positions Position Array of the Mesh
     * @param textureCoords Texture Coordinates of the Mesh
     * @param normals Normals of the Mesh
     * @param indices Index Array of the Mesh
     */
    public Mesh(float[] positions, float[] textureCoords, float[] normals, int[] indices) {

        vertices.put("positions", new VertexBufferObject(0, 3,positions.length / 3, positions));
        vertices.put("textureCoords", new VertexBufferObject(1, 2,textureCoords.length / 2, textureCoords));
        vertices.put("normals",new VertexBufferObject(2,3,normals.length / 3,normals));
        this.indices = indices;
        this.vertexCount = indices.length;
    }

    /**
     * Constructor for a 2D Mesh
     * @param positions Position Array of Mesh
     * @param textureCoords Texture Coordinates of the Mesh
     * @param indices Index Array of the Mesh
     */
    public Mesh(float[] positions, float[] textureCoords, int[] indices) {
        vertices.put("positions", new VertexBufferObject(0, 2,positions.length / 2, positions));
        vertices.put("textureCoords", new VertexBufferObject(1, 2,textureCoords.length / 2, textureCoords));
        this.indices = indices;
        this.vertexCount = indices.length;
    }

    /**
     * A Generic mesh with any type of dimensions and vertices
     * @param vertices VertexData Array
     * @param indices Index Array
     */
    public Mesh(HashMap<String, VertexBufferObject> vertices, int[] indices){
        this.vertices.putAll(vertices);
        this.indices = indices;
        this.vertexCount = indices.length;
    }

    /**
     * Load mesh values to the gpu
     * @param scene Scene to be loaded to
     * @param window Window that VAOs are going to be generated for
     */
    public void genMesh(Scene scene, Window window){
        if(!ids.containsKey(scene)) {
            ids.put(scene, new HashMap<>());
            int id = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(id);
            for(String vertexName : vertices.keySet())
                vertices.get(vertexName).genVertex(scene, GL15.GL_STATIC_DRAW);

            bindIndices(scene, indices);
            ids.get(scene).put(window, id);
        }else{
            int id = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(id);
            for(String vertexName : vertices.keySet())
                vertices.get(vertexName).registerVertex(scene);

            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexIDs.get(scene));
            ids.get(scene).put(window, id);
        }
        GL30.glBindVertexArray(0);
    }

    //TODO will add regenMesh Functions
    public void regenMesh(Scene scene, Window window, Array<VertexBufferObject> vertices, int[] indices){

    }

    /**
     * Bind Mesh
     * @param scene Scene that is loaded to
     * @param window Window that is currently rendering
     */
    public void bind(Scene scene, Window window){
        GL30.glBindVertexArray(ids.get(scene).get(window));
        for(String vertexName : vertices.keySet())
            vertices.get(vertexName).enable();
    }

    /**
     * Unbind the mesh
     */
    public void unbind(){
        for(String vertexName : vertices.keySet())
            vertices.get(vertexName).disable();
        GL30.glBindVertexArray(0);
    }

    private void bindIndices(Scene scene, int[] indices){
        int eboID = GL15.glGenBuffers();
        indexIDs.put(scene, eboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboID);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

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

    public HashMap<String, VertexBufferObject> getVertecies(){
        return vertices;
    }

    public int getVertexCount() {
        return vertexCount;
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
        for(String vertexName : vertices.keySet())
            vertices.get(vertexName).delete(scene);
        GL15.glDeleteBuffers(indexIDs.get(scene));
        vertices.clear();
        indexIDs.clear();
    }
}
