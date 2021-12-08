package com.alice.mel.graphics;

import com.alice.mel.components.BatchRenderingComponent;
import com.alice.mel.components.TransformComponent;
import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Game;
import com.alice.mel.engine.Scene;
import com.alice.mel.utils.collections.Array;
import org.javatuples.Pair;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.*;

import java.util.HashMap;

public class MeshBatch implements Comparable<MeshBatch>{

    enum OptimizationPriority{
        AddRemoveSpeed,
        MinimumSize
    }

    public OptimizationPriority optimizationPriority = OptimizationPriority.AddRemoveSpeed;

    private final int[] indices;

    private final HashMap<String, VertexBufferObject> vertices = new HashMap<>();
    private final HashMap<Scene, HashMap<Window, Integer>> ids = new HashMap<>();
    private final HashMap<Scene, Integer> indexIDs = new HashMap<>();
    private final HashMap<String, HashMap<Integer, Pair<Integer,BatchRenderingComponent>>> elements;
    private final HashMap<String, Integer> textureToIntMap;
    private final Array<Integer> indexArray = new Array<>();
    private final Array<Integer> textureIndexArray = new Array<>();

    private final Mesh mesh;
    private final Scene scene;
    private final AssetManager assetManager;
    private final int maxElementCount;

    private int numberOfElements = 0;
    private int numberOfTextures = 0;
    private int zIndex;
    private final int textureLimit;
    private boolean hasRoom;
    private int attributeIndex = 0;

