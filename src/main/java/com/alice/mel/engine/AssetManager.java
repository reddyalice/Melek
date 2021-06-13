package com.alice.mel.engine;

import com.alice.mel.graphics.Mesh;
import com.alice.mel.graphics.Shader;
import com.alice.mel.graphics.Texture;
import com.alice.mel.utils.Disposable;

import java.util.HashMap;

public final class AssetManager implements Disposable {

    private final HashMap<String, Texture> textures = new HashMap<>();
    private final HashMap<String, Shader> shaders = new HashMap<>();
    private final HashMap<String, Mesh> meshes = new HashMap<>();

    public AssetManager(){
        addMesh("Quad", new Mesh(
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
        ));

        addMesh("Quad3D", new Mesh(
                new float[]{
                        -0.5f, 0.5f, 0,
                        0.5f,  0.5f, 0,
                        0.5f,  -0.5f, 0,
                        -0.5f, -0.5f, 0
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

    public void addShader(String name, Shader shader){
        if(shaders.containsKey(name)){
            System.out.println("Shader with \"" + name +  "\" name already exist!" + "\n" + "Overwriting!");
        }
        shaders.put(name, shader);
    }

    public Shader getShader(String name)
    {
        if(shaders.containsKey(name))
            return shaders.get(name);
        else{
            System.err.println("There is no Shader with the name of " + name);
            return null;
        }
    }

    public void removeShader(String name){
        shaders.remove(name);
    }

    @Override
    public void dispose() {
        textures.clear();
        meshes.clear();
        shaders.clear();
    }
}
