package com.alice.editor;

import com.alice.mel.engine.Entity;
import com.alice.mel.engine.Game;
import com.alice.mel.engine.SceneAdaptor;
import com.alice.mel.graphics.CameraType;
import com.alice.mel.graphics.Texture;
import com.alice.mel.graphics.Window;
import com.alice.mel.gui.Button;
import com.alice.mel.gui.GUIRenderer;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

public class EditorScene extends SceneAdaptor {


    public EditorScene() {
        super(RunEditor.game);
        addToGame();
    }

    Window editorWindow;

    private GUIRenderer guiRenderer;
    @Override
    public void Init(Window loaderWindow) {

        guiRenderer = new GUIRenderer(game.assetManager, scene);
        game.assetManager.addTexture("button", new Texture(1, 1, new int[]{255 << 24}));
        scene.loadTexture("button");
        Button b = new Button("button", new Vector2f(0,768f/2f - 10), new Vector2f(1024, 20));

        guiRenderer.addUIElement(b);

        editorWindow = createWindow(CameraType.Orthographic, "Editor Window", 1024, 768, true);
        editorWindow.setDecorated(false);

        b.onClick.add(x -> {
            editorWindow.close();
        });
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
        guiRenderer.Update(deltaTime);
    }

    @Override
    public void PostUpdate(float deltaTime) {

    }

    @Override
    public void PreRender(Window currentWindow, float deltaTime) {

    }

    @Override
    public void Render(Window currentWindow, float deltaTime) {
        guiRenderer.Render(currentWindow, deltaTime);
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
