package com.alice.mel.components;

import com.alice.mel.graphics.Material;

public class RenderingComponent extends Component{
    public Material material;
    public String meshName;
    public String textureName;
    public RenderingComponent(Material material, String meshName, String textureName){
        this.material = material;
        this.meshName = meshName;
        this.textureName = textureName;
    }
}
