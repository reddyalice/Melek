package com.alice.mel.gui;

import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.InputHandler;
import com.alice.mel.graphics.Window;
import com.alice.mel.graphics.materials.SpriteMaterial;
import com.alice.mel.utils.Event;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

public class Button extends UIElement {

    public String idleTextureName;
    public final Vector4f idleColor = new Vector4f(1,1,1,1);

    public String hoveringTextureName;
    public final Vector4f hoveringColor = new Vector4f(1,1,1,1);

    public String pressedTextureName;
    public final Vector4f pressedColor = new Vector4f(1,1,1,1);

    public final Event<Float> onClick = new Event<>();



    public Button(String textureName, Vector2f position, Vector2f size){
        this.textureName = textureName;
        this.idleTextureName = textureName;
        this.hoveringTextureName = textureName;
        this.pressedTextureName = textureName;
        this.position.set(position);
        this.size.set(size);
    }


    @Override
    protected void Update(float deltaTime) {

    }


    @Override
    protected void Render(Window window, float deltaTime) {
        Vector2f cursorPos = window.getCursorPosition();
        Vector2i windowSize = window.getSize();
        cursorPos.set(cursorPos.x - windowSize.x / 2f, windowSize.y / 2f - cursorPos.y);




        if(cursorPos.x >= position.x - size.x / 2f && cursorPos.x <= position.x + size.x / 2f
            &&  cursorPos.y >= position.y - size.y / 2f && cursorPos.y <= position.y + size.y / 2f)
        {
            textureName = hoveringTextureName;
            if(InputHandler.getMouseButtonPressed(scene, 0))
                textureName = pressedTextureName;
        }else
            textureName = idleTextureName;
    }
}
