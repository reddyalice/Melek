package com.alice.editor;

import com.alice.mel.engine.Entity;
import com.alice.mel.engine.Game;
import com.alice.mel.engine.SceneAdaptor;
import com.alice.mel.graphics.CameraType;
import com.alice.mel.graphics.Window;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class EditorScene extends SceneAdaptor {


    public EditorScene() {
        super(RunEditor.game);
        addToGame();
    }

    Window editorWindow;


    @Override
    public void Init(Window loaderWindow) {
        editorWindow = createWindow(CameraType.Orthographic, "Editor Window", 1024, 768, true);
    }

    @Override
    public void PreUpdate(float deltaTime) {

    }


    private boolean dragging = false;
    private final Vector2i cursorOffset = new Vector2i();

    @Override
    public void Update(float deltaTime) {
        Vector2f cursor = editorWindow.getCursorPosition();
        if(!dragging) {
            if (cursor.x >= 0 && cursor.x <= editorWindow.getSize().x
                    && cursor.y >= 0 && cursor.y <= 20)
                if (getMouseButtonPressed(0)) {
                    dragging = true;
                    cursorOffset.set((int) cursor.x, (int) cursor.y);
                }
        }else{
            Vector2i ePos = editorWindow.getPosition();
            editorWindow.setPosition(ePos.x + (int)cursor.x - cursorOffset.x, ePos.y + (int)cursor.y - cursorOffset.y);

            if(getMouseButtonReleased(0))
                dragging = false;
        }
    }

    @Override
    public void PostUpdate(float deltaTime) {

    }

    @Override
    public void PreRender(Window currentWindow, float deltaTime) {

    }

    @Override
    public void Render(Window currentWindow, float deltaTime) {

    }

    @Override
    public void PostRender(Window currentWindow, float deltaTime) {

    }

    @Override
    public void entityAdded(Entity entity) {

    }

    @Override
    public void entityModified(Entity entity) {

    }

    @Override
    public void entityRemoved(Entity entity) {

    }
}
