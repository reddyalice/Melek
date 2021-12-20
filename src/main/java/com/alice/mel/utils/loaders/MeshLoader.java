package com.alice.mel.utils.loaders;

import com.alice.mel.engine.Game;
import com.alice.mel.graphics.*;
import com.alice.mel.utils.collections.Array;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.util.HashMap;

public class MeshLoader {


    enum NameType{
        MeshNames,
        TextureNames,
        MaterialNames
    }

    public static HashMap<NameType, Array<String>> loadMesh(String name, String filePath, String texturesDir) throws Exception {
        return loadMesh(name, filePath, texturesDir, Assimp.aiProcess_JoinIdenticalVertices |
                Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals);
    }

    public static HashMap<NameType, Array<String>> loadMesh(String name, String filePath, String texturesDir, int flags) throws Exception{
        AIScene aiScene = Assimp.aiImportFile(filePath, flags);

        if (aiScene == null) {
            throw new Exception("Error loading model at : " + filePath);
        }

        HashMap<NameType, Array<String>> names = new HashMap<>();
        names.put(NameType.MeshNames, new Array<>());
        names.put(NameType.TextureNames, new Array<>());
        names.put(NameType.MaterialNames, new Array<>());


        int numMaterials = aiScene.mNumMaterials();
        PointerBuffer aiMaterials = aiScene.mMaterials();
        for(int i = 0; i < numMaterials; i++) {
            assert aiMaterials != null;
            int finalI = i;
            Game.forkJoinPool.submit(() -> {
                AIMaterial mat = AIMaterial.create(aiMaterials.get(finalI));
                processMaterial(name, finalI, names, mat, texturesDir);
            });
        }


        int meshCount = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        for(int i = 0; i < meshCount; i++){
            int finalI = i;
            Game.forkJoinPool.submit(() -> {

            });
        }

        //noinspection StatementWithEmptyBody
        while (!Game.forkJoinPool.isQuiescent()) {}

        return names;
    }

    private static void processMaterial(String name, int index, HashMap<NameType, Array<String>> names, AIMaterial aiMaterial, String texturesDir){
        AIColor4D color = AIColor4D.create();
        int textureCount = Assimp.aiGetMaterialTextureCount(aiMaterial, Assimp.aiTextureType_DIFFUSE);

        AIString path = AIString.calloc();
        for(int i = 0; i < textureCount; i++) {
            Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_DIFFUSE, i, path, (IntBuffer) null, null, null, null, null, null);
            String texN = path.dataString();
            if (texN.length() > 0) {
                String texName = name + "Tex" + i;
                Game.assetManager.addTexture(texName, new Texture(texturesDir + "/" + texN));
                names.get(NameType.TextureNames).add(texName);
            }
        }


        HashMap<String, VertexData> props = new HashMap<>();

        int result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_AMBIENT, Assimp.aiTextureType_NONE, 0, color);
        if(result == 0){
            props.put("ambient", new VertexData(1, 4, new float[]{color.r(), color.g(), color.b(), color.a()}));
        }

        result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_NONE, 0, color);
        if(result == 0){
            props.put("diffuse", new VertexData(1, 4, new float[]{color.r(), color.g(), color.b(), color.a()}));
        }

        result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_SPECULAR, Assimp.aiTextureType_NONE, 0, color);
        if(result == 0){
            props.put("specular", new VertexData(1, 4, new float[]{color.r(), color.g(), color.b(), color.a()}));
        }


        MaterialData materialData = new MaterialData();
        materialData.properties.putAll(props);
        for(String pN : materialData.properties.keySet())
        {
            materialData.oldProperties.put(pN, materialData.properties.get(pN).clone());
        }
        String matName = name + "Mat" + index;
        Game.assetManager.addMaterialBase(matName, materialData);
    }
}
