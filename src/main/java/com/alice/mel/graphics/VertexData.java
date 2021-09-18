package com.alice.mel.graphics;

import java.io.Serializable;

public final class VertexData implements Serializable {
    public final int size;
    public final int length;
    public final int dimension;
    public final float[] data;

    public VertexData(int size, int dimension) {
        this.size = size;
        this.length = size * dimension;
        this.dimension = dimension;
        data = new float[length];

    }

    public VertexData(int size, int dimension, float[] data){
        this(size, dimension);
        System.arraycopy(data, 0, this.data, 0, length);

    }

    public void setVertex(int offset, float[] vertex){
        if(vertex.length >= dimension){
            int index = dimension * offset;
            if (index < length - dimension) {
                if (dimension >= 0) System.arraycopy(vertex, 0, data, index, dimension);
            }else
                System.err.println("There is no space left in the Vertex");
        }else
            System.err.println("Vertex Length has to be at least " + dimension);
    }


}
