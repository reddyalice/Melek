package com.alice.mel.components;

import com.alice.mel.graphics.Material;
import com.alice.mel.utils.collections.Array;

/**
 * Component used to carry rendering information/data
 * @author Bahar Demircan
 */
public class RenderingComponent extends Component{


    public String meshName;
    public final Array<Material> materials = new Array<>();
    /**
     * Constructor for Rendering Component
     * @param meshName Mesh name registered at the Asset Manager
     * @param materials Materials that carries the data that will load to the shader and shader
     */
    public RenderingComponent(String meshName, Material... materials){
        this.materials.addAll(materials);
        this.meshName = meshName;
    }
}
