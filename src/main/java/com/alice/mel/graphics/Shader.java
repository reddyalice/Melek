package com.alice.mel.graphics;

import com.alice.mel.engine.Scene;
import com.alice.mel.utils.collections.Array;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL32C;

import java.io.*;
import java.nio.FloatBuffer;
import java.util.HashMap;

/**
 * Class for loading the Shader
 * @author Bahar Demircan
 */
public abstract class Shader implements Serializable {

    public enum ShaderType{
        NONE,
        VERTEX ,
        FRAGMENT,
        GEOMETRY
    }

    public HashMap<Scene, Integer> ids = new HashMap<>();
    public final HashMap<Integer, String> sources = new HashMap<>();
    private final float[] matrixBuffer = new float[16];

    /**
     * @param shaderOrShaderFilePath Shader Source or File Path to the shader file
     * @param isShaderSource Is the String give shader source
     */
    public Shader(String shaderOrShaderFilePath, boolean isShaderSource){
        Array<String> lines = new Array<>();
        ShaderType type = ShaderType.NONE;
        if(isShaderSource){
            lines.addAll(shaderOrShaderFilePath.split("\n"));
        }
        else{
            try {
                BufferedReader reader = new BufferedReader(new FileReader(shaderOrShaderFilePath));

                String line;
                while((line = reader.readLine()) != null){
                    lines.add(line);
                }

                reader.close();
            } catch (FileNotFoundException e) {

                System.err.println("Couldn't find the shader file!");
                e.printStackTrace();
                System.exit(-1);
            } catch (IOException e) {

                System.err.println("Couldn't read the shader file!");
                e.printStackTrace();
                System.exit(-1);
            }
        }

        for(int i = 0; i < lines.size; i++){
            if(lines.get(i).contains("#shader")){
                if(lines.get(i).contains("vertex")){
                    type = ShaderType.VERTEX;
                    sources.put(GL32C.GL_VERTEX_SHADER, "");
                }else if(lines.get(i).contains("fragment")){
                    type = ShaderType.FRAGMENT;
                    sources.put(GL32C.GL_FRAGMENT_SHADER, "");
                }else if(lines.get(i).contains("geometry")){
                    type = ShaderType.GEOMETRY;
                    sources.put(GL32C.GL_GEOMETRY_SHADER, "");
                }
            }else{
                String sb;
                switch(type){
                    case NONE:
                        break;
                    case VERTEX:
                        sb = "";
                        sb += sources.get(GL32C.GL_VERTEX_SHADER);
                        sb += lines.get(i) + "\n";
                        sources.replace(GL32C.GL_VERTEX_SHADER, sb + "");
                        break;
                    case FRAGMENT:
                        sb = "";
                        sb += sources.get(GL32C.GL_FRAGMENT_SHADER);
                        sb += lines.get(i) + "\n";
                        sources.replace(GL32C.GL_FRAGMENT_SHADER, sb + "");
                        break;
                    case GEOMETRY:

                        sb = "";
                        sb += sources.get(GL32C.GL_GEOMETRY_SHADER);
                        sb += lines.get(i) + "\n";
                        sources.replace(GL32C.GL_GEOMETRY_SHADER, sb + "");
                        break;

                }
            }
        }

    }


    protected abstract void bindAttributes(Scene scene);
    protected abstract void getAllUniformLocations(Scene scene);


    /**
     * Bind Attribute to the location
     * @param scene Scene that loads the shader
     * @param attribute Attribute Location
     * @param variableName Attribute Variable name
     */
    public void bindAttribute(Scene scene, int attribute, String variableName){
        GL32C.glBindAttribLocation(ids.get(scene), attribute, variableName);
    }

    /**
     * Get the Location of a Uniform Variable
     * @param scene Scene the shader loaded to
     * @param name Name of the Uniform variable
     * @return Location of the Uniform variable
     */
    public int getUniformLocation(Scene scene, String name){
        return GL32C.glGetUniformLocation(ids.get(scene), name);
    }

