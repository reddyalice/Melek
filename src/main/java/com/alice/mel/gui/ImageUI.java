package com.alice.mel.gui;

import com.alice.mel.graphics.Window;
import org.joml.Vector2f;

public class ImageUI extends UIElement {

    public ImageUI(String textureName, Vector2f position, Vector2f scale){
        this.guiMaterial.textureName = textureName;
        this.position.set(position, 0);
        this.scale.set(scale, 0);
    }

    @Override
    protected void Update(float deltaTime) {

    }

    @Override
    protected void Render(Window window, float deltaTime) {

    }
}
