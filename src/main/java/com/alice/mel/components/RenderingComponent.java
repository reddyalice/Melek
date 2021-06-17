package com.alice.mel.components;

import com.alice.mel.graphics.Shader;

public class RenderingComponent extends Component{
    public Class<? extends Shader> shaderName;
    public String meshName;
    public String textureName;
}
