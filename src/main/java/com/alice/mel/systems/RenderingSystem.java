package com.alice.mel.systems;

import com.alice.mel.components.RenderingComponent;
import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Entity;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.*;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.ImmutableArray;
import com.alice.mel.utils.collections.ObjectMap;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.HashMap;
import java.util.Objects;

public class RenderingSystem extends ComponentSystem{

    protected final AssetManager assetManager;
    protected final ObjectMap<Class<? extends Shader>, HashMap<String, HashMap<Material, Array<Entity>>>> renderMap = new ObjectMap<>();

    public RenderingSystem(AssetManager assetManager){
        this.assetManager = assetManager;
    }
    public RenderingSystem(int priority, AssetManager assetManager){
        super(priority);
        this.assetManager = assetManager;
    }

    @Override
    public void addedToScene(Scene scene) {
        ImmutableArray<Entity> entities = scene.getEntitiesFor(RenderingComponent.class);

        if(entities != null)
        for(Entity entity : entities){

            RenderingComponent rend = entity.getComponent(RenderingComponent.class);

            for(Material material : rend.materials) {
                HashMap<String, HashMap<Material, Array<Entity>>> batch0 = renderMap.get(material.shaderClass);
                if (batch0 != null) {
                    HashMap<Material, Array<Entity>> batch1 = batch0.get(rend.meshName);
                    if (batch1 != null) {
                        Array<Entity> batch2 = batch1.get(material);
                        if (batch2 != null) {
                            batch2.add(entity);
                            batch1.put(material, batch2);
                            batch0.put(rend.meshName, batch1);
                            renderMap.put(material.shaderClass, batch0);
                        } else {
                            batch2 = new Array<>();
                            batch2.add(entity);
                            batch1.put(material, batch2);
                            batch0.put(rend.meshName, batch1);
                            renderMap.put(material.shaderClass, batch0);
                        }
                    } else {
                        batch1 = new HashMap<>();
                        Array<Entity> batch2 = new Array<>();
                        batch2.add(entity);
                        batch1.put(material, batch2);
                        batch0.put(rend.meshName, batch1);
                        renderMap.put(material.shaderClass, batch0);
                    }
                } else {
                    batch0 = new HashMap<>();
                    HashMap<Material, Array<Entity>> batch1 = new HashMap<>();
                    Array<Entity> batch2 = new Array<>();
                    batch2.add(entity);
                    batch1.put(material, batch2);
                    batch0.put(rend.meshName, batch1);
                    renderMap.put(material.shaderClass, batch0);
                }
            }
        }
        scene.entityAdded.add("RenderingSystem", entity -> {
            if(entity.hasComponent(RenderingComponent.class)) {
                RenderingComponent rend = entity.getComponent(RenderingComponent.class);

                for(Material material : rend.materials) {


                    HashMap<String, HashMap<Material, Array<Entity>>> batch0 = renderMap.get(material.shaderClass);
                    if (batch0 != null) {
                        HashMap<Material, Array<Entity>> batch1 = batch0.get(rend.meshName);
                        if (batch1 != null) {
                            Array<Entity> batch2 = batch1.get(material);
                            if (batch2 != null) {
                                batch2.add(entity);
                                batch1.put(material, batch2);
                                batch0.put(rend.meshName, batch1);
                                renderMap.put(material.shaderClass, batch0);
                            } else {
                                batch2 = new Array<>();
                                batch2.add(entity);
                                batch1.put(material, batch2);
                                batch0.put(rend.meshName, batch1);
                                renderMap.put(material.shaderClass, batch0);
                            }
                        } else {
                            batch1 = new HashMap<>();
                            Array<Entity> batch2 = new Array<>();
                            batch2.add(entity);
                            batch1.put(material, batch2);
                            batch0.put(rend.meshName, batch1);
                            renderMap.put(material.shaderClass, batch0);
                        }
                    } else {
                        batch0 = new HashMap<>();
                        HashMap<Material, Array<Entity>> batch1 = new HashMap<>();
                        Array<Entity> batch2 = new Array<>();
                        batch2.add(entity);
                        batch1.put(material, batch2);
                        batch0.put(rend.meshName, batch1);
                        renderMap.put(material.shaderClass, batch0);
                    }
                }
            }
        });

        scene.entityModified.add("RenderingSystem", entity -> {
            if(entity.hasComponent(RenderingComponent.class)) {
                RenderingComponent rend = entity.getComponent(RenderingComponent.class);

                for(Material material : rend.materials) {


                    HashMap<String, HashMap<Material, Array<Entity>>> batch0 = renderMap.get(material.shaderClass);
                    if (batch0 != null) {
                        HashMap<Material, Array<Entity>> batch1 = batch0.get(rend.meshName);
                        if (batch1 != null) {
                            Array<Entity> batch2 = batch1.get(material);
                            if (batch2 != null) {
                                batch2.add(entity);
                                batch1.put(material, batch2);
                                batch0.put(rend.meshName, batch1);
                                renderMap.put(material.shaderClass, batch0);
                            } else {
                                batch2 = new Array<>();
                                batch2.add(entity);
                                batch1.put(material, batch2);
                                batch0.put(rend.meshName, batch1);
                                renderMap.put(material.shaderClass, batch0);
                            }
                        } else {
                            batch1 = new HashMap<>();
                            Array<Entity> batch2 = new Array<>();
                            batch2.add(entity);
                            batch1.put(material, batch2);
                            batch0.put(rend.meshName, batch1);
                            renderMap.put(material.shaderClass, batch0);
                        }
                    } else {
                        batch0 = new HashMap<>();
                        HashMap<Material, Array<Entity>> batch1 = new HashMap<>();
                        Array<Entity> batch2 = new Array<>();
                        batch2.add(entity);
                        batch1.put(material, batch2);
                        batch0.put(rend.meshName, batch1);
                        renderMap.put(material.shaderClass, batch0);
                    }
                }
            }
        });

        scene.entityRemoved.add("RenderingSystem", entity -> {
            if(entity.hasComponent(RenderingComponent.class)){
                RenderingComponent rend = entity.getComponent(RenderingComponent.class);
                for(Material material : rend.materials) {


                    HashMap<String, HashMap<Material, Array<Entity>>> batch0 = renderMap.get(material.shaderClass);
                    if (batch0 != null) {
                        HashMap<Material, Array<Entity>> batch1 = batch0.get(rend.meshName);
                        if (batch1 != null) {
                            Array<Entity> batch2 = batch1.get(material);
                            if (batch2 != null) {
                                batch2.removeValue(entity, false);
                                if (batch2.isEmpty()) {
                                    batch1.remove(material);
                                    batch0.remove(rend.meshName);
                                    renderMap.remove(material.shaderClass);
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
        for(Class<? extends Shader> shaderClass : renderMap.keys()){
            Objects.requireNonNull(assetManager.getShader(shaderClass)).start(scene);
            for(String meshName : renderMap.get(shaderClass).keySet()){
                Objects.requireNonNull(assetManager.getMesh(meshName)).bind(scene, window);
                    for(Material material : renderMap.get(shaderClass).get(meshName).keySet()){
                        GL20.glEnable(GL11.GL_TEXTURE);
                        GL20.glActiveTexture(GL20.GL_TEXTURE0);
                        material.loadValues(assetManager, scene, window);
                        for(Entity entity : renderMap.get(shaderClass).get(meshName).get(material)){
                            material.loadElement(assetManager, scene, window, entity);
                            GL11.glDrawElements(GL11.GL_TRIANGLES, Objects.requireNonNull(assetManager.getMesh(meshName)).getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
                        }
                        GL20.glDisable(GL11.GL_TEXTURE);
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
                    }
            }
            Objects.requireNonNull(assetManager.getShader(shaderClass)).stop();
        }
    }
}
