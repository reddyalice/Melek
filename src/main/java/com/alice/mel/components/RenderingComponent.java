package com.alice.mel.components;

import com.alice.mel.graphics.Material;
import com.alice.mel.graphics.Shader;

public class RenderingComponent extends Component{
    public final Material material;
    public final String meshName;
    public final String textureName;
    public RenderingComponent(Material material, String meshName, String textureName){
        this.material = material;
        this.meshName = meshName;
        this.textureName = textureName;
    }
}
