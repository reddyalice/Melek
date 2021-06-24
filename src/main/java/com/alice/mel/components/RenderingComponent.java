package com.alice.mel.components;

import com.alice.mel.graphics.Material;

/**
 * Component used to carry rendering information/data
 * @author Bahar Demircan
 */
public class RenderingComponent extends Component{

    public Material material;
    public String meshName;
    public String textureName;

    /**
     * Constructor for Rendering Component
     * @param material Material that carries the data that will load to the shader and shader
     * @param meshName Mesh name registered at the Asset Manager
     * @param textureName Texture name registered at the Asset Manager
     */
    public RenderingComponent(Material material, String meshName, String textureName){
        this.material = material;
        this.meshName = meshName;
        this.textureName = textureName;
    }
}
