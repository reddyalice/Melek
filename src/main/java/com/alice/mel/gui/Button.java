package com.alice.mel.gui;

import com.alice.mel.engine.AssetManager;
import com.alice.mel.graphics.Window;
import com.alice.mel.utils.Event;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Button extends UIElement {

    public String idleTextureName;
    public final Vector4f idleColor = new Vector4f(1,1,1,1);

    public String hoveringTextureName;
    public final Vector4f hoveringColor = new Vector4f(1,1,1,1);

    public String pressedTextureName;
    public final Vector4f pressedColor = new Vector4f(1,1,1,1);

    public final Event<Float> onClick = new Event<>();

    private final AssetManager assetManager;

    public Button(AssetManager assetManager, String textureName, Vector2f position){
        this.idleTextureName = textureName;
        this.hoveringTextureName = textureName;
        this.pressedTextureName = textureName;
        this.position.set(position);
        this.assetManager = assetManager;
        this.size.set(assetManager.getTexture(textureName).getWidth(), assetManager.getTexture(textureName).getHeight());
    }


    @Override
    protected void Update(float deltaTime) {

    }

    @Override
    protected void Render(Window window, float deltaTime) {

    }
}
