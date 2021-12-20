package com.alice.mel.graphics;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.HashMap;

public final class MaterialData extends Asset {
    public final HashMap<String, VertexData> properties = new HashMap<>();
    public final HashMap<String, VertexData> oldProperties = new HashMap<>();

    public boolean checkDirty(){
        boolean dirty = false;
        for(String pN : properties.keySet()){
            dirty |= properties.get(pN).equals(oldProperties.get(pN));
        }
        return dirty;
    }

    public void clean(){
        for(String pN : properties.keySet()){
            oldProperties.get(pN).copy(properties.get(pN));
        }
    }

    @Override
    protected MaterialData clone() {
        MaterialData data = new MaterialData();
        for(String pN : properties.keySet()){
            data.properties.put(pN, properties.get(pN).clone());
            data.oldProperties.put(pN, oldProperties.get(pN).clone());
        }
        return data;
    }

    @Override
    public void dispose() {
       properties.clear();
       oldProperties.clear();
    }
}
