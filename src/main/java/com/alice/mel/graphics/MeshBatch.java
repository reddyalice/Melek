package com.alice.mel.graphics;

import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Element;
import com.alice.mel.engine.Entity;
import com.alice.mel.engine.Scene;
import com.alice.mel.gui.UIElement;
import org.javatuples.Pair;
import org.joml.*;
import org.lwjgl.opengl.*;

import java.util.HashMap;

public class MeshBatch  implements Comparable<MeshBatch>{


    private final int[] indices;

    private final HashMap<String, VertexBufferObject> vertices = new HashMap<>();
    private final HashMap<Scene, HashMap<Window, Integer>> ids = new HashMap<>();
    private final HashMap<Scene, Integer> indexIDs = new HashMap<>();


    private final Mesh mesh;
    private final AssetManager assetManager;
    private final int maxElementCount;
    private final HashMap<String, HashMap<Integer, Pair<Element,BatchMaterial>>> elements;
    private final HashMap<String, Integer> textureToIntMap;
    private int numberOfElements = 0;
    private int numberOfTextures = 0;
    private int zIndex;
    private final int textureLimit;
    private boolean hasRoom;

    private int attributeIndex = 0;
    public MeshBatch(AssetManager assetManager, String meshName, int maxElementCount, int zIndex){

        textureLimit = GL45.glGetInteger(GL45.GL_MAX_TEXTURE_IMAGE_UNITS);
        this.zIndex = zIndex;
        this.maxElementCount = maxElementCount;
        elements = new HashMap<>(textureLimit, 1);
        textureToIntMap = new HashMap<>(textureLimit, 1);
        this.assetManager = assetManager;
        Mesh mesh = assetManager.getMesh(meshName);
        this.mesh = mesh;


        assert mesh != null;
        HashMap<String, VertexBufferObject> meshVertecies = mesh.getVertecies();

        for(String vertexName : meshVertecies.keySet()) {
            VertexBufferObject vertex = meshVertecies.get(vertexName);
            vertices.put(vertexName, new VertexBufferObject(attributeIndex++, vertex.vertexData.dimension, vertex.vertexData.size * maxElementCount));
        }
        vertices.put("colors", new VertexBufferObject(attributeIndex++, 4, vertices.get("positions").vertexData.size * maxElementCount));
        vertices.put("texID", new VertexBufferObject(attributeIndex++, 1, vertices.get("positions").vertexData.size * maxElementCount));
        indices = new int[mesh.getIndices().length * maxElementCount];

    }


