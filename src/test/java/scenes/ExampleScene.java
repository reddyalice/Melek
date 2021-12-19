package scenes;

import com.alice.mel.components.BatchRenderingComponent;
import com.alice.mel.components.Component;
import com.alice.mel.components.RenderingComponent;
import com.alice.mel.components.TransformComponent;
import com.alice.mel.engine.*;
import com.alice.mel.graphics.*;
import com.alice.mel.graphics.Window;
import com.alice.mel.graphics.materials.BatchedBasic3DMaterial;
import com.alice.mel.graphics.shaders.Batched3DShader;
import com.alice.mel.graphics.shaders.BatchedSpriteShader;
import com.alice.mel.systems.BatchedRenderingSystem;
import com.alice.mel.systems.IteratingSystem;
import com.alice.mel.systems.RenderingSystem;
import com.alice.mel.utils.loaders.MeshLoader;
import com.alice.mel.utils.maths.MathUtils;
import materials.Basic3DMaterial;
import org.joml.*;
import org.lwjgl.glfw.GLFW;
import shaders.Basic3DShader;

import java.util.Objects;

public class ExampleScene extends SceneAdaptor {


    Window w2;
    Window w3;
    int en1;
    TransformComponent tc;
    @Override
    public void Init(Window loaderWindow, Scene scene)  {

        Texture texture = new Texture("src/test/resources/textures/cactus.png");
        Texture textureC = new Texture("src/test/resources/textures/cardedge.png");
        Mesh mesh = new Mesh("src/test/resources/models/cactus.obj");
        try {
            MeshLoader.loadMesh("Mesh", "src/test/resources/models/cactus.obj", "src/test/resources/textures");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Material material = new BatchedBasic3DMaterial();
        addShader(BatchedSpriteShader.class);
        addShader(Batched3DShader.class);
        addShader(Basic3DShader.class);
        addTexture("Texture1", texture);
        addTexture("Texture2", textureC);
        addMesh("Mesh", mesh);
        addMesh("Quad3D");
        addMesh("sphere");
        addTexture("null");
       // game.assetManager.addMesh("Mesh1", mesh);

        Window w = createWindow(CameraType.Orthographic, "Test", 640, 480, false);


        w2 = createWindow(CameraType.Orthographic, "Test1", 640, 480, true);
        w2.setDecorated(false);

        w3 = createWindow(CameraType.Orthographic, "Test2", 640, 480, true);
        addSystem(new BatchedRenderingSystem());
        addSystem(new RenderingSystem());
        addSystem(new IteratingSystem(RelationType.All, TransformComponent.class) {
                      private float ang = 0;

                      @Override
                      public void update(float deltaTime) {
                          super.update(deltaTime);
                          ang += deltaTime;
                      }

                      @Override
                      public void processEntityUpdate(int entity, float deltaTime) {
                          entityManager.getComponent(entity, TransformComponent.class).rotation.fromAxisAngleDeg(0, 1, 0, ang * 90);
                      }

                      @Override
                      public void processEntityRender(int entity, Window window, float deltaTime) { }
        });

        tc = new TransformComponent();
        tc.scale.set(100, 100, 100);
        tc.position.set(0,0, -100);

        createEntity(tc, new BatchRenderingComponent( "Mesh", "Texture1", material));

        TransformComponent tc1 = tc.Clone();
        tc1.scale.set(50, 50, 50);

        en1 = createEntity(tc1, new BatchRenderingComponent( "Mesh", "Texture1",  new BatchedBasic3DMaterial()));
        createEntity(tc1, new RenderingComponent("sphere", "Texture2", new Basic3DMaterial()));

        w2.update.add("move", x -> MathUtils.LookRelativeTo(w2, w));
        w3.update.add("move", x -> MathUtils.LookRelativeTo(w3, w));

    }



    @Override
    public void PreUpdate(float deltaTime) {

    }

    private final Vector3f move = new Vector3f(0,0,0);
    private final Vector2f moveDiff = new Vector2f(0,0);
    private boolean prevKPressed;

    private float keepTime = 0;
    @Override
    public void Update(float deltaTime) {


        if(!prevKPressed && getKeyPressed(GLFW.GLFW_KEY_K))
            Objects.requireNonNull(Game.assetManager.getMesh("Mesh")).drawWireframe = !Objects.requireNonNull(Game.assetManager.getMesh("Mesh")).drawWireframe;

        prevKPressed = getKeyPressed(GLFW.GLFW_KEY_K);

        if(getKeyPressed(GLFW.GLFW_KEY_G))
                if(scene.getForAny(BatchRenderingComponent.class).size() > 1)
                    scene.removeEntity(scene.getForAny(BatchRenderingComponent.class).get(MathUtils.random.nextInt(scene.getForAny(BatchRenderingComponent.class).size())));

        move.set(0,0,0);
        if(getKeyPressed(GLFW.GLFW_KEY_W))
            move.add(0,1f,0);
        if(getKeyPressed(GLFW.GLFW_KEY_S))
            move.add(0,-1f, 0);
        if(getKeyPressed(GLFW.GLFW_KEY_A))
            move.add(-1f,0, 0);
        if(getKeyPressed(GLFW.GLFW_KEY_D))
            move.add(1f,0, 0);
        if(getKeyPressed(GLFW.GLFW_KEY_E))
            move.add(0,0, 1f);
        if(getKeyPressed(GLFW.GLFW_KEY_Q))
            move.add(0,0, -1f);

        if(move.length() != 0) {
            move.normalize();
            move.mul((deltaTime * 1000));
        }
        Vector2f cursorPos = w2.getCursorPosition();
        Vector2i winPos = w2.getPosition();
        Vector2i winSize = w2.getSize();
        w2.setPosition((int)cursorPos.x  + winPos.x - winSize.x /2, (int)cursorPos.y + winPos.y - winSize.y /2);





        entityManager.getComponent(en1, TransformComponent.class).position.add(move.x, move.y, move.z);
        if(keepTime >= 1) {
            entityManager.getComponent(en1, BatchRenderingComponent.class).material.color.set(MathUtils.random.nextFloat(), MathUtils.random.nextFloat(), MathUtils.random.nextFloat(), 1);
            keepTime = 0;
        }
        Vector2i moveI = new Vector2i((int)move.x, (int)-move.y);
        moveDiff.add(move.x - moveI.x, -move.y - moveI.y);
        if(moveDiff.x >= 1){
            moveI.x += 1;
            moveDiff.x = move.x - moveI.x;
        }
        if(moveDiff.y >= 1){
            moveI.y += 1;
            moveDiff.y = move.y - moveI.y;
        }

        w3.translate((int)move.x, (int)-move.y);


        keepTime += deltaTime;


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
