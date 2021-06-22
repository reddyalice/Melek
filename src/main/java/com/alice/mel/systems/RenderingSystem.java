package com.alice.mel.systems;

import com.alice.mel.components.RenderingComponent;
import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Entity;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.*;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.ImmutableArray;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.HashMap;

public class RenderingSystem extends ComponentSystem{

    private final AssetManager assetManager;
    private final HashMap<Shader, HashMap<Mesh, HashMap<Texture, HashMap<Material, Array<Entity>>>>> renderMap = new HashMap<>();

    public RenderingSystem(AssetManager assetManager){
        this.assetManager = assetManager;
    }


    @Override
    public void addedToScene(Scene scene) {
        ImmutableArray<Entity> entities = scene.getEntitiesFor(RenderingComponent.class);

        if(entities != null)
        for(Entity entity : entities){
            RenderingComponent rend = entity.getComponent(RenderingComponent.class);
            scene.loadShader(rend.material.shaderClass);
            Shader shader = assetManager.getShader(rend.material.shaderClass);
            Material material = rend.material;
            scene.loadTexture(rend.textureName);
            Texture texture = assetManager.getTexture(rend.textureName);
            scene.loadMesh(rend.meshName);
            Mesh mesh = assetManager.getMesh(rend.meshName);

            HashMap<Mesh, HashMap<Texture, HashMap<Material, Array<Entity>>>> batch0 = renderMap.get(shader);
            if(batch0 != null){
                HashMap<Texture, HashMap<Material, Array<Entity>>> batch1 = batch0.get(mesh);
                if(batch1 != null){
                    HashMap<Material, Array<Entity>> batch2 = batch1.get(texture);
                    if(batch2 != null){
                        Array<Entity> batch3 = batch2.get(material);
                        if(batch3 != null){
                            batch3.add(entity);
                            batch2.put(material, batch3);
                            batch1.put(texture, batch2);
                            batch0.put(mesh, batch1);
                            renderMap.put(shader, batch0);
                        }else{
                            batch3 = new Array<>();
                            batch3.add(entity);
                            batch2.put(material, batch3);
                            batch1.put(texture, batch2);
                            batch0.put(mesh, batch1);
                            renderMap.put(shader, batch0);
                        }
                    }else{
                        batch2 = new HashMap<>();
                        Array<Entity> batch3 = new Array<>();
                        batch3.add(entity);
                        batch2.put(material, batch3);
                        batch1.put(texture, batch2);
                        batch0.put(mesh, batch1);
                        renderMap.put(shader, batch0);
                    }
                }else{
                    batch1 = new HashMap<>();
                    HashMap<Material, Array<Entity>> batch2 = new HashMap<>();
                    Array<Entity> batch3 = new Array<>();
                    batch3.add(entity);
                    batch2.put(material, batch3);
                    batch1.put(texture, batch2);
                    batch0.put(mesh, batch1);
                    renderMap.put(shader, batch0);
                }
            }else{
                batch0 = new HashMap<>();
                HashMap<Texture, HashMap<Material, Array<Entity>>> batch1 = new HashMap<>();
                HashMap<Material, Array<Entity>> batch2 = new HashMap<>();
                Array<Entity> batch3 = new Array<>();
                batch3.add(entity);
                batch2.put(material, batch3);
                batch1.put(texture, batch2);
                batch0.put(mesh, batch1);
                renderMap.put(shader, batch0);
            }
        }
        scene.entityAdded.add("RenderingSystem", entity -> {
            if(entity.hasComponent(RenderingComponent.class)) {
                RenderingComponent rend = entity.getComponent(RenderingComponent.class);
                scene.loadShader(rend.material.shaderClass);
                Shader shader = assetManager.getShader(rend.material.shaderClass);
                Material material = rend.material;
                scene.loadTexture(rend.textureName);
                Texture texture = assetManager.getTexture(rend.textureName);
                scene.loadMesh(rend.meshName);
                Mesh mesh = assetManager.getMesh(rend.meshName);

                HashMap<Mesh, HashMap<Texture, HashMap<Material, Array<Entity>>>> batch0 = renderMap.get(shader);
                if (batch0 != null) {
                    HashMap<Texture, HashMap<Material, Array<Entity>>> batch1 = batch0.get(mesh);
                    if (batch1 != null) {
                        HashMap<Material, Array<Entity>> batch2 = batch1.get(texture);
                        if (batch2 != null) {
                            Array<Entity> batch3 = batch2.get(material);
                            if (batch3 != null) {
                                batch3.add(entity);
                                batch2.put(material, batch3);
                                batch1.put(texture, batch2);
                                batch0.put(mesh, batch1);
                                renderMap.put(shader, batch0);
                            } else {
                                batch3 = new Array<>();
                                batch3.add(entity);
                                batch2.put(material, batch3);
                                batch1.put(texture, batch2);
                                batch0.put(mesh, batch1);
                                renderMap.put(shader, batch0);
                            }
                        } else {
                            batch2 = new HashMap<>();
                            Array<Entity> batch3 = new Array<>();
                            batch3.add(entity);
                            batch2.put(material, batch3);
                            batch1.put(texture, batch2);
                            batch0.put(mesh, batch1);
                            renderMap.put(shader, batch0);
                        }
                    } else {
                        batch1 = new HashMap<>();
                        HashMap<Material, Array<Entity>> batch2 = new HashMap<>();
                        Array<Entity> batch3 = new Array<>();
                        batch3.add(entity);
                        batch2.put(material, batch3);
                        batch1.put(texture, batch2);
                        batch0.put(mesh, batch1);
                        renderMap.put(shader, batch0);
                    }
                } else {
                    batch0 = new HashMap<>();
                    HashMap<Texture, HashMap<Material, Array<Entity>>> batch1 = new HashMap<>();
                    HashMap<Material, Array<Entity>> batch2 = new HashMap<>();
                    Array<Entity> batch3 = new Array<>();
                    batch3.add(entity);
                    batch2.put(material, batch3);
                    batch1.put(texture, batch2);
                    batch0.put(mesh, batch1);
                    renderMap.put(shader, batch0);
                }
            }
        });

        scene.entityRemoved.add("RenderingSystem", entity -> {
            if(entity.hasComponent(RenderingComponent.class)){
                RenderingComponent rend = entity.getComponent(RenderingComponent.class);
                Shader shader = assetManager.getShader(rend.material.shaderClass);
                Material material = rend.material;
                Texture texture = assetManager.getTexture(rend.textureName);
                Mesh mesh = assetManager.getMesh(rend.meshName);

                HashMap<Mesh, HashMap<Texture, HashMap<Material, Array<Entity>>>> batch0 = renderMap.get(shader);
                if(batch0 != null) {
                    HashMap<Texture, HashMap<Material, Array<Entity>>> batch1 = batch0.get(mesh);
                    if(batch1 != null){
                        HashMap<Material, Array<Entity>> batch2 = batch1.get(texture);
                        if(batch2 != null){
                            Array<Entity> batch3 = batch2.get(material);
                            if(batch3 != null){
                                batch3.removeValue(entity, false);
                                if(batch3.isEmpty()){
                                    batch2.remove(material);
                                    batch1.remove(texture);
                                    batch0.remove(mesh);
                                    renderMap.remove(shader);
                                }
                            }
                        }
                    }
                }


            }
        });

    }

    @Override
    public void removedFromScene(Scene scene) {
            renderMap.clear();
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void render(Window window, float deltaTime) {
        for(Shader shader : renderMap.keySet()){
            shader.start(scene);
            for(Mesh mesh : renderMap.get(shader).keySet()){
                mesh.bind(scene, window);
                for(Texture texture : renderMap.get(shader).get(mesh).keySet()){
                    GL20.glEnable(GL11.GL_TEXTURE);
                    GL20.glActiveTexture(GL20.GL_TEXTURE0);
                    texture.bind(scene);
                    for(Material material : renderMap.get(shader).get(mesh).get(texture).keySet()){
                        for(Entity entity : renderMap.get(shader).get(mesh).get(texture).get(material)){
                            material.loadValues(shader, window.camera, entity);
                            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
                        }
                    }
                    GL20.glDisable(GL11.GL_TEXTURE);
                    texture.unbind();
                }
                mesh.unbind();
            }
            shader.stop();
        }
    }
}
