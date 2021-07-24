package com.alice.mel.gui;

import com.alice.mel.graphics.Window;
import com.alice.mel.utils.collections.Array;
import com.alice.mel.utils.collections.ImmutableArray;
import org.joml.Vector2f;

public abstract class UIElement {

    public final Vector2f position = new Vector2f();
    public final Vector2f size = new Vector2f();
    public float rotation = 0;

    private UIElement parent;
    private final Array<UIElement> children = new Array<>();

    public final void addChild(UIElement child){
        if(!children.contains(child, false))
            children.add(child);
        child.parent = this;
    }

    public final void removeChild(UIElement child){
        children.removeValue(child, false);
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


    public final void update(float deltaTime){
        Update(deltaTime);
        for(UIElement child : children){
            child.update(deltaTime);
        }
    }

    public final void render(Window window, float deltaTime){
        Render(window, deltaTime);
        for(UIElement child : children){
            child.render(window, deltaTime);
        }
    }

    protected abstract void Update(float deltaTime);
    protected abstract void Render(Window window, float deltaTime);

}
