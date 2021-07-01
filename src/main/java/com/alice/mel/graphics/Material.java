package com.alice.mel.graphics;

import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Entity;
import com.alice.mel.engine.Scene;
import com.alice.mel.utils.collections.Array;

public abstract class Material {
    public final Class<? extends  Shader> shaderClass;
    public final Array<Object> data = new Array<>();
    public final Array<String> textureNames = new Array<>();
    public Material(Class<? extends  Shader> shaderClass){
        this.shaderClass = shaderClass;
    }

    public abstract void loadValues(AssetManager assetManager, Scene scene, Camera camera);
    public abstract void loadEntity(AssetManager assetManager, Scene scene, Camera camera, Entity entity);

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
