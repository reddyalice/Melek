package com.alice.mel.graphics;

import java.io.Serializable;
import java.util.HashMap;

public abstract class MaterialData implements Serializable {
    public final HashMap<String, VertexData> properties = new HashMap<>();
    protected abstract boolean checkDirty();
    protected abstract void clean();
}