    /**
     * Compile the shader
     * @param scene Scene its loaded to
     */
    public void compile(Scene scene){
        int id = GL32C.glCreateProgram();
        ids.put(scene, id);
        int[] shaders = new int[sources.size()];

        int i = 0;
        for(int key : sources.keySet()){
            shaders[i] = loadShader(sources.get(key), key);
            GL32C.glAttachShader(id, shaders[i]);
            i++;
        }

        bindAttributes(scene);
        GL32C.glLinkProgram(id);

        for(i = 0; i < shaders.length; i++)
            GL32C.glDeleteShader(shaders[i]);

        getAllUniformLocations(scene);



    }

    /**
     * Start the Shader (Use program)
     * @param scene Scene It's loaded to
     */
    public void start(Scene scene){
        if(ids.get(scene) == 0)
            compile(scene);
        GL32C.glUseProgram(ids.get(scene));
    }

    /**
     * Stop the Shader (Use program 0)
     */
    public void stop(){
        GL32C.glUseProgram(0);
    }

    private static int loadShader(String shader, int type)
    {

        int shaderID = GL32C.glCreateShader(type);
        GL32C.glShaderSource(shaderID, shader);
        GL32C.glCompileShader(shaderID);
        if(GL32C.glGetShaderi(shaderID, GL32C.GL_COMPILE_STATUS) == GL32C.GL_FALSE){
            System.out.println(GL32C.glGetShaderInfoLog(shaderID, 500));
            String error = switch (type) {
                case GL32C.GL_VERTEX_SHADER -> "vertex";
                case GL32C.GL_FRAGMENT_SHADER -> "fragment";
                case GL32C.GL_GEOMETRY_SHADER -> "geometry";
                default -> "";
            };
            System.err.println("Couldn't compile " + error + " shader!");
            System.exit(-1);
        }

        return shaderID;

    }


    protected void loadFloat(int location, float value){
        GL32C.glUniform1f(location, value);
    }

    protected void loadInt(int location, int value){
        GL32.glUniform1i(location, value);
    }

    protected void loadMatrix(int location, Matrix4f value){
        value.get(matrixBuffer);
        GL32C.glUniformMatrix4fv(location, false, matrixBuffer);
    }

    protected void loadBoolean(int location, boolean value){
        float toLoad = value ? 1 : 0;
        GL32C.glUniform1f(location, toLoad);
    }

    protected void loadVector(int location, Vector2f value){
        GL32C.glUniform2f(location, value.x, value.y);
    }

    protected void loadVector(int location, Vector3f value){
        GL32C.glUniform3f(location, value.x, value.y, value.z);
    }

    protected void loadVector(int location, Vector4f value){
        GL32.glUniform4f(location, value.x, value.y, value.z, value.w);
    }

    protected void loadFloatArray(int location, float[] value){

        GL32C.glUniform1fv(location, value);
    }

    protected void loadIntArray(int location, int[] value){
        GL32C.glUniform1iv(location, value);
    }


    protected void loadBooleanArray(int location, boolean[] value){

        float[] tmp = new float[value.length];
        for(int i = 0; i < value.length; i++){
            tmp[i] = value[i] ? 1 : 0;
        }

        GL32C.glUniform1fv(location, tmp);
    }


    protected void loadVectorArray(int location, Vector3f[] value){
        float[] tmp = new float[value.length * 3];
        for(int i = 0; i < value.length; i++){
            tmp[3 * i] = value[i].x;
            tmp[3 * i + 1] = value[i].y;
            tmp[3 * i + 2] = value[i].z;
        }
        GL32C.glUniform3fv(location, tmp);
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass();
    }


    /**
     * Stop and Dispose the shader
     * @param scene Scene it's loaded to
     */
    public void dispose(Scene scene)
    {
        stop();
        GL32C.glDeleteShader(ids.get(scene));
        //sources.clear();
    }



}
