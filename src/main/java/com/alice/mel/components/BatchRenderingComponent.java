package com.alice.mel.components;

import com.alice.mel.graphics.BatchMaterial;
import com.alice.mel.graphics.Material;

public class BatchRenderingComponent extends Component{
    public String meshName;
    public BatchMaterial material;
    /**
     * Constructor for Rendering Component
     * @param meshName Mesh name registered at the Asset Manager
     * @param material Material that carries the data that will load to the shader and shader
     */
    public BatchRenderingComponent(String meshName, BatchMaterial material){
        this.material = material;
        this.meshName = meshName;
    }
}
