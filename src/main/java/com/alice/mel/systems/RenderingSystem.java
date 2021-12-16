package com.alice.mel.systems;

import com.alice.mel.components.Component;
import com.alice.mel.components.RenderingComponent;
import com.alice.mel.components.TransformComponent;
import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.*;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.ImmutableArray;
import com.alice.mel.utils.collections.ObjectMap;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.HashMap;
import java.util.Objects;

public class RenderingSystem extends ComponentSystem{

    protected final AssetManager assetManager;
    protected final ObjectMap<Class<? extends Shader>, HashMap<String, HashMap<Material, Array<Integer>>>> renderMap = new ObjectMap<>();

    public RenderingSystem(AssetManager assetManager){
        this(0, assetManager);
    }
    public RenderingSystem(int priority, AssetManager assetManager){
        super(priority);
        this.assetManager = assetManager;
    }


    @Override
    public void addedToScene(Scene scene) {
        ImmutableArray<Integer> entities = scene.getForAll(RenderingComponent.class, TransformComponent.class);

        for(int entity : entities) {

            RenderingComponent rend = entityManager.getComponent(entity, RenderingComponent.class);

            Material material = rend.material;
            var batch0 = renderMap.get(material.shaderClass);
            if (batch0 != null) {
                var batch1 = batch0.get(rend.meshName);
                if (batch1 != null) {
                    var batch2 = batch1.get(material);
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
                    Array<Integer> batch2 = new Array<>();
                    batch2.add(entity);
                    batch1.put(material, batch2);
                    batch0.put(rend.meshName, batch1);
                    renderMap.put(material.shaderClass, batch0);
                }
            } else {
                batch0 = new HashMap<>();
                HashMap<Material, Array<Integer>> batch1 = new HashMap<>();
                Array<Integer> batch2 = new Array<>();
                batch2.add(entity);
                batch1.put(material, batch2);
                batch0.put(rend.meshName, batch1);
                renderMap.put(material.shaderClass, batch0);
            }
        }
        entityManager.entityAdded.add("RenderingSystem", entity -> {
            if(entityManager.hasComponent(entity, RenderingComponent.class)) {
                RenderingComponent rend = entityManager.getComponent(entity, RenderingComponent.class);

                Material material = rend.material;
                var batch0 = renderMap.get(material.shaderClass);
                if (batch0 != null) {
                    var batch1 = batch0.get(rend.meshName);
                    if (batch1 != null) {
                        var batch2 = batch1.get(material);
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
                        Array<Integer> batch2 = new Array<>();
                        batch2.add(entity);
                        batch1.put(material, batch2);
                        batch0.put(rend.meshName, batch1);
                        renderMap.put(material.shaderClass, batch0);
                    }
                } else {
                    batch0 = new HashMap<>();
                    HashMap<Material, Array<Integer>> batch1 = new HashMap<>();
                    Array<Integer> batch2 = new Array<>();
                    batch2.add(entity);
                    batch1.put(material, batch2);
                    batch0.put(rend.meshName, batch1);
                    renderMap.put(material.shaderClass, batch0);
                }
            }

        });

        entityManager.entityModified.add("RenderingSystem", entityComponentPair -> {

            int entity = entityComponentPair.getValue0();
            Component comp = entityComponentPair.getValue1();

            if (comp instanceof RenderingComponent ||comp instanceof TransformComponent) {
                if(entityManager.hasComponent(entity, RenderingComponent.class) && entityManager.hasComponent(entity, TransformComponent.class)) {
                    RenderingComponent rend = entityManager.getComponent(entity, RenderingComponent.class);

                    Material material = rend.material;
                    var batch0 = renderMap.get(material.shaderClass);
                    if (batch0 != null) {
                        var batch1 = batch0.get(rend.meshName);
                        if (batch1 != null) {
                            Array<Integer> batch2 = batch1.get(material);
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
                            Array<Integer> batch2 = new Array<>();
                            batch2.add(entity);
                            batch1.put(material, batch2);
                            batch0.put(rend.meshName, batch1);
                            renderMap.put(material.shaderClass, batch0);
                        }
                    } else {
                        batch0 = new HashMap<>();
                        HashMap<Material, Array<Integer>> batch1 = new HashMap<>();
                        Array<Integer> batch2 = new Array<>();
                        batch2.add(entity);
                        batch1.put(material, batch2);
                        batch0.put(rend.meshName, batch1);
                        renderMap.put(material.shaderClass, batch0);
                    }
                }
                else {
                    if (comp instanceof RenderingComponent) {
                        RenderingComponent rend = (RenderingComponent) comp;
                        Material material = rend.material;


                        var batch0 = renderMap.get(material.shaderClass);
                        if (batch0 != null) {
                            var batch1 = batch0.get(rend.meshName);
                            if (batch1 != null) {
                                var batch2 = batch1.get(material);
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
            }

        });

        entityManager.entityRemoved.add("RenderingSystem", entity -> {
            if(entityManager.hasComponent(entity, RenderingComponent.class)) {
                RenderingComponent rend = entityManager.getComponent(entity, RenderingComponent.class);
                Material material = rend.material;


                var batch0 = renderMap.get(material.shaderClass);
                if (batch0 != null) {
                    var batch1 = batch0.get(rend.meshName);
                    if (batch1 != null) {
                        var batch2 = batch1.get(material);
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
                if(Objects.requireNonNull(assetManager.getMesh(meshName)).drawWireframe) GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_LINE);
                    for(Material material : renderMap.get(shaderClass).get(meshName).keySet()){
                        GL20.glEnable(GL11.GL_TEXTURE);
                        GL20.glActiveTexture(GL20.GL_TEXTURE0);
                        material.loadValues(assetManager, scene, window);
                        for(int entity : renderMap.get(shaderClass).get(meshName).get(material)){
                            material.loadElement(assetManager, scene, window, entityManager.getComponent(entity, TransformComponent.class));

                            GL11.glDrawElements(GL11.GL_TRIANGLES, Objects.requireNonNull(assetManager.getMesh(meshName)).getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

                        }
                        GL20.glDisable(GL11.GL_TEXTURE);
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
                    }
                if(Objects.requireNonNull(assetManager.getMesh(meshName)).drawWireframe) GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_FILL);
            }
            Objects.requireNonNull(assetManager.getShader(shaderClass)).stop();
        }
    }
}
