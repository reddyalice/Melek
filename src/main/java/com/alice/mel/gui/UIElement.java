package com.alice.mel.gui;

import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Element;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.BatchMaterial;
import com.alice.mel.graphics.MeshBatch;
import com.alice.mel.graphics.Window;
import com.alice.mel.graphics.shaders.BatchedSpriteShader;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.ImmutableArray;

public abstract class UIElement extends Element {


    protected AssetManager assetManager;
    protected BatchedSpriteShader shader;
    protected final Array<MeshBatch> meshBatches = new Array<>();
    public final BatchMaterial guiMaterial = new BatchMaterial(BatchedSpriteShader.class);
    protected Scene scene;

    private UIElement parent;
    private final Array<UIElement> children = new Array<>();



    public final void addChild(UIElement child){
        if(!children.contains(child, false))
            children.add(child);
        child.assetManager = assetManager;
        child.scene = scene;
        child.shader = shader;


        child.parent = this;
    }

    public final void removeChild(UIElement child){
        children.removeValue(child, false);
        child.assetManager = null;
        child.scene = scene;
        child.shader = null;
        child.parent = null;
    }

    public final void setParent(UIElement parent){
        if(this.parent != null)
            this.parent.removeChild(this);
        parent.addChild(this);
    }

    public final UIElement getParent(){
        return parent;
    }

    public final ImmutableArray<UIElement> getChildren(){
        return new ImmutableArray<>(children);
    }


    void update(float deltaTime){
        Update(deltaTime);
        for(UIElement child : children){
            child.update(deltaTime);
        }
    }

    void render(Window window, float deltaTime){


        Render(window, deltaTime);
        for(UIElement child : children){
            child.render(window, deltaTime);
        }
    }

    protected abstract void Update(float deltaTime);
    protected abstract void Render(Window window, float deltaTime);

}