    public MeshBatch(Scene scene, String meshName, int maxElementCount, int zIndex){

        this.scene = scene;
        textureLimit = GL45.glGetInteger(GL45.GL_MAX_TEXTURE_IMAGE_UNITS);
        this.zIndex = zIndex;
        this.maxElementCount = maxElementCount;
        elements = new HashMap<>(textureLimit, 1);
        textureToIntMap = new HashMap<>(textureLimit, 1);
        this.assetManager = Game.assetManager;
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

    public boolean addEntity(int entity){
        if(numberOfElements < maxElementCount) {
            if (scene.entityManager.hasComponent( entity, BatchRenderingComponent.class)) {
                BatchRenderingComponent brc = scene.entityManager.getComponent(entity, BatchRenderingComponent.class);
                String textureName = brc.material.textureName;
                if(elements.containsKey(textureName)){
                    HashMap<Integer, Pair<Integer,BatchRenderingComponent>> elemental = elements.get(textureName);

                    int index = numberOfElements;
                    for(int i = 0; i <= indexArray.size; i++)
                        if(!indexArray.contains(i, false))
                        {
                            index = i;
                            indexArray.add(i);
                            break;
                        }
                    indexArray.sort();
                    elemental.put(index, Pair.with(entity, brc));
                    for(String property : brc.material.properties.keySet()){
                        if(!vertices.containsKey(property)){
                            VertexData data = brc.material.properties.get(property);
                            vertices.put(property, new VertexBufferObject(attributeIndex++, data.dimension, vertices.get("positions").vertexData.size * maxElementCount));
                        }
                    }
                    loadElementProperties(index, entity);
                    loadMaterialProperties(index, brc);
                    vertices.get("texID").setVertex(index, new float[] { textureToIntMap.get(textureName) });

                    numberOfElements = indexArray.size;
                    if (numberOfElements == maxElementCount)
                        hasRoom = false;
                    return true;

                }else{
                    if(numberOfTextures < textureLimit) {
                        HashMap<Integer, Pair<Integer, BatchRenderingComponent>> elemental = new HashMap<>();

                        int index = numberOfElements;
                        for(int i = 0; i <= indexArray.size; i++)
                            if(!indexArray.contains(i, false))
                            {
                                index = i;
                                indexArray.add(i);
                                break;
                            }
                        indexArray.sort();

                        int textureIndex = numberOfTextures;
                        for(int i = 0; i <= textureIndexArray.size; i++)
                            if(!textureIndexArray.contains(i, false)){
                                textureIndex = i;
                                textureIndexArray.add(i);
                                break;
                            }
                        textureIndexArray.sort();
                        numberOfTextures = textureIndexArray.size;
                        elemental.put(index, Pair.with(entity, brc));
                        elements.put(textureName, elemental);
                        textureToIntMap.put(textureName, textureIndex);
                        for(String property : brc.material.properties.keySet()){
                            if(!vertices.containsKey(property)){
                                VertexData data = brc.material.properties.get(property);
                                vertices.put(property, new VertexBufferObject(attributeIndex++, data.dimension, vertices.get("positions").vertexData.size * maxElementCount));
                            }
                        }
                        loadElementProperties(index, entity);
                        loadMaterialProperties(index, brc);
                        vertices.get("texID").setVertex(index, new float[] { textureToIntMap.get(textureName) });

                        numberOfElements = indexArray.size;
                        if (numberOfElements == maxElementCount)
                            hasRoom = false;
                        return true;
                    }

                    return false;
                }
            }else
                return false;
        }else
            return false;
    }

    public void removeEntity(int entity){
        if (scene.entityManager.hasComponent(entity, BatchRenderingComponent.class)) {
            BatchRenderingComponent brc = scene.entityManager.getComponent(entity, BatchRenderingComponent.class);
            HashMap<Integer, Pair<Integer,BatchRenderingComponent>> elemental = elements.get(brc.material.textureName);
            if(entity != -1){
                Pair<Integer, BatchRenderingComponent> elementPair = Pair.with(entity, brc);
                int index = -1;
                for(int i : elemental.keySet()){
                    if(elemental.get(i).equals(elementPair)){
                        index = i;
                        break;
                    }
                }

                if(index != -1){
                    unloadElementProperties(index);
                    unloadMaterialProperties(index, brc);
                    indexArray.removeValue(index, false);
                    numberOfElements--;
                    elemental.remove(index);
                    if(elements.get(brc.material.textureName).isEmpty()) {
                        elements.remove(brc.material.textureName);
                        textureIndexArray.removeValue(textureToIntMap.get(brc.material.textureName), false);
                        textureToIntMap.remove(brc.material.textureName);
                        numberOfTextures--;
                    }
                }
            }
        }
    }

    public void bind(Scene scene, Window window){
        if(indexArray.size > 0) {
            boolean rebufferElement = false;
            boolean rebufferMaterial = false;
            for (String textureName : elements.keySet()) {
                for (int index : elements.get(textureName).keySet()) {
                    Pair<Integer, BatchRenderingComponent> element = elements.get(textureName).get(index);


                    TransformComponent tc = scene.entityManager.getComponent( element.getValue0(), TransformComponent.class);
                    if (tc.isDirty()) {
                        loadElementProperties(index, element.getValue0());
                        tc.doClean();
                        rebufferElement = true;
                    }


                    if (element.getValue1().isDirty()) {
                        loadMaterialProperties(index, element.getValue1());
                        element.getValue1().doClean();
                        rebufferMaterial = true;
                    }

                }
            }
            if (rebufferElement) {
                vertices.get("positions").regenVertex(scene);
                rebufferElement = false;
            }

            if (rebufferMaterial) {
                vertices.get("textureCoords").regenVertex(scene);
                vertices.get("colors").regenVertex(scene);
                for (String textureName : elements.keySet()) {
                    for (int index : elements.get(textureName).keySet()) {
                        Pair<Integer, BatchRenderingComponent> element = elements.get(textureName).get(index);
                        for (String property : element.getValue1().material.properties.keySet()) {
                            if (!vertices.get(property).getIDs().containsKey(scene)) {
                                scene.loaderWindow.makeContextCurrent();
                                vertices.get(property).genVertex(scene, GL15.GL_DYNAMIC_DRAW);
                                if (scene.currentContext != null)
                                    scene.currentContext.makeContextCurrent();
                            } else
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
            for (String vertexName : vertices.keySet())
                vertices.get(vertexName).enable();

            if(mesh.drawWireframe) GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_LINE);
            GL30.glDrawElements(GL30.GL_TRIANGLES, mesh.getVertexCount() * (indexArray.get(indexArray.size - 1) + 1), GL30.GL_UNSIGNED_INT, 0);
            if(mesh.drawWireframe) GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_FILL);
            for (String vertexName : vertices.keySet())
                vertices.get(vertexName).disable();
            GL30.glBindVertexArray(0);
        }
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

    private void loadElementIndices(int index){
        int indicesLength = mesh.getIndices().length;
        int offsetArrayIndex = indicesLength * index;
        int offset = (mesh.getVertecies().get("positions").vertexData.size) * index;

        for(int i = 0; i < indicesLength; i++){
            indices[offsetArrayIndex + i] = offset + mesh.getIndices()[i];
        }
    }


    private void loadElementProperties(int index, int entity){
        VertexData positionData = mesh.getVertecies().get("positions").vertexData;

        int vertexSize = positionData.size;
        int offset = index * vertexSize;

        TransformComponent comp = scene.entityManager.getComponent(entity, TransformComponent.class);

        Vector3f position = comp.position;
        Quaternionf rotation = comp.rotation;
        Vector3f scale = comp.scale;
        float[] POS = new float[positionData.dimension];
        for(int i = 0;i < vertexSize; i++){
            switch (POS.length) {
                case 1 -> POS[0] = position.x + positionData.data[positionData.dimension * i] * scale.x;
                case 2 -> {
                    POS[0] = position.x + positionData.data[positionData.dimension * i] * scale.x;
                    POS[1] = position.y + positionData.data[positionData.dimension * i + 1] * scale.y;
                }
                default -> {
                    POS[0] = position.x + positionData.data[positionData.dimension * i] * scale.x;
                    POS[1] = position.y + positionData.data[positionData.dimension * i + 1] * scale.y;
                    POS[2] = position.z + positionData.data[positionData.dimension * i + 2] * scale.z;
                }
            }
            vertices.get("positions").setVertex(offset + i, POS);
        }
    }

    private void unloadElementProperties(int index){
        VertexData positionData = mesh.getVertecies().get("positions").vertexData;
        int vertexSize = positionData.size;
        int offset = index * vertexSize;
        float[] POS = new float[positionData.dimension];
        for(int i = 0;i < vertexSize; i++){
            vertices.get("positions").setVertex(offset + i, POS);
        }
    }


    private void loadMaterialProperties(int index, BatchRenderingComponent brc){


        VertexData textureData = mesh.getVertecies().get("textureCoords").vertexData;
        Vector2f textureOffset = brc.material.textureOffset;
        Vector2f textureDivision = brc.material.textureDivision;


        int vertexSize = textureData.size;
        int offset = index * vertexSize;
        float[] TEX = new float[textureData.dimension];
        for(int i = 0; i < vertexSize; i++){
            TEX[0] = textureData.data[textureData.dimension * i] / textureDivision.x + (textureOffset.x / textureDivision.x);
            TEX[1] = textureData.data[textureData.dimension * i + 1] / textureDivision.y + (textureOffset.y / textureDivision.y);
            vertices.get("textureCoords").setVertex(offset + i, TEX);
        }

        Vector4f color = brc.material.color;
        float[] COL = new float[] {color.x, color.y, color.z, color.w};
        for(int i = 0; i < vertexSize; i++)
            vertices.get("colors").setVertex(index + i, COL);

        for(String property : brc.material.properties.keySet()){
            VertexData propertyData = brc.material.properties.get(property);
            vertexSize = propertyData.size;
            offset = index * vertexSize;
            float[] VER = new float[propertyData.dimension];
            for(int i = 0; i < vertexSize; i++){
                System.arraycopy(propertyData.data, propertyData.dimension * i, VER, 0, propertyData.dimension);
                vertices.get(property).setVertex(offset + i, VER);
            }
        }


    }

    private void unloadMaterialProperties(int index, BatchRenderingComponent brc){
        VertexData textureData = mesh.getVertecies().get("textureCoords").vertexData;
        int vertexSize = textureData.size;
        int offset = index * vertexSize;
        float[] TEX = new float[textureData.dimension];
        float[] COL = new float[4];
        for(int i = 0; i < vertexSize; i++){
            vertices.get("textureCoords").setVertex(offset + i, TEX);
        }
        for(int i = 0; i < vertexSize; i++)
            vertices.get("colors").setVertex(index + i, COL);

        for(String property : brc.material.properties.keySet()){
            VertexData propertyData = brc.material.properties.get(property);
            vertexSize = propertyData.size;
            offset = index * vertexSize;
            float[] VER = new float[propertyData.dimension];
            for(int i = 0; i < vertexSize; i++){
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

    public Mesh getMesh(){
        return mesh;
    }

    public int zIndex() {
        return this.zIndex;
    }

    public int getNumberOfElements(){
        return numberOfElements;
    }

    @Override
    public int compareTo(MeshBatch o) {
        return Integer.compare(this.zIndex, o.zIndex());
    }
}
