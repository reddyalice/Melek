package com.alice.mel.graphics.materials;

import com.alice.mel.graphics.Material;
import com.alice.mel.graphics.Shader;
import com.alice.mel.graphics.shaders.Basic3DShader;

public class Basic3DMaterial extends Material {

    public Basic3DMaterial() {
        super(Basic3DShader.class);
    }

    @Override
    public void loadValues(Shader shader) {
        Basic3DShader shader3D = (Basic3DShader) shader;

    }
}
