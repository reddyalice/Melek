package com.alice.mel.graphics;

import com.alice.mel.engine.Entity;
import com.alice.mel.utils.reflections.ClassReflection;

import java.util.Objects;

public abstract class Material {
    public final Class<? extends  Shader> shaderClass;
    public Material(Class<? extends  Shader> shaderClass){
        this.shaderClass = shaderClass;
    }
    public abstract void loadValues(Shader shader, Camera camera, Entity entity);

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
