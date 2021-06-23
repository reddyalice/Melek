package com.alice.mel.engine;

import com.alice.mel.graphics.Mesh;
import com.alice.mel.graphics.Shader;
import com.alice.mel.graphics.Texture;
import com.alice.mel.utils.collections.ObjectMap;
import com.alice.mel.utils.reflections.ClassReflection;
import com.alice.mel.utils.reflections.ReflectionException;

import java.util.HashMap;

public final class AssetManager {

    private final HashMap<String, Texture> textures = new HashMap<>();
    private final ObjectMap<Class<? extends Shader>, Shader> shaders = new ObjectMap<>();
    private final HashMap<String, Mesh> meshes = new HashMap<>();

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

        textures.put("null", new Texture(1, 1, new int[]{ (255 << 24) + (255 << 16) + 255 }));

    }

    public void addMesh(String name, Mesh mesh){
        if(meshes.containsKey(name)){
            System.out.println("Mesh with " + name + " name already exist!" + "\n" + "Overwriting!");
        }
        meshes.put(name, mesh);
    }

    public Mesh getMesh(String name)
    {
        if(meshes.containsKey(name))
            return meshes.get(name);
        else{
            System.err.println("There is no Mesh with the name of " + name);
            return null;
        }
    }

    public void removeMesh(String name){
        meshes.remove(name);
    }

    public void addTexture(String name, Texture texture){
        if(textures.containsKey(name)){
            System.out.println("Texture with \"" + name +  "\" name already exist!" + "\n" + "Overwriting!");
        }
        textures.put(name, texture);
    }

    public Texture getTexture(String name)
    {
        if(textures.containsKey(name))
            return textures.get(name);
        else{
            System.err.println("There is no Texture with the name of " + name);
            return null;
        }
    }

    public void removeTexture(String name) {
        textures.remove(name);
    }

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

    public<T extends Shader> T getShader(Class<T> shaderClass)
    {
        if(shaders.containsKey(shaderClass))
            return (T)shaders.get(shaderClass);
        else{
            System.err.println("There is no Shader with the name of " + shaderClass);
            return null;
        }
    }

    public void removeShader(Class<? extends  Shader> shaderClass){
        shaders.remove(shaderClass);
    }

    public void dispose() {
        textures.clear();
        meshes.clear();
        shaders.clear();
    }
}
