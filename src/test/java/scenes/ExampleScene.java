package scenes;

import com.alice.mel.components.RenderingComponent;
import com.alice.mel.engine.*;
import com.alice.mel.graphics.*;
import com.alice.mel.graphics.materials.SpriteMaterial;
import com.alice.mel.graphics.shaders.SpriteShader;
import com.alice.mel.systems.RenderingSystem;
import com.alice.mel.utils.maths.MathUtils;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

public class ExampleScene extends SceneAdaptor {

    public ExampleScene(Game game) {
        super(game);
    }
    Window w;
    Window w2;
    @Override
    public void Init(Window loaderWindow) {

        Texture texture = new Texture("src/test/resources/textures/cactus.png");
        Texture textureC = new Texture("src/test/resources/textures/cardedge.png");
        Material material = new SpriteMaterial("Texture1");
        game.assetManager.addShader(SpriteShader.class);
        game.assetManager.addTexture("Texture1", texture);
        game.assetManager.addTexture("Texture2", textureC);
       // game.assetManager.addMesh("Mesh1", mesh);

        w = createWindow(CameraType.Orthographic, "Test", 640, 480, false);
        w2 = createWindow(CameraType.Orthographic, "Test1", 640, 480, true);
        w2.setDecorated(false);

        Window w3 = createWindow(CameraType.Orthographic, "Test2", 640, 480, true);


        addSystem(new RenderingSystem(game.assetManager));
        Entity en = createEntity();
        en.scale.set(100, 100, 100);
        en.position.set(0,0, -100);
        en.addComponent(new RenderingComponent( "Quad", material));
        Entity en1 = createEntity();
        en1.scale.set(50, 50, 50);
        en1.position.set(500,0, -99);
        en1.addComponent(new RenderingComponent( "Quad", material));


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
            if(scene.getEntitiesFor(RenderingComponent.class).size() > 1)
                scene.removeEntity(scene.getEntitiesFor(RenderingComponent.class).get(0));

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
        System.out.println((int)cursorPos.x + " , " + (int)cursorPos.y);


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
    public void entityModified(Entity entity) {

    }

    @Override
    public void entityRemoved(Entity entity) {
    }
}
