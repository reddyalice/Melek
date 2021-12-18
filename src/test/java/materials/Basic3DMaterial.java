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

    public Basic3DMaterial() {
        super(Basic3DShader.class);
    }


    @Override
    protected boolean checkDirty() {
        return false;
    }

    @Override
    protected void clean() {

    }

}
