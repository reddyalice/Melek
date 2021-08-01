package com.alice.mel.graphics.shaders;

import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.Shader;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL45;

public class BatchedSpriteShader extends Shader {

    private int location_viewMatrix;
    private int location_projectionMatrix;

    public BatchedSpriteShader() {
        super("#shader vertex\n" +
                "#version 400 core\n" +
                "layout (location=0) in vec2 position;\n" +
                "layout (location=1) in vec2 textureCoords;\n" +
                "layout (location=2) in vec4 color;\n" +
                "layout (location=3) in float texID;\n" +
                "\n" +
                "out vec4 pass_color;\n" +
                "out vec2 pass_texCoords;\n" +
                "out float pass_texId;\n" +
                "\n" +
                "uniform mat4 viewMatrix;\n" +
                "uniform mat4 projectionMatrix;\n" +
                "\n" +
                "\n" +
                "void main(){\n" +
                "\n" +
                "    pass_color = color;\n" +
                "    pass_texCoords = textureCoords;\n" +
                "    pass_texId = texID;\n" +
                "\n" +
                "    gl_Position = projectionMatrix * viewMatrix * vec4(position, 0.0, 1.0);\n" +
                "\n" +
                "}\n" +
                "\n" +
                "#shader fragment\n" +
                "#version 400 core\n" +
                "\n" +
                "\n" +
                "in vec4 pass_color;\n" +
                "in vec2 pass_texCoords;\n" +
                "in float pass_texId;\n" +
                "out vec4 out_Color;\n" +
                "\n" +
                "uniform sampler2D textureSampler[gl_MaxTextureImageUnits];\n" +
                "\n" +
                "void main(){\n" +
                "    int id = int(pass_texId);\n" +
                "    out_Color = pass_color * texture(textureSampler[id], pass_texCoords);\n" +
                "}", true);

    }

    @Override
    protected void bindAttributes(Scene scene) {
        bindAttribute(scene, 0, "position");
        bindAttribute(scene, 1, "textureCoords");
        bindAttribute(scene, 2, "color");
        bindAttribute(scene, 3, "texID");
    }

    @Override
    public void compile(Scene scene) {
        super.compile(scene);
    }

    @Override
    protected void getAllUniformLocations(Scene scene) {
        location_viewMatrix = getUniformLocation(scene, "viewMatrix");
        location_projectionMatrix = getUniformLocation(scene, "projectionMatrix");
    }

    public void loadViewMatrix(Matrix4f viewMatrix){
        loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projectionMatrix){
        loadMatrix(location_projectionMatrix, projectionMatrix);
    }

}
