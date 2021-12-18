package com.alice.mel.utils.loaders;

import com.alice.mel.graphics.Material;
import com.alice.mel.graphics.Mesh;
import com.alice.mel.graphics.VertexBufferObject;
import com.alice.mel.utils.collections.Array;
import org.javatuples.Pair;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class MeshLoader {

    public static Pair<Material[], Mesh[]> loadMesh(String filePath, int flags) {
        AIScene aiScene = Assimp.aiImportFile(filePath, flags);

        int numMaterials = aiScene.mNumMaterials();
        PointerBuffer aiMaterials = aiScene.mMaterials();
        Array<Material> materials = new Array<>(numMaterials);
        for(int i = 0; i < numMaterials; i++) {
            assert aiMaterials != null;
            AIMaterial mat = AIMaterial.create(aiMaterials.get(i));

        }


        int meshCount = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        Mesh[] meshes = new Mesh[meshCount];
        for(int i = 0; i < meshCount; i++){
        }

        return Pair.with(materials.toArray(Material.class), meshes);
    }
}
