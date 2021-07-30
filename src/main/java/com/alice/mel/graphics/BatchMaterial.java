package com.alice.mel.graphics;

import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Element;
import com.alice.mel.engine.Entity;
import com.alice.mel.engine.Scene;
import org.joml.Vector2f;
import org.joml.Vector4f;

public abstract class BatchMaterial extends Material{

    public String textureName;
    public final Vector2f textureOffset = new Vector2f(0,0);
    public final Vector2f textureScale = new Vector2f(1,1);
    public final Vector4f color = new Vector4f(1,1,1,1);

    public final Vector2f lastTextureOffset = new Vector2f();
    public final Vector2f lastTextureScale = new Vector2f();
    public final Vector4f lastColor = new Vector4f();


    public BatchMaterial(Class<? extends Shader> shaderClass) {
        super(shaderClass);
    }



}
