package com.alice.mel.graphics.shaders;

import com.alice.mel.components.Component;
import com.alice.mel.components.TransformComponent;
import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Game;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.*;
import com.alice.mel.graphics.materials.SpriteMaterial;
import com.alice.mel.utils.maths.MathUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteShader extends Shader {

    private int location_transformationMatrix;
    private int location_viewMatrix;
    private int location_projectionMatrix;
    private int location_color;
    private int location_textureOffset;
    private int location_textureDivision;


    public SpriteShader() {




        super("#shader vertex\n" +
                        "#version 400 core\n" +
                        "in vec2 position;\n" +
                        "in vec2 texCoords;\n" +
                        "out vec2 pass_texCoords;\n" +
                        "uniform mat4 transformationMatrix;\n" +
                        "uniform mat4 viewMatrix;\n" +
                        "uniform mat4 projectionMatrix;\n" +
                        "uniform vec2 textureDivision;\n" +
                        "uniform vec2 textureOffset;\n" +
                        "void main(void){\n" +
                        "    vec4 worldPosition = transformationMatrix * vec4(position, 0.0, 1.0);\n" +
                        "    gl_Position = projectionMatrix * viewMatrix * worldPosition;\n" +
                        "    vec2 finalTexCoords = texCoords;\n" +
                        "    finalTexCoords.x = texCoords.x / textureDivision.x + textureOffset.x;\n" +
                        "    finalTexCoords.y = texCoords.y / textureDivision.y + textureOffset.y;\n" +
                        "    pass_texCoords = finalTexCoords;\n" +
                        "}\n" +
                        "#shader fragment\n" +
                        "#version 400 core\n" +
                        "in vec2 pass_texCoords;\n" +
                        "out vec4 out_Color;\n" +
                        "uniform sampler2D textureSampler;\n" +
                        "uniform vec4 color;\n" +
                        "void main(void){\n" +
                        "    out_Color = texture(textureSampler, pass_texCoords) * color;\n" +
                        "}"
                , true);
    }

    @Override
    protected void bindAttributes(Scene scene) {
        bindAttribute(scene,0, "position");
        bindAttribute(scene,1, "texCoords");
    }

    @Override
    protected void getAllUniformLocations(Scene scene) {
        location_transformationMatrix = getUniformLocation(scene,"transformationMatrix");
        location_viewMatrix = getUniformLocation(scene,"viewMatrix");
        location_projectionMatrix = getUniformLocation(scene,"projectionMatrix");
        location_color = getUniformLocation(scene,"color");
        location_textureOffset = getUniformLocation(scene, "textureOffset");
        location_textureDivision = getUniformLocation(scene, "textureDivision");
    }



    public void loadTransformationMatrix(Matrix4f matrix){
        this.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadCamera(Camera camera){

        this.loadMatrix(location_viewMatrix, camera.viewMatrix);
        this.loadMatrix(location_projectionMatrix, camera.projectionMatrix);

    }

    @Override
    public void loadValues(Material material, Scene scene, Window window) {
            Game.assetManager.getTexture(material.textureName).bind(scene);
           loadOffset(material.textureOffset, material.textureDivision);
           loadCamera(window.camera);
           loadColor(material.color);
    }

    @Override
    public void loadElement(Scene scene, Window window,  Component... components) {
        if(components[0] instanceof TransformComponent) {
            TransformComponent transform = (TransformComponent) components[0];
            loadTransformationMatrix(MathUtils.CreateTransformationMatrix(transform.position, transform.rotation, transform.scale));
        }
    }

    public void loadColor(Vector4f color){
        this.loadVector(location_color, color);
    }

    public void loadOffset(Vector2f offset, Vector2f division) {
        this.loadVector(location_textureOffset, offset);
        this.loadVector(location_textureDivision, division);
    }

}
