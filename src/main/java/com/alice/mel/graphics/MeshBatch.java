package com.alice.mel.graphics;

import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Element;
import com.alice.mel.engine.Entity;
import com.alice.mel.engine.Scene;
import com.alice.mel.gui.UIElement;
import com.alice.mel.utils.collections.Array;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.SortedMap;
import java.util.TreeMap;

public class MeshBatch  implements Comparable<MeshBatch>{

    private final float[] vertices;
    private final float[] textureCoords;
    private final float[] colors;
    private final float[] texID;
    private final float[] normals;
    private final int[] indices;

    private final HashMap<Scene, HashMap<Window, Integer>> ids = new HashMap<>();
    private final HashMap<Scene, Array<Integer>> VBOS = new HashMap<>();
    private final HashMap<Scene, Integer> EBOS = new HashMap<>();

    private final Mesh mesh;
    private final AssetManager assetManager;
    private final int dimension;
    private final int maxElementCount;
    private final HashMap<String, HashMap<Integer, Pair<Element,BatchMaterial>>> elements;
    private final HashMap<String, Integer> textureToIntMap;
    private int numberOfElements = 0;
    private int numberOfTextures = 0;
    private int zIndex;
    private final int textureLimit;
    private boolean hasRoom;


    public MeshBatch(AssetManager assetManager, String meshName, int maxElementCount, int zIndex){

        textureLimit = GL45.glGetInteger(GL45.GL_MAX_TEXTURE_IMAGE_UNITS);
        this.zIndex = zIndex;
        this.maxElementCount = maxElementCount;
        elements = new HashMap<>(textureLimit, 1);
        textureToIntMap = new HashMap<>(textureLimit, 1);
        this.assetManager = assetManager;
        Mesh mesh = assetManager.getMesh(meshName);
        this.mesh = mesh;
        this.dimension = mesh.getDimension();
        vertices = new float[mesh.getVertices().length * maxElementCount];
        textureCoords = new float[mesh.getTextureCoords().length * maxElementCount];
        normals = new float[mesh.getNormals().length * maxElementCount];
        colors = new float[ 4 * maxElementCount];
        texID = new float[maxElementCount];
        indices = new int[mesh.getIndices().length * maxElementCount];


    }


