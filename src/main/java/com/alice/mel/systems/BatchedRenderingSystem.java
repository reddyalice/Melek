package com.alice.mel.systems;

import com.alice.mel.components.BatchRenderingComponent;
import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Game;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.MeshBatch;
import com.alice.mel.graphics.Shader;
import com.alice.mel.graphics.Window;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.ImmutableArray;
import com.alice.mel.utils.collections.ObjectMap;

import java.util.Objects;


public class BatchedRenderingSystem extends ComponentSystem{

    private final ObjectMap<Class<? extends Shader>, Array<MeshBatch>> batches = new ObjectMap<>();

    public BatchedRenderingSystem(){
        this(0);
    }
    public BatchedRenderingSystem(int priority){
        super(priority);

    }

    @Override
    public void addedToScene(Scene scene) {
        ImmutableArray<Integer> entities = scene.getForAll(BatchRenderingComponent.class);
        for(int entity : entities){
            BatchRenderingComponent component = entityManager.getComponent(entity, BatchRenderingComponent.class);
            if(batches.get(component.material.shaderClass) == null)
                batches.put(component.material.shaderClass, new Array<>());

            MeshBatch batchA = null;
            for(Class<? extends Shader> batchShader : batches.keys()) {
                Array<MeshBatch> batchess = batches.get(batchShader);
                for (MeshBatch batch : batchess) {
                    if (batch.hasRoom()) {
                        if (batch.getMesh() == Game.assetManager.getMesh(component.meshName)) {
                            if (batch.hasTexture(component.material.textureName)) {
                                batchA = batch;
                                break;
                            } else {
                                if (batch.hasTextureRoom()) {
                                    batchA = batch;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if(batchA != null) {
                if(!batchA.addEntity(entity)){
                    MeshBatch mb = new MeshBatch(scene, component.meshName, 16, 0);
                    scene.loadMeshBatch(mb + "", mb);
                    batches.get(component.material.shaderClass).add(mb);
                    mb.addEntity(entity);
                }
            }
            else{
                MeshBatch mb = new MeshBatch(scene, component.meshName, 16, 0);
                scene.loadMeshBatch(mb + "", mb);
                batches.get(component.material.shaderClass).add(mb);
                mb.addEntity(entity);
            }
        }

        entityManager.entityAdded.add("BatchedRenderingSystem", entity -> {
            if(entityManager.hasComponent(entity, BatchRenderingComponent.class)) {
                BatchRenderingComponent component = entityManager.getComponent(entity, BatchRenderingComponent.class);
                if (batches.get(component.material.shaderClass) == null)
                    batches.put(component.material.shaderClass, new Array<>());

                MeshBatch batchA = null;
                for (Class<? extends Shader> batchShader : batches.keys()) {
                    Array<MeshBatch> batchess = batches.get(batchShader);
                    for (MeshBatch batch : batchess) {
                        if (batch.hasRoom()) {
                            if (batch.getMesh() == Game.assetManager.getMesh(component.meshName)) {
                                if (batch.hasTexture(component.material.textureName)) {
                                    batchA = batch;
                                    break;
                                } else {
                                    if (batch.hasTextureRoom()) {
                                        batchA = batch;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if (batchA != null) {
                    if (!batchA.addEntity(entity)) {
                        MeshBatch mb = new MeshBatch(scene, component.meshName, 16, 0);
                        scene.loadMeshBatch(mb + "", mb);
                        batches.get(component.material.shaderClass).add(mb);
                        mb.addEntity(entity);
                    }
                } else {
                    MeshBatch mb = new MeshBatch(scene, component.meshName, 16, 0);
                    scene.loadMeshBatch(mb + "", mb);
                    batches.get(component.material.shaderClass).add(mb);
                    mb.addEntity(entity);
                }
            }
        });


        entityManager.entityModified.add("BatchedRenderingSystem", entityComponentPair -> {
            int entity = entityComponentPair.getValue0();

            if(entityComponentPair.getValue1() instanceof BatchRenderingComponent){
                BatchRenderingComponent component = (BatchRenderingComponent) entityComponentPair.getValue1();
                if(entityManager.hasComponent(entity, BatchRenderingComponent.class)){

                    if (batches.get(component.material.shaderClass) == null)
                        batches.put(component.material.shaderClass, new Array<>());

                    MeshBatch batchA = null;
                    for (Class<? extends Shader> batchShader : batches.keys()) {
                        Array<MeshBatch> batchess = batches.get(batchShader);
                        for (MeshBatch batch : batchess) {
                            if (batch.hasRoom()) {
                                if (batch.getMesh() == Game.assetManager.getMesh(component.meshName)) {
                                    if (batch.hasTexture(component.material.textureName)) {
                                        batchA = batch;
                                        break;
                                    } else {
                                        if (batch.hasTextureRoom()) {
                                            batchA = batch;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (batchA != null) {
                        if (!batchA.addEntity(entity)) {
                            MeshBatch mb = new MeshBatch(scene, component.meshName, 16, 0);
                            scene.loadMeshBatch(mb + "", mb);
                            batches.get(component.material.shaderClass).add(mb);
                            mb.addEntity(entity);
                        }
                    } else {
                        MeshBatch mb = new MeshBatch(scene, component.meshName, 16, 0);
                        scene.loadMeshBatch(mb + "", mb);
                        batches.get(component.material.shaderClass).add(mb);
                        mb.addEntity(entity);
                    }
                }else
                {
                    Array<MeshBatch> meshBatches = batches.get(component.material.shaderClass);
                    for(MeshBatch batch : meshBatches){
                        int i = batch.getNumberOfElements();
                        batch.removeEntity(entity);
                        int j = batch.getNumberOfElements();
                        if(j <= 0)
                            meshBatches.removeValue(batch, false);
                        if(i - j != 0)
                            break;
                    }
                    if (meshBatches.isEmpty())
                        batches.remove(component.material.shaderClass);
                }
            }
        });

        entityManager.entityRemoved.add("BatchedRenderingSystem", entity -> {
            if(entityManager.hasComponent(entity, BatchRenderingComponent.class)){
                BatchRenderingComponent component = entityManager.getComponent(entity, BatchRenderingComponent.class);
                Array<MeshBatch> meshBatches = batches.get(component.material.shaderClass);
                for(MeshBatch batch : meshBatches){
                    int i = batch.getNumberOfElements();
                    batch.removeEntity(entity);
                    int j = batch.getNumberOfElements();
                    if(j <= 0)
                        meshBatches.removeValue(batch, false);
                    if(i - j != 0)
                        break;
                }
                if (meshBatches.isEmpty())
                    batches.remove(component.material.shaderClass);
            }
        });


    }

    @Override
    public void removedFromScene(Scene scene) {

    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void render(Window window, float deltaTime) {

        for ( Class<? extends Shader> shader : batches.keys()) {
            Objects.requireNonNull(Game.assetManager.getShader(shader)).start(scene);
            Objects.requireNonNull(Game.assetManager.getShader(shader)).loadValues(null, scene, window);
            for(MeshBatch batch : batches.get(shader))
                batch.bind(scene, window);
            Objects.requireNonNull(Game.assetManager.getShader(shader)).stop();
        }
    }
}
