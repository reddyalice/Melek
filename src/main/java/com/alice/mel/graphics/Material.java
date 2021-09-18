package com.alice.mel.graphics;

import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Element;
import com.alice.mel.engine.Entity;
import com.alice.mel.engine.Scene;
import com.alice.mel.utils.collections.Array;

import java.io.Serializable;

/**
 * Parent Class for Classes that carry shader properties
 */
public abstract class Material implements Serializable {

    public String textureName = "null";
    public final Class<? extends  Shader> shaderClass;
    public Material(Class<? extends  Shader> shaderClass){
        this.shaderClass = shaderClass;
    }

    /**
     * Loads Values to the Shader
     * @param assetManager Asset Manager Shader registered to
     * @param scene Scene shader loaded to
     * @param window Window shader rendering to
     */
    public abstract void loadValues(AssetManager assetManager, Scene scene, Window window);

    /**
     * Load Entity specific Values to the shader
     * @param assetManager
     * @param scene
     * @param window
     * @param element
     */
    public abstract void loadElement(AssetManager assetManager, Scene scene, Window window, Element element);

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
