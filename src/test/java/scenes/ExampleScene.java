package scenes;

import com.alice.mel.components.BatchRenderingComponent;
import com.alice.mel.components.Component;
import com.alice.mel.components.RenderingComponent;
import com.alice.mel.components.TransformComponent;
import com.alice.mel.engine.*;
import com.alice.mel.graphics.*;
import com.alice.mel.graphics.materials.BatchedBasic3DMaterial;
import com.alice.mel.graphics.materials.GUIMaterial;
import com.alice.mel.graphics.materials.SpriteMaterial;
import com.alice.mel.graphics.shaders.Batched3DShader;
import com.alice.mel.graphics.shaders.BatchedSpriteShader;
import com.alice.mel.graphics.shaders.SpriteShader;
import com.alice.mel.systems.BatchedRenderingSystem;
import com.alice.mel.utils.maths.MathUtils;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class ExampleScene extends SceneAdaptor {


    Window w;
    Window w2;
    Window w3;
    int en1;
    @Override
    public void Init(Window loaderWindow, Scene scene) {

        Texture texture = new Texture("src/test/resources/textures/cactus.png");
        Texture textureC = new Texture("src/test/resources/textures/cardedge.png");
        Mesh mesh = new Mesh("src/test/resources/models/cactus.obj");
        BatchMaterial material = new BatchedBasic3DMaterial();
        material.textureName = "Texture1";
        addShader(BatchedSpriteShader.class);
        addShader(Batched3DShader.class);
        addTexture("Texture1", texture);
        addTexture("Texture2", textureC);
        addMesh("Mesh", mesh);
        //scene.loadMesh("Quad3D");




       // game.assetManager.addMesh("Mesh1", mesh);

        w = createWindow(CameraType.Orthographic, "Test", 640, 480, false);
        w2 = createWindow(CameraType.Orthographic, "Test1", 640, 480, true);
        w2.setDecorated(false);

        w3 = createWindow(CameraType.Orthographic, "Test2", 640, 480, true);
        addSystem(new BatchedRenderingSystem(Game.assetManager));


       TransformComponent tc = new TransformComponent();
        tc.scale.set(100, 100, 100);
        tc.position.set(0,0, -100);

        int en = createEntity(tc, new BatchRenderingComponent( "Mesh", "Texture1", material));

        TransformComponent tc1 = tc.Clone();
        tc1.scale.set(50, 50, 50);

        BatchedBasic3DMaterial mat = new BatchedBasic3DMaterial();
       // mat.textureDivision.set(10f, 1);
        en1 = createEntity(tc1, new BatchRenderingComponent( "Mesh", "Texture1",  mat));


        w2.update.add("move", x -> MathUtils.LookRelativeTo(w2, w));
        w3.update.add("move", x -> MathUtils.LookRelativeTo(w3, w));

    }

    @Override
    public void PreUpdate(float deltaTime) {

    }

    private final Vector2i move = new Vector2i(0,0);

    float diff;
    boolean prevKPressed;
    @Override
    public void Update(float deltaTime) {


        if(!prevKPressed && getKeyPressed(GLFW.GLFW_KEY_K))
            Objects.requireNonNull(Game.assetManager.getMesh("Mesh")).drawWireframe = !Objects.requireNonNull(Game.assetManager.getMesh("Quad")).drawWireframe;

        prevKPressed = getKeyPressed(GLFW.GLFW_KEY_K);

        if(getKeyPressed(GLFW.GLFW_KEY_G))
                if(scene.getForAny(BatchRenderingComponent.class).size() > 1)
                    scene.removeEntity(scene.getForAny(BatchRenderingComponent.class).get(MathUtils.random.nextInt(scene.getForAny(BatchRenderingComponent.class).size())));

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
        diff +=  move.x * deltaTime;
        entityManager.getComponent(en1, TransformComponent.class).position.add( move.x, -move.y, 0);
        entityManager.getComponent(en1, BatchRenderingComponent.class).material.textureOffset.set(diff % 10,0);
        w3.translate(move.x, move.y);


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
    public void entityAdded(int entity) {

    }

    @Override
    public void entityModified(int entity, Component component) {

    }

    @Override
    public void entityRemoved(int entity) {

    }
}
