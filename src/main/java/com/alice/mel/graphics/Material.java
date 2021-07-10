package com.alice.mel.graphics;

import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Entity;
import com.alice.mel.engine.Scene;
import com.alice.mel.utils.collections.Array;

/**
 * Parent Class for Classes that carry shader properties
 */
public abstract class Material {
    public final Class<? extends  Shader> shaderClass;
    public final Array<Object> data = new Array<>();
    public final Array<String> textureNames = new Array<>();
    public Material(Class<? extends  Shader> shaderClass){
        this.shaderClass = shaderClass;
    }

    /**
     * Loads Values to the Shader
     * @param assetManager Asset Manager Shader registered to
     * @param scene Scene shader loaded to
     * @param camera Camera shader rendering to
     */
    public abstract void loadValues(AssetManager assetManager, Scene scene, Camera camera);

    /**
     * Load Entity specific Values to the shader
     * @param assetManager
     * @param scene
     * @param camera
     * @param entity
     */
    public abstract void loadEntity(AssetManager assetManager, Scene scene, Camera camera, Entity entity);

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
