package com.alice.mel.gui;

import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.Window;
import com.alice.mel.graphics.shaders.GUIShader;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.ImmutableArray;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.Objects;

public abstract class UIElement {

    public final Vector2f position = new Vector2f();
    public final Vector2f size = new Vector2f();
    public float rotation = 0;

    public String textureName = "null";
    public final Vector4f color = new Vector4f(1,1,1,1);
    public final Vector2f textureOffset = new Vector2f(0,0);
    public final Vector2f textureScale = new Vector2f(1,1);




    protected AssetManager assetManager;
    protected GUIShader shader;
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
        GL20.glEnable(GL11.GL_TEXTURE);
        GL20.glActiveTexture(GL20.GL_TEXTURE0);
        assetManager.getTexture(textureName).bind(scene);
        shader.loadElementValues(this);
        shader.loadOffset(textureOffset, textureScale);
        GL11.glDrawElements(GL11.GL_TRIANGLES, Objects.requireNonNull(assetManager.getMesh("Quad")).getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        for(UIElement child : children){
            child.render(window, deltaTime);
        }
        GL20.glDisable(GL11.GL_TEXTURE);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    protected abstract void Update(float deltaTime);
    protected abstract void Render(Window window, float deltaTime);

}
