package com.alice.mel.graphics;

import com.alice.mel.utils.reflections.ClassReflection;

public abstract class Material {
    public final Class<? extends  Shader> shaderClass;
    public Material(Class<? extends  Shader> shaderClass){
        this.shaderClass = shaderClass;
    }
    public abstract void loadValues(Shader shader);
}
