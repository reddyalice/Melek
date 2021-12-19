package materials;

import com.alice.mel.graphics.MaterialData;
import shaders.Basic3DShader;
import com.alice.mel.graphics.Material;


public class Basic3DMaterial extends Material {

    public Basic3DMaterial() {
        super(Basic3DShader.class, new MaterialData() {
            @Override
            protected boolean checkDirty() {
                return false;
            }

            @Override
            protected void clean() {

            }
        });
    }



}