    public void genBatch(Scene scene, Window window){

        if(!ids.containsKey(scene)) {
            ids.put(scene, new HashMap<>());
            int id = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(id);
            for(String vertexName : vertices.keySet())
                vertices.get(vertexName).genVertex(scene, GL15.GL_DYNAMIC_DRAW);

            for(int i = 0; i < maxElementCount; i++)
                loadElementIndices(i);
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



    public void addEntity(Entity entity){

    }

    public boolean addUIElement(UIElement element){
        if(numberOfElements < maxElementCount){
            BatchMaterial material = element.guiMaterial;
            String textureName = material.textureName;
            if(elements.containsKey(textureName)){
                HashMap<Integer, Pair<Element,BatchMaterial>> elemental = elements.get(textureName);
                elemental.put(numberOfElements, Pair.with(element, material));
                for(String property : material.properties.keySet()){
                    if(!vertices.containsKey(property)){
                        VertexData data = material.properties.get(property);
                        vertices.put(property, new VertexBufferObject(attributeIndex++, data.dimension, vertices.get("positions").vertexData.size * maxElementCount));
                    }
                }
                loadElementProperties(numberOfElements, element);
                loadMaterialProperties(numberOfElements, material);
                vertices.get("texID").setVertex(numberOfElements, new float[] { textureToIntMap.get(textureName) });

                numberOfElements++;
                if(numberOfElements == maxElementCount)
                    hasRoom = false;
                return true;

            }else{
                if(numberOfTextures < textureLimit) {
                    HashMap<Integer, Pair<Element, BatchMaterial>> elemental = new HashMap<>();
                    elemental.put(numberOfElements, Pair.with(element, material));
                    elements.put(textureName, elemental);
                    textureToIntMap.put(textureName, numberOfTextures++);
                    for(String property : material.properties.keySet()){
                        if(!vertices.containsKey(property)){
                            VertexData data = material.properties.get(property);
                            vertices.put(property, new VertexBufferObject(attributeIndex++, data.dimension, vertices.get("positions").vertexData.size * maxElementCount));
                        }
                    }
                    loadElementProperties(numberOfElements, element);
                    loadMaterialProperties(numberOfElements, material);
                    vertices.get("texID").setVertex(numberOfElements, new float[] { textureToIntMap.get(textureName) });

                    numberOfElements++;
                    if(numberOfElements == maxElementCount)
                        hasRoom = false;
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
        boolean rebufferElement = false;
        boolean rebufferMaterial = false;
        for(String textureName : elements.keySet()){
            for(int index : elements.get(textureName).keySet()){
                Pair<Element, BatchMaterial> element = elements.get(textureName).get(index);

                if(element.getValue0().isDirty()){
                    loadElementProperties(index, element.getValue0());
                    element.getValue0().doClean();
                    rebufferElement = true;
                }


                if(element.getValue1().isDirty()){
                    loadMaterialProperties(index, element.getValue1());
                    element.getValue1().doClean();
                    rebufferMaterial = true;
                }

            }
        }
        if(rebufferElement)
        {
            vertices.get("positions").regenVertex(scene);
            rebufferElement = false;
        }

        if(rebufferMaterial){
            vertices.get("textureCoords").regenVertex(scene);
            vertices.get("colors").regenVertex(scene);
            for(String textureName : elements.keySet()) {
                for (int index : elements.get(textureName).keySet()) {
                    Pair<Element, BatchMaterial> element = elements.get(textureName).get(index);
                    for (String property : element.getValue1().properties.keySet()) {
                        if(!vertices.get(property).getIDs().containsKey(scene)) {
                            scene.loaderWindow.makeContextCurrent();
                            vertices.get(property).genVertex(scene, GL15.GL_DYNAMIC_DRAW);
                            if(scene.currentContext != null)
                                scene.currentContext.makeContextCurrent();
                        }else
                            vertices.get(property).regenVertex(scene);
                    }
                }
            }
            rebufferMaterial = false;
        }

        GL20.glEnable(GL11.GL_TEXTURE);
        for (String textureName : textureToIntMap.keySet()) {
            GL20.glActiveTexture(GL20.GL_TEXTURE0 + textureToIntMap.get(textureName));
            assetManager.getTexture(textureName).bind(scene);
        }

        GL30.glBindVertexArray(ids.get(scene).get(window));
        for(String vertexName : vertices.keySet())
            vertices.get(vertexName).enable();

        GL30.glDrawElements(GL30.GL_TRIANGLES, mesh.getVertexCount() * numberOfElements, GL30.GL_UNSIGNED_INT, 0);


        for(String vertexName : vertices.keySet())
           vertices.get(vertexName).disable();
        GL30.glBindVertexArray(0);


    }



    private void loadElementIndices(int index){
        int indicesLength = mesh.getIndices().length;
        int offsetArrayIndex = indicesLength * index;
        int offset = (mesh.getVertecies().get("positions").vertexData.size) * index;

        for(int i = 0; i < indicesLength; i++){
            indices[offsetArrayIndex + i] = offset + mesh.getIndices()[i];
        }
    }


    private void loadElementProperties(int index, Element element){
        VertexData positionData = mesh.getVertecies().get("positions").vertexData;

        int vertexSize = positionData.size;
        int offset = index * vertexSize;

        Vector3f position = element.position;
        Quaternionf rotation = element.rotation;
        Vector3f scale = element.scale;
        float[] POS = new float[positionData.dimension];
        for(int i = 0;i < vertexSize; i++){

            switch (POS.length) {
                case 1 -> POS[0] = position.x + positionData.data[positionData.dimension * i] * scale.x / 2f;
                case 2 -> {
                    POS[0] = position.x + positionData.data[positionData.dimension * i] * scale.x / 2f;
                    POS[1] = position.y + positionData.data[positionData.dimension * i + 1] * scale.y / 2f;
                }
                default -> {
                    POS[0] = position.x + positionData.data[positionData.dimension * i] * scale.x / 2f;
                    POS[1] = position.y + positionData.data[positionData.dimension * i + 1] * scale.y / 2f;
                    POS[2] = position.z + positionData.data[positionData.dimension * i + 2] * scale.z / 2f;
                }
            }
            vertices.get("positions").setVertex(offset + i, POS);
        }
    }

    private void loadMaterialProperties(int index, BatchMaterial material){


        VertexData textureData = mesh.getVertecies().get("textureCoords").vertexData;
        Vector2f textureOffset = material.textureOffset;
        Vector2f textureDivision = material.textureDivision;


        int vertexSize = textureData.size;
        int offset = index * vertexSize;
        float[] TEX = new float[textureData.dimension];
        for(int i = 0; i < vertexSize; i++){
            TEX[0] = textureData.data[textureData.dimension * i] / textureDivision.x + (textureOffset.x / textureDivision.x);
            TEX[1] = textureData.data[textureData.dimension * i + 1] / textureDivision.y + (textureOffset.y / textureDivision.y);
            vertices.get("textureCoords").setVertex(offset + i, TEX);
        }

        Vector4f color = material.color;
        float[] COL = new float[] {color.x, color.y, color.z, color.w};
        for(int i = 0; i < vertexSize; i++)
            vertices.get("colors").setVertex(index + i, COL);

        for(String property : material.properties.keySet()){
            VertexData propertyData = material.properties.get(property);
            vertexSize = propertyData.size;
            offset = index * vertexSize;
            float[] VER = new float[propertyData.dimension];
            for(int i = 0; i < vertexSize; i++){
                System.arraycopy(propertyData.data, propertyData.dimension * i, VER, 0, propertyData.dimension);
                vertices.get(property).setVertex(offset + i, VER);
            }
        }


    }

    private void bindIndices(Scene scene, int[] indices){
        int eboID = GL15.glGenBuffers();
        indexIDs.put(scene, eboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboID);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

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
        for(String vertexName : vertices.keySet())
            vertices.get(vertexName).delete(scene);
        GL15.glDeleteBuffers(indexIDs.get(scene));
        vertices.clear();
        indexIDs.clear();
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
