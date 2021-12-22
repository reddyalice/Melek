package com.alice.mel.utils.loaders;

import com.alice.mel.graphics.Mesh;
import com.alice.mel.graphics.VertexBufferObject;
import com.alice.mel.utils.collections.Array;
import org.javatuples.Pair;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.*;
import java.util.HashMap;

/**
 * .obj File Loader class
 * @author Bahar Demircan
 */
public class OBJLoader {

    /**
     * Loads a 3D Mesh from a obj file
     * @param file File
     * @return VertexData that is created
     */
    public static Pair<HashMap<String, VertexBufferObject>, int[]> loadOBJ(File file) {
        FileReader isr = null;
        try {
            isr = new FileReader(file);
        } catch (FileNotFoundException e) {
            System.err.println("File not found in res; don't use any extension");
        }

        assert isr != null;
        BufferedReader reader = new BufferedReader(isr);
        String line;
        Array<MeshVertex> vertices = new Array<>();
        Array<Vector2f> textures = new Array<>();
        Array<Vector3f> normals = new Array<>();
        Array<Integer> indices = new Array<>();
        try {
            while (true) {
                line = reader.readLine();
                if (line.startsWith("v ")) {
                    String[] currentLine = line.split("\\s+");
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
                             Float.parseFloat(currentLine[2]),
                             Float.parseFloat(currentLine[3]));
                    MeshVertex newVertex = new MeshVertex(vertices.size, vertex);
                    vertices.add(newVertex);

                } else if (line.startsWith("vt ")) {
                    String[] currentLine = line.split("\\s+");
                    Vector2f texture = new Vector2f( Float.parseFloat(currentLine[1]),
                             Float.parseFloat(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) {
                    String[] currentLine = line.split("\\s+");
                    Vector3f normal = new Vector3f( Float.parseFloat(currentLine[1]),
                             Float.parseFloat(currentLine[2]),
                             Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                } else if (line.startsWith("f ")) {
                    break;
                }
            }
            while (line != null && line.startsWith("f ")) {
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");
                processVertex(vertex1, vertices, indices);
                processVertex(vertex2, vertices, indices);
                processVertex(vertex3, vertices, indices);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading the file");
        }
        removeUnusedVertices(vertices);
        float[] verticesArray = new float[vertices.size * 3];
        float[] texturesArray = new float[vertices.size * 2];
        float[] normalsArray = new float[vertices.size * 3];
        convertDataToArrays(vertices, textures, normals, verticesArray,
                texturesArray, normalsArray);
        int[] indicesArray = convertIndicesListToArray(indices);


        HashMap<String, VertexBufferObject> verte = new HashMap<>();
        verte.put("positions", new VertexBufferObject(0, 3,verticesArray.length / 3, verticesArray));
        verte.put("textureCoords", new VertexBufferObject(1, 2,texturesArray.length / 2, texturesArray));
        verte.put("normals",new VertexBufferObject(2,3,normalsArray.length / 3,normalsArray));

        return Pair.with(verte, indicesArray);
    }

    private static void processVertex(String[] vertex, Array<MeshVertex> vertices, Array<Integer> indices) {
        int index = Integer.parseInt(vertex[0]) - 1;
        MeshVertex currentVertex = vertices.get(index);
        int textureIndex = Integer.parseInt(vertex[1]) - 1;
        int normalIndex = Integer.parseInt(vertex[2]) - 1;
        if (!currentVertex.isSet()) {
            currentVertex.setTextureIndex(textureIndex);
            currentVertex.setNormalIndex(normalIndex);
            indices.add(index);
        } else {
            dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
                    vertices);
        }
    }

    private static int[] convertIndicesListToArray(Array<Integer> indices) {
        int[] indicesArray = new int[indices.size];
        for (int i = 0; i < indicesArray.length; i++) {
            indicesArray[i] = indices.get(i);
        }
        return indicesArray;
    }

    private static void convertDataToArrays(Array<MeshVertex> vertices, Array<Vector2f> textures,
                                             Array<Vector3f> normals, float[] verticesArray, float[] texturesArray,
                                             float[] normalsArray) {
        float furthestPoint = 0;
        for (int i = 0; i < vertices.size; i++) {
            MeshVertex currentVertex = vertices.get(i);
            if (currentVertex.getLength() > furthestPoint) {
                furthestPoint = currentVertex.getLength();
            }
            Vector3f position = currentVertex.getPosition();
            Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
            Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
            verticesArray[i * 3] = position.x;
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;
            texturesArray[i * 2] = textureCoord.x;
            texturesArray[i * 2 + 1] = 1 - textureCoord.y;
            normalsArray[i * 3] = normalVector.x;
            normalsArray[i * 3 + 1] = normalVector.y;
            normalsArray[i * 3 + 2] = normalVector.z;
        }
    }

    private static void dealWithAlreadyProcessedVertex(MeshVertex previousVertex, int newTextureIndex,
                                                       int newNormalIndex, Array<Integer> indices, Array<MeshVertex> vertices) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.getIndex());
        } else {
            MeshVertex anotherVertex = previousVertex.getDuplicateVertex();
            if (anotherVertex != null) {
                dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex,
                        indices, vertices);
            } else {
                MeshVertex duplicateVertex = new MeshVertex(vertices.size, previousVertex.getPosition());
                duplicateVertex.setTextureIndex(newTextureIndex);
                duplicateVertex.setNormalIndex(newNormalIndex);
                previousVertex.setDuplicateVertex(duplicateVertex);
                vertices.add(duplicateVertex);
                indices.add(duplicateVertex.getIndex());
            }

        }
    }

    private static void removeUnusedVertices(Array<MeshVertex> vertices){
        for(MeshVertex vertex:vertices){
            if(!vertex.isSet()){
                vertex.setTextureIndex(0);
                vertex.setNormalIndex(0);
            }
        }
    }

     private static class MeshVertex {

        private static final int NO_INDEX = -1;

        private final Vector3f position;
        private int textureIndex = NO_INDEX;
        private int normalIndex = NO_INDEX;
        private MeshVertex duplicateVertex = null;
        private final int index;
        private final float length;

        MeshVertex(int index, Vector3f position){
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
}
