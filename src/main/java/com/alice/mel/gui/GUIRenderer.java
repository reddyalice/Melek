package com.alice.mel.gui;

import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.MeshBatch;
import com.alice.mel.graphics.Window;
import com.alice.mel.graphics.shaders.BatchedSpriteShader;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.SnapshotArray;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class GUIRenderer {

    private final Canvas canvas;
    private final BatchedSpriteShader shader;
    private final AssetManager assetManager;
    private final SnapshotArray<MeshBatch> meshBatches;
    private final Scene scene;

    public GUIRenderer(AssetManager assetManager, Scene scene){
        this.scene = scene;
        this.assetManager = assetManager;
        meshBatches = new SnapshotArray<>();

        scene.loadTexture("null");
        shader = assetManager.getShader(BatchedSpriteShader.class);
        scene.loadShader(BatchedSpriteShader.class);

        canvas = new Canvas(scene, assetManager, shader);
    }

    public void Update(float deltaTime){
        canvas.update(deltaTime);
    }

    public void Render(Window window, float deltaTime){
        canvas.render(window, deltaTime);
    }

    public void addUIElement(UIElement element){
        canvas.addChild(element);

        MeshBatch batchA = null;
        for(MeshBatch batch : meshBatches)
            if(batch.hasRoom()) {
                if (batch.hasTexture(element.guiMaterial.textureName)) {
                    batchA = batch;
                    break;
                }else
                {
                    if(batch.hasTextureRoom())
                        batchA = batch;
                }
            }

        if(batchA != null) {
            if(!batchA.addUIElement(element)){
                MeshBatch mb = new MeshBatch(assetManager, "Quad", 100, 0);
                scene.loadMeshBatch(meshBatches.size + "", mb);
                meshBatches.add(mb);
                mb.addUIElement(element);
            }
        }
        else{
                MeshBatch mb = new MeshBatch(assetManager, "Quad", 100, 0);
                scene.loadMeshBatch(meshBatches.size + "", mb);
                meshBatches.add(mb);
                mb.addUIElement(element);
        }
    }

    public void removeUIElement(UIElement element){
        canvas.removeChild(element);
        for(MeshBatch batch : meshBatches)
        {
            int i = batch.getNumberOfElements();
            batch.removeUIElement(element);
            int j = batch.getNumberOfElements();
            if(j <= 0)
                meshBatches.removeValue(batch, false);
            if(i - j != 0)
                break;
        }
    }


    private class Canvas extends UIElement{

        private Canvas(Scene scene, AssetManager assetManager, BatchedSpriteShader shader){
            this.scene = scene;
            this.assetManager = assetManager;
        }

        private final Matrix4f projectionMatrix = new Matrix4f();
        private final Matrix4f viewMatrix = new Matrix4f();

        private final Vector3f position = new Vector3f();
        private final Vector3f direction = new Vector3f(0,0,-1);
        private final Vector3f up = new Vector3f(0,1,0);
        private final Vector3f tmp = new Vector3f();


        @Override
        void render(Window window, float deltaTime) {
            Vector2i size = window.getSize();
            shader.start(scene);
            shader.loadProjectionMatrix(projectionMatrix.identity().setOrtho( -size.x / 2f,  (size.x / 2f),  -(size.y / 2f),  size.y / 2f, 0, 1000));
            shader.loadViewMatrix(viewMatrix.identity().lookAt(position, tmp.set(position).add(direction), up));
            for(MeshBatch batch : meshBatches)
                batch.bind(scene, window);
            super.render(window, deltaTime);
            shader.stop();
        }

        @Override
        protected void Update(float deltaTime) { }
        @Override
        protected void Render(Window window, float deltaTime) { }
    }
}



