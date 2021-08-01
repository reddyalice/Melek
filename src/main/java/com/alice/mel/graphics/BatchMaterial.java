package com.alice.mel.graphics;

import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Element;
import com.alice.mel.engine.Entity;
import com.alice.mel.engine.Scene;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.HashMap;

public abstract class BatchMaterial{

    public String textureName;

    public final HashMap<String, VertexData> properties = new HashMap<>();

    public final Vector2f textureOffset = new Vector2f(0,0);
    public final Vector2f textureDivision = new Vector2f(1,1);
    public final Vector4f color = new Vector4f(1,1,1,1);

    public final Vector2f lastTextureOffset = new Vector2f();
    public final Vector2f lastTextureDivision = new Vector2f();
    public final Vector4f lastColor = new Vector4f();

    public final Class<? extends  Shader> shaderClass;
    public BatchMaterial(Class<? extends Shader> shaderClass) {
        this.shaderClass = shaderClass;
    }

    protected abstract boolean checkDirty();
    protected abstract void clean();

    public final boolean isDirty(){
       return  !lastTextureOffset.equals(textureOffset) || !lastTextureDivision.equals(textureDivision)
                || !lastColor.equals(color) || checkDirty();
    }

    public final void doClean(){
        lastTextureOffset.set(textureOffset);
        lastTextureDivision.set(textureDivision);
        lastColor.set(color);
        clean();
    }




}
