package materials;

import com.alice.mel.components.TransformComponent;
import com.alice.mel.graphics.Window;
import shaders.Basic3DShader;
import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.Material;
import com.alice.mel.utils.maths.MathUtils;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Objects;


public class Basic3DMaterial extends Material {

    public final Vector2f textureOffset = new Vector2f(0f,0f);
    public final Vector2f textureScale = new Vector2f(1f,1f);
    public final Vector4f color = new Vector4f(1,1,1,1);

    public Basic3DMaterial(String textureName) {
        super(Basic3DShader.class);
        this.textureName = textureName;
    }

    @Override
    public void loadValues(AssetManager assetManager, Scene scene, Window window) {
        Basic3DShader shader3D = (Basic3DShader) assetManager.getShader(shaderClass);
        assetManager.getTexture(textureName).bind(scene);
        shader3D.loadOffset(textureOffset, textureScale);
        shader3D.loadCamera(window.camera);
        shader3D.loadColor(color);
    }

    @Override
    public void loadElement(AssetManager assetManager, Scene scene,Window window, TransformComponent transform) {
        Basic3DShader shader3D = (Basic3DShader) assetManager.getShader(shaderClass);
        shader3D.loadTransformationMatrix(MathUtils.CreateTransformationMatrix(transform.position, transform.rotation, transform.scale));
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void doClean() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Basic3DMaterial)) return false;
        Basic3DMaterial that = (Basic3DMaterial) o;
        return Objects.equals(textureOffset, that.textureOffset) && Objects.equals(textureScale, that.textureScale) && Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(textureName, textureOffset, textureScale, color);
    }
}
