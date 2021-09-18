package com.alice.mel.components;

import com.alice.mel.graphics.Material;
import com.alice.mel.utils.collections.Array;

/**
 * Component used to carry rendering information/data
 * @author Bahar Demircan
 */
public class RenderingComponent extends Component{


    public String meshName;
    public Material material;
    /**
     * Constructor for Rendering Component
     * @param meshName Mesh name registered at the Asset Manager
     * @param material Material that carries the data that will load to the shader and shader
     */
    public RenderingComponent(String meshName, String textureName, Material material){
        this.material = material;
        this.material.textureName = textureName;
        this.meshName = meshName;
    }
}
