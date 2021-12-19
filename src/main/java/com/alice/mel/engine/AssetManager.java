package com.alice.mel.engine;

import com.alice.mel.graphics.*;
import com.alice.mel.graphics.shaders.Batched3DShader;
import com.alice.mel.graphics.shaders.BatchedSpriteShader;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.ObjectMap;
import com.alice.mel.utils.reflections.ClassReflection;
import com.alice.mel.utils.reflections.ReflectionException;
import org.javatuples.Pair;
import org.lwjgl.util.par.ParShapes;
import org.lwjgl.util.par.ParShapesMesh;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Objects;

/**
 * An Asset Manager for Asset Handling between all scenes and windows
 * @author Bahar Demircan
 */
public final class AssetManager implements Serializable {

    private final HashMap<String, Texture> textures = new HashMap<>();
    private final ObjectMap<Class<? extends Shader>, Shader> shaders = new ObjectMap<>();
    private final HashMap<String, Mesh> meshes = new HashMap<>();
    private final HashMap<String, MaterialData> materialBases = new HashMap<>();

    public AssetManager(){
        addMesh("Quad", new Mesh(
                new float[]{
                        -1f, 1f,
                        1f,  1f,
                        1f,  -1f,
                        -1f, -1f
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
        ));

        addMesh("Quad3D", new Mesh(
                new float[]{
                        -1f, 1f, 0,
                        1f,  1f, 0,
                        1f,  -1f, 0,
                        -1f, -1f, 0
                },
                new float[]{
                        0, 0,
                        1, 0,
                        1, 1,
                        0, 1
                },
                new float[] {
                        0,0,1,
                        0,0,1,
                        0,0,1,
                        0,0,1
                },
                new int[]{
                        0,1,2,
                        2,3,0
                }
        ));
        ParShapesMesh mesh = ParShapes.par_shapes_create_parametric_sphere(10, 10);
        assert mesh != null;
        int vc = mesh.npoints();
        float[] vertices = new float[vc * 3]; mesh.points(mesh.npoints() * 3 ).get(vertices);
        float[] tCoords = new float[vc * 2]; Objects.requireNonNull(mesh.tcoords(vc * 2)).get(tCoords);
        float[] normals = new float[vc * 3]; Objects.requireNonNull(mesh.normals(vc * 3)).get(normals);
        int[] indices = new int[mesh.ntriangles() * 3]; mesh.triangles(mesh.ntriangles() * 3).get(indices);
        mesh.free();
        addMesh("sphere", new Mesh(vertices, tCoords, normals, indices));
        addTexture("null", new Texture(1, 1, new int[]{0}));
    }


    public void hotReload(Scene scene, Window window){

        //Check and Reload textures
        for (String textureName : textures.keySet()) {
        Texture texture = textures.get(textureName);
        if (texture.fileInfo != null)
        if (texture.fileInfo.getValue1() != texture.fileInfo.getValue0().lastModified()) {
            if (scene.hasTexture(textureName)) {
                System.out.println("Reloading " + textureName);
                scene.loaderWindow.makeContextCurrent();
                texture.regenTexture(scene, texture.fileInfo.getValue0());
                if (scene.currentContext != null)
                    scene.currentContext.makeContextCurrent();
            }
            texture.fileInfo = texture.fileInfo.setAt1(texture.fileInfo.getValue0().lastModified());
            }

        }


        //Todo mesh regen

        //Todo shader regenShaders

    }


    /**
     * Add Mesh to the the Asset Manager
     * @param name Name the Mesh will registered with
     * @param mesh Mesh data itself
     */
    public void addMesh(String name, Mesh mesh){
        if(meshes.containsKey(name)){
            System.out.println("Mesh with " + name + " name already exist!" + "\n" + "Overwriting!");
        }
        meshes.put(name, mesh);
    }

    /**
     * Check if Asset Manager has the mesh with a certain name
     * @param name Name that is wanted to be checked
     * @return If asset manager has the mesh
     */
    public boolean hasMesh(String name){
        return meshes.containsKey(name);
    }


    /**
     * Get the registered mesh from the Asset Manager
     * @param name Name the Mesh is registered as
     * @return Registered Mesh if the name is valid else null
     */
    public Mesh getMesh(String name)
    {
        if(meshes.containsKey(name))
            return meshes.get(name);
        else{
            System.err.println("There is no Mesh with the name of " + name);
            return null;
        }
    }

    /**
     * Remove Mesh data from the Asset Manager
     * @param name Name the Mesh registered as
     */
    public void removeMesh(String name){
        meshes.remove(name);
    }


    /**
     * Add Texture to the Asset Manager
     * @param name Name the texture will register with
     * @param texture Texture data itself
     */
    public void addTexture(String name, Texture texture){
        if(textures.containsKey(name)){
            System.out.println("Texture with \"" + name +  "\" name already exist!" + "\n" + "Overwriting!");
        }
        textures.put(name, texture);
    }

    /**
     * Check if Asset Manager has the Texture with a certain name
     * @param name Name that is wanted to be checked
     * @return If asset manager has the Texture
     */
    public boolean hasTexture(String name){
        return textures.containsKey(name);
    }

    /**
     * Get the registered Texture from the Asset Manager
     * @param name Name the Texture registered as
     * @return Registered Texture if given name is valid else null
     */
    public Texture getTexture(String name)
    {
        if(textures.containsKey(name))
            return textures.get(name);
        else{
            System.err.println("There is no Texture with the name of " + name);
            return textures.get("null");
        }
    }

    /**
     * Remove the Texture data from the Asset Manager
     * @param name Name the Texture registered as
     */
    public void removeTexture(String name) {
        textures.remove(name);
    }

    /**
     * Add Shader to the Asset Manager
     * @param shaderClass Class of the Shader that will be added
     */
    public void addShader(Class<? extends Shader> shaderClass){
        if(shaders.containsKey(shaderClass)){
            System.out.println("Shader with \"" + shaderClass +  "\" class already exist!");
        }else{
            try {
                shaders.put(shaderClass, ClassReflection.newInstance(shaderClass));
            } catch (ReflectionException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Check if Asset Manager has the Shader with a certain class
     * @param shaderClass Class that is wanted to be checked
     * @return If asset manager has the Shader
     */
    public boolean hasShader(Class<? extends Shader> shaderClass){
        return shaders.containsKey(shaderClass);
    }


    /**
     * Get the registered Shader from the Asset Manager
     * @param shaderClass Class of the registered Shader
     * @param <T> Type of the registered shader
     * @return Registered shader if given class was registered else null
     */
    public<T extends Shader> T getShader(Class<T> shaderClass)
    {
        if(shaders.containsKey(shaderClass))
            return (T)shaders.get(shaderClass);
        else{
            System.err.println("There is no Shader with the name of " + shaderClass);
            return null;
        }
    }

    /**
     * Remove the Shader from the Asset Manager
     * @param shaderClass Class of the Shader
     */
    public void removeShader(Class<? extends  Shader> shaderClass){
        shaders.remove(shaderClass);
    }

    /**
     * Add Texture to the Asset Manager
     * @param name Name the texture will register with
     * @param materialData Material data itself
     */
    public void addMaterialBase(String name, MaterialData materialData){
        if(materialBases.containsKey(name)){
            System.out.println("Material Base with \"" + name +  "\" name already exist!" + "\n" + "Overwriting!");
        }
        materialBases.put(name, materialData);
    }

    /**
     * Check if Asset Manager has the MaterialData with a certain name
     * @param name Name that is wanted to be checked
     * @return If asset manager has the MaterialData
     */
    public boolean hasMaterialBase(String name){
        return materialBases.containsKey(name);
    }

    /**
     * Get the registered MaterialData from the Asset Manager
     * @param name Name the MaterialData registered as
     * @return Registered MaterialData if given name is valid else null
     */
    public MaterialData getMaterialBase(String name)
    {
        if(materialBases.containsKey(name))
            return materialBases.get(name);
        else{
            System.err.println("There is no Material Base with the name of " + name);
            return materialBases.get("null");
        }
    }

    /**
     * Remove the MaterialData data from the Asset Manager
     * @param name Name the MaterialData registered as
     */
    public void removeMaterialBase(String name) {
        materialBases.remove(name);
    }


    /**
     * Dispose all registered data
     */
    public void dispose() {
        textures.clear();
        meshes.clear();
        shaders.clear();
        materialBases.clear();
    }
}
