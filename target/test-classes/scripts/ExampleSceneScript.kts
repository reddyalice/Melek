import materials.Basic3DMaterial
import shaders.Basic3DShader
import com.alice.mel.components.RenderingComponent
import com.alice.mel.engine.Entity
import com.alice.mel.engine.Game
import com.alice.mel.engine.OBJLoader
import com.alice.mel.engine.SceneAdaptor
import com.alice.mel.graphics.CameraType
import com.alice.mel.graphics.Material
import com.alice.mel.graphics.Texture
import com.alice.mel.graphics.Window
import com.alice.mel.systems.RenderingSystem
import com.alice.mel.utils.maths.MathUtils


class ExampleSceneScript(game : Game?) : SceneAdaptor(game){

    lateinit var w: Window;
    lateinit var w2: Window;

    override fun Init(loaderWindow: Window?) {
        val texture = Texture("src/test/resources/textures/cactus.png")
        val textureC = Texture("src/test/resources/textures/cardedge.png")
        val mesh = OBJLoader.loadOBJ("src/test/resources/models/cactus.obj")
        val material: Material = Basic3DMaterial("Texture1")
        game.assetManager.addShader(Basic3DShader::class.java)
        game.assetManager.addTexture("Texture1", texture)
        game.assetManager.addTexture("Texture2", textureC)
        game.assetManager.addMesh("Mesh1", mesh)

        w = createWindow(CameraType.Orthographic, "Test", 640, 480, false)
        w2 = createWindow(CameraType.Orthographic, "Test1", 640, 480, true)
        w2.setDecorated(false)

        val w3 = createWindow(CameraType.Orthographic, "Test2", 640, 480, true)


        addSystem(RenderingSystem(game.assetManager))
        val en = createEntity()
        en.scale[100f, 100f] = 100f
        en.position[0f, 0f] = -100f
        en.addComponent(RenderingComponent("Mesh1", material))
        val en1 = createEntity()
        en1.scale[50f, 50f] = 50f
        en1.position[500f, 0f] = -99f
        en1.addComponent(RenderingComponent("Mesh1", material))


        w2.update.add("move") { x: Float? -> MathUtils.LookRelativeTo(w2, w) }
        w3.update.add("move") { x: Float? -> MathUtils.LookRelativeTo(w3, w) }
    }

    override fun PreUpdate(deltaTime: Float) {
    }

    override fun Update(deltaTime: Float) {
    }

    override fun PostUpdate(deltaTime: Float) {
    }

    override fun PreRender(currentWindow: Window?, deltaTime: Float) {
    }

    override fun Render(currentWindow: Window?, deltaTime: Float) {
    }

    override fun PostRender(currentWindow: Window?, deltaTime: Float) {
    }

    override fun entityAdded(entity: Entity?) {
    }

    override fun entityModified(entity: Entity?) {
    }

    override fun entityRemoved(entity: Entity?) {
    }

}