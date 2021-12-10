package scenes;

import com.alice.mel.components.BatchRenderingComponent;
import com.alice.mel.components.Component;
import com.alice.mel.components.TransformComponent;
import com.alice.mel.engine.*;
import com.alice.mel.graphics.*;
import com.alice.mel.graphics.Window;
import com.alice.mel.graphics.materials.BatchedBasic3DMaterial;
import com.alice.mel.graphics.shaders.Batched3DShader;
import com.alice.mel.graphics.shaders.BatchedSpriteShader;
import com.alice.mel.systems.BatchedRenderingSystem;
import com.alice.mel.utils.maths.MathUtils;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImInt;
import org.joml.*;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class ExampleScene extends SceneAdaptor {


    Window w;
    Window w2;
    Window w3;
    int en1;
    TransformComponent tc;
    boolean but = false;
    @Override
    public void Init(Window loaderWindow, Scene scene) {
        ImGui.init();

        Texture texture = new Texture("src/test/resources/textures/cactus.png");
        Texture textureC = new Texture("src/test/resources/textures/cardedge.png");
        Mesh mesh = new Mesh("src/test/resources/models/cactus.obj");
        BatchMaterial material = new BatchedBasic3DMaterial();
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


        tc = new TransformComponent();
        tc.scale.set(100, 100, 100);
        tc.position.set(0,0, -100);

        int en = createEntity(tc, new BatchRenderingComponent( "Mesh", "Texture1", material));

        TransformComponent tc1 = tc.Clone();
        tc1.scale.set(50, 50, 50);

        en1 = createEntity(tc1, new BatchRenderingComponent( "Mesh", "Texture1",  new BatchedBasic3DMaterial()));


        w2.update.add("move", x -> MathUtils.LookRelativeTo(w2, w));
        w3.update.add("move", x -> MathUtils.LookRelativeTo(w3, w));


        w.render.add("a", x -> {
            ImGui.begin("test");

            if(ImGui.button("test1")){
                but = !but;
            }


            ImGui.listBox("", new ImInt(0), new String[]{
                    "Rotation of the cacti : " + entityManager.getComponent(en1,  TransformComponent.class).rotation.getEulerAnglesXYZ(new Vector3f()),
                    "Position of the cactus 1 : " + tc.position,
                    "Position of the cactus 2 : " + entityManager.getComponent(en1,  TransformComponent.class).position
            });

            ImGui.end();


        });
    }

    @Override
    public void PreUpdate(float deltaTime) {

    }

    private final Vector3f move = new Vector3f(0,0,0);
    private final Vector2f moveDiff = new Vector2f(0,0);
    private boolean prevKPressed;
    private float ang = 0;
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


        tc.rotation.fromAxisAngleDeg(0,1, 0, ang* 90);



        entityManager.getComponent(en1, TransformComponent.class).position.add(move.x, move.y, move.z);
        entityManager.getComponent(en1, TransformComponent.class).rotation.fromAxisAngleDeg(0, 1, 0, ang * 90);
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
        ang += deltaTime;

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
