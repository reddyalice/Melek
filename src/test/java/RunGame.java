import com.alice.mel.components.BatchRenderingComponent;
import com.alice.mel.components.Component;
import com.alice.mel.engine.Entity;
import com.alice.mel.engine.Game;
import com.alice.mel.engine.Scene;
import com.alice.mel.engine.SceneAdaptor;
import com.alice.mel.graphics.BatchMaterial;
import com.alice.mel.graphics.CameraType;
import com.alice.mel.graphics.Texture;
import com.alice.mel.graphics.Window;
import com.alice.mel.graphics.materials.GUIMaterial;
import com.alice.mel.graphics.shaders.BatchedSpriteShader;
import com.alice.mel.systems.BatchedRenderingSystem;
import com.alice.mel.utils.maths.MathUtils;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.CallbackI;
import scenes.ExampleScene;

import java.io.IOException;

public class RunGame {

    public static void main(String[] args) {
        ExampleScene s = new ExampleScene();

        Game.addActiveScene(s.scene);
        Game.run();
    }

}
