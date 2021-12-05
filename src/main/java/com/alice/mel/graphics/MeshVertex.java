package com.alice.mel.graphics;

import org.joml.Vector3f;

/**
 * Vertex Data Holder
 * @author Bahar Demircan
 */
public class MeshVertex {

    private static final int NO_INDEX = -1;

    private final Vector3f position;
    private int textureIndex = NO_INDEX;
    private int normalIndex = NO_INDEX;
    private MeshVertex duplicateVertex = null;
    private final int index;
    private final float length;

    public MeshVertex(int index, Vector3f position){
        this.index = index;
        this.position = position;
        this.length = position.length();
    }

    public int getIndex(){
        return index;
    }

    public float getLength(){
        return length;
    }

    public boolean isSet(){
        return textureIndex!=NO_INDEX && normalIndex!=NO_INDEX;
    }

    public boolean hasSameTextureAndNormal(int textureIndexOther,int normalIndexOther){
        return textureIndexOther==textureIndex && normalIndexOther==normalIndex;
    }

    public void setTextureIndex(int textureIndex){
        this.textureIndex = textureIndex;
    }

    public void setNormalIndex(int normalIndex){
        this.normalIndex = normalIndex;
    }

    public Vector3f getPosition() {
        return position;
    }

    public int getTextureIndex() {
        return textureIndex;
    }

    public int getNormalIndex() {
        return normalIndex;
    }

    public MeshVertex getDuplicateVertex() {
        return duplicateVertex;
    }

    public void setDuplicateVertex(MeshVertex duplicateVertex) {
        this.duplicateVertex = duplicateVertex;
    }

}