    public void genBatch(Scene scene, Window window){
        if(!VBOS.containsKey(scene))
            VBOS.put(scene, new Array<>());
        if(!ids.containsKey(scene))
            ids.put(scene, new HashMap<>());

        if(VBOS.get(scene).size == 0) {
            int id = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(id);
            storeDataInAttributeList(scene,0, dimension, vertices);
            storeDataInAttributeList(scene,1, 2, textureCoords);
            storeDataInAttributeList(scene,2, 4, colors);
            storeDataInAttributeList(scene,3, 1, texID);
            if (dimension >= 3)
                storeDataInAttributeList(scene,4, dimension, normals);
            for(int i = 0; i < maxElementCount; i++)
                loadElementIndices(i);
            bindIndices(scene, indices);
            ids.get(scene).put(window, id);
        }else
        {
            int id = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(id);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOS.get(scene).get(0));
            GL20.glVertexAttribPointer(0, dimension, GL11.GL_FLOAT, false, 0, 0);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOS.get(scene).get(1));
            GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOS.get(scene).get(2));
            GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 0, 0);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOS.get(scene).get(3));
            GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0);
            if(dimension == 3){
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOS.get(scene).get(4));
                GL20.glVertexAttribPointer(2, 1, GL11.GL_FLOAT, false, 0, 0);
            }
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBOS.get(scene));
            ids.get(scene).put(window, id);
        }
        GL30.glBindVertexArray(0);
    }



    public void addEntity(Entity entity){

    }

    public boolean addUIElement(UIElement element){
        if(numberOfElements < maxElementCount){
            BatchMaterial material = element.guiMaterial;
            String textureName = material.textureName;
            if(elements.containsKey(textureName)){
                HashMap<Integer, Pair<Element,BatchMaterial>> elemental = elements.get(textureName);
                elemental.put(numberOfElements, Pair.with(element, material));
                loadVertexProperties(textureName, numberOfElements);
                loadTextureProperties(textureName, numberOfElements);
                loadColorProperties(textureName, numberOfElements);
                if(dimension >= 3)
                    loadNormalProperties(textureName, numberOfElements);
                numberOfElements++;
                return true;

            }else{
                if(numberOfTextures < textureLimit) {
                    HashMap<Integer, Pair<Element, BatchMaterial>> elemental = new HashMap<>();
                    elemental.put(numberOfElements, Pair.with(element, material));
                    elements.put(textureName, elemental);
                    textureToIntMap.put(textureName, numberOfTextures++);

                    loadVertexProperties(textureName, numberOfElements);
                    loadTextureProperties(textureName, numberOfElements);
                    loadColorProperties(textureName, numberOfElements);
                    if(dimension >= 3)
                        loadNormalProperties(textureName, numberOfElements);
                    numberOfElements++;
                    return true;
                }

                return false;
            }

        }else
            return false;
    }


    /**
     * Bind Mesh Batch
     * @param scene Scene that is loaded to
     * @param window Window that is currently rendering
     */
    public void bind(Scene scene, Window window){
        boolean rebufferVertex = false;
        boolean rebufferColor = false;
        boolean rebufferTexture = false;
        for(String textureName : elements.keySet()){
            for(int index : elements.get(textureName).keySet()){
                Pair<Element, BatchMaterial> element = elements.get(textureName).get(index);
                if(!element.getValue0().lastPosition.equals(element.getValue0().position) ||
                        !element.getValue0().lastRotation.equals(element.getValue0().rotation)||
                        !element.getValue0().lastScale.equals(element.getValue0().scale)){
                    loadVertexProperties(textureName, index);
                    element.getValue0().lastPosition.set(element.getValue0().position);
                    element.getValue0().lastRotation.set(element.getValue0().rotation);
                    element.getValue0().lastScale.set(element.getValue0().scale);
                    rebufferVertex = true;
                }

                if(!element.getValue1().lastColor.equals(element.getValue1().color)){
                    loadColorProperties(textureName, index);
                    element.getValue1().lastColor.set(element.getValue1().color);
                    rebufferColor = true;
                }

                if(!element.getValue1().lastTextureOffset.equals(element.getValue1().textureOffset) ||
                        !element.getValue1().lastTextureScale.equals(element.getValue1().textureScale)){
                    loadTextureProperties(textureName, index);
                    element.getValue1().lastTextureOffset.set(element.getValue1().textureOffset);
                    element.getValue1().lastTextureScale.set(element.getValue1().textureScale);
                    rebufferTexture = true;
                }

            }
        }
        if(rebufferVertex)
        {
            GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, VBOS.get(scene).get(0));
            GL20.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, storeDataInFloatBuffer(vertices));
        }

        if(rebufferTexture){
            GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, VBOS.get(scene).get(1));
            GL20.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, storeDataInFloatBuffer(textureCoords));
            GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, VBOS.get(scene).get(3));
            GL20.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, storeDataInFloatBuffer(texID));
        }

        if(rebufferColor){
            GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, VBOS.get(scene).get(2));
            GL20.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, storeDataInFloatBuffer(colors));
        }

        for (String textureName : textureToIntMap.keySet()) {
            GL20.glActiveTexture(GL20.GL_TEXTURE0 + textureToIntMap.get(textureName));
            assetManager.getTexture(textureName).bind(scene);
        }

        GL30.glBindVertexArray(ids.get(scene).get(window));
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glEnableVertexAttribArray(3);
        if(dimension >= 3)
            GL30.glEnableVertexAttribArray(4);

        GL30.glDrawElements(GL30.GL_TRIANGLES, this.numberOfElements * mesh.getVertexCount(), GL30.GL_UNSIGNED_INT, 0);

    }

    private void loadElementIndices(int index){

        int indicesLength = mesh.getIndices().length;

        int offsetArrayIndex = indicesLength * index;
        int offset = mesh.getVertices().length / dimension * index;

        for(int i = 0; i < indicesLength; i++){
            indices[index + i] = offset + mesh.getIndices()[i];
        }
    }

    private final Vector3f POS = new Vector3f();

    private void loadVertexProperties(String textureName, int index){
        HashMap<Integer, Pair<Element, BatchMaterial>> elementA = elements.get(textureName);
        Pair<Element, BatchMaterial> element = elementA.get(index);

        float[] vert = mesh.getVertices();
        float[] text = mesh.getTextureCoords();
        float[] norms = mesh.getNormals();


        int vertexSize = vert.length / dimension;
        int offset = index * vert.length;

        Vector3f position = element.getValue0().position;
        Quaternionf rotation = element.getValue0().rotation;
        Vector3f scale = element.getValue0().scale;


        for(int i = 0; i < vertexSize; i++){

            if(dimension >= 3)
                POS.set(position.x + vert[i] * scale.x / 2f, position.y + vert[i + 1] * scale.y / 2f, position.z + vert[i + 2] * scale.z / 2f);
            else
                POS.set(position.x + vert[i] * scale.x / 2f, position.y + vert[i + 1] * scale.y / 2f, 0);

            POS.rotate(rotation);


            vertices[offset] = POS.x;
            vertices[offset + 1] = POS.y;
            if(dimension >= 3)
                vertices[offset + 2] = POS.z;

            offset += dimension;


        }




    }

    private final Vector2f TEX = new Vector2f();
    private void loadTextureProperties(String textureName, int index){
        HashMap<Integer, Pair<Element, BatchMaterial>> elementA = elements.get(textureName);
        Pair<Element, BatchMaterial> element = elementA.get(index);


        float[] text = mesh.getTextureCoords();
        Vector2f textureOffset = element.getValue1().textureOffset;
        Vector2f textureScale = element.getValue1().textureScale;
        int vertexSize = text.length / 2;
        int offset = index * text.length;
        int offsetA = index * vertexSize;
        for(int i = 0; i < vertexSize; i++){
            TEX.set(text[i], text[i + 1]).mul(textureScale).add(textureOffset);
            textureCoords[offset] = TEX.x;
            textureCoords[offset] = TEX.y;
            offset += 2;

            texID[offsetA] = textureToIntMap.get(textureName);
            offsetA++;

        }

    }

    private void loadColorProperties(String textureName, int index){
        HashMap<Integer, Pair<Element, BatchMaterial>> elementA = elements.get(textureName);
        Pair<Element, BatchMaterial> element = elementA.get(index);

        float[] text = mesh.getTextureCoords();
        Vector4f color = element.getValue1().color;

        int vertexSize = text.length / 2;
        int offset = index * vertexSize * 4;

        for(int i = 0; i < vertexSize; i++){

            colors[offset] = color.x;
            colors[offset + 1] = color.y;
            colors[offset + 2] = color.z;
            colors[offset + 3] = color.w;
            offset += 4;

        }
    }

    private void loadNormalProperties(String textureName, int index){
        HashMap<Integer, Pair<Element, BatchMaterial>> elementA = elements.get(textureName);
        Pair<Element, BatchMaterial> element = elementA.get(index);
        float[] norms = mesh.getNormals();

        int vertexSize = norms.length / dimension;
        int offset = index * norms.length;

        for(int i = 0; i < vertexSize; i++){
            normals[offset] = norms[i];
            normals[offset + 1] = norms[i];
            normals[offset + 2] = norms[i];
            offset += dimension;
        }

    }




    private void storeDataInAttributeList(Scene scene, int attributeNumber, int attributeSize, float[] data){
        int vboID = GL15.glGenBuffers();
        VBOS.get(scene).add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_DYNAMIC_DRAW);
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
     * Get VAO ID
     * @param scene Scene its loaded to
     * @param window Window it's rendering to
     * @return VAO ID
     */
    public int getVAOid(Scene scene, Window window){
        if(ids.get(scene).get(window) != null)
            return ids.get(scene).get(window);
        else
            return 0;
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

    /**
     * Dispose the MeshBatch and clear
     * @param scene
     */
    public void dispose(Scene scene) {
        for (int vbo : VBOS.get(scene))
            GL15.glDeleteBuffers(vbo);
        GL15.glDeleteBuffers(EBOS.get(scene));
        VBOS.clear();
        EBOS.clear();
    }


    public boolean hasRoom() {
        return this.hasRoom;
    }

    public boolean hasTextureRoom() {
        return this.elements.keySet().size() < textureLimit;
    }

    public boolean hasTexture(String textureName) {
        return this.elements.containsKey(textureName);
    }

    public int zIndex() {
        return this.zIndex;
    }

    @Override
    public int compareTo(MeshBatch o) {
        return Integer.compare(this.zIndex, o.zIndex());
    }
}
