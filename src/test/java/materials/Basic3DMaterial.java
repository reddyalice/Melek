package materials;

import com.alice.mel.engine.Game;
import com.alice.mel.graphics.MaterialData;
import shaders.Basic3DShader;
import com.alice.mel.graphics.Material;


public class Basic3DMaterial extends Material {

    public Basic3DMaterial() {
        super(Basic3DShader.class, Game.assetManager.getMaterialBase("empty"));
    }



}
