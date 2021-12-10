package com.alice.mel.graphics;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public final class VertexData implements Serializable, Cloneable {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VertexData)) return false;
        VertexData that = (VertexData) o;
        return size == that.size && length == that.length && dimension == that.dimension && Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(size, length, dimension);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public VertexData clone() {
         return new VertexData(size, dimension, data);
    }

    public void copy(VertexData vertexData){
        if(vertexData.length != length)
            throw new ClassCastException("Two vertex data are not compatible with each other!");
        System.arraycopy(vertexData.data, 0, data, 0, length);
    }




}
