package com.alice.mel.graphics;

import com.alice.mel.components.TransformComponent;
import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Scene;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Parent Class for Classes that carry shader properties
 */
public abstract class Material implements Serializable {

    public String textureName = "null";
    public final Class<? extends  Shader> shaderClass;
    public final HashMap<String, VertexData> properties = new HashMap<>();

    public final Vector2f textureOffset = new Vector2f(0,0);
    public final Vector2f textureDivision = new Vector2f(1,1);
    public final Vector4f color = new Vector4f(1,1,1,1);

    public final Vector2f lastTextureOffset = new Vector2f();
    public final Vector2f lastTextureDivision = new Vector2f();
    public final Vector4f lastColor = new Vector4f();

    public Material(Class<? extends  Shader> shaderClass){
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
