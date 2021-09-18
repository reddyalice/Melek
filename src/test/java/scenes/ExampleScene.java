package scenes;

import com.alice.mel.components.BatchRenderingComponent;
import com.alice.mel.components.Component;
import com.alice.mel.engine.*;
import com.alice.mel.graphics.*;
import com.alice.mel.graphics.materials.GUIMaterial;
import com.alice.mel.graphics.materials.SpriteMaterial;
import com.alice.mel.graphics.shaders.BatchedSpriteShader;
import com.alice.mel.graphics.shaders.SpriteShader;
import com.alice.mel.systems.BatchedRenderingSystem;
import com.alice.mel.utils.maths.MathUtils;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

public class ExampleScene extends SceneAdaptor {


    Window w;
    Window w2;
    @Override
    public void Init(Window loaderWindow, Scene scene) {

        Texture texture = new Texture("src/test/resources/textures/cactus.png");
        Texture textureC = new Texture("src/test/resources/textures/cardedge.png");
        BatchMaterial material = new GUIMaterial();
        material.textureName = "Texture1";
        addShader(BatchedSpriteShader.class);
        addTexture("Texture1", texture);
        addTexture("Texture2", textureC);
        scene.loadMesh("Quad");
       // game.assetManager.addMesh("Mesh1", mesh);

        w = createWindow(CameraType.Orthographic, "Test", 640, 480, false);
        w2 = createWindow(CameraType.Orthographic, "Test1", 640, 480, true);
        w2.setDecorated(false);

        Window w3 = createWindow(CameraType.Orthographic, "Test2", 640, 480, true);
        addSystem(new BatchedRenderingSystem(Game.assetManager));


        Entity en = createEntity();
        en.scale.set(100, 100, 100);
        en.position.set(0,0, -100);
        en.addComponent(new BatchRenderingComponent( "Quad", "Texture1", material));
        Entity en1 = createEntity();
        en1.scale.set(50, 50, 50);
        en1.position.set(0,100, -100);
        en1.addComponent(new BatchRenderingComponent( "Quad", "Texture1",  new GUIMaterial()));


        w2.update.add("move", x -> MathUtils.LookRelativeTo(w2, w));
        w3.update.add("move", x -> MathUtils.LookRelativeTo(w3, w));

    }

    @Override
    public void PreUpdate(float deltaTime) {

    }

    private final Vector2i move = new Vector2i(0,0);

    @Override
    public void Update(float deltaTime) {
        if(getKeyPressed(GLFW.GLFW_KEY_G))
            if(scene.getEntitiesFor(BatchRenderingComponent.class).size() > 1)
                scene.removeEntity(scene.getEntitiesFor(BatchRenderingComponent.class).get(0));

        move.set(0,0);
        if(getKeyPressed(GLFW.GLFW_KEY_W))
            move.add(0,-1);
        if(getKeyPressed(GLFW.GLFW_KEY_S))
            move.add(0,1);
        if(getKeyPressed(GLFW.GLFW_KEY_A))
            move.add(-1,0);
        if(getKeyPressed(GLFW.GLFW_KEY_D))
            move.add(1,0);

        Vector2f cursorPos = w2.getCursorPosition();
        Vector2i winPos = w2.getPosition();
        Vector2i winSize = w2.getSize();
        w2.setPosition((int)cursorPos.x  + winPos.x - winSize.x /2, (int)cursorPos.y + winPos.y - winSize.y /2);

        w.translate(move.x, move.y);


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
    public void entityModified(Entity entity, Component component) {

    }

    @Override
    public void entityRemoved(Entity entity) {
    }
}
