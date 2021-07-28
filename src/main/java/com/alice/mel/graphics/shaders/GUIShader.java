package com.alice.mel.graphics.shaders;

import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.Shader;
import com.alice.mel.gui.UIElement;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class GUIShader extends Shader {


    private int location_elementPosition;
    private int location_elementSize;
    private int location_screenSize;
    private int location_rotation;
    private int location_color;
    private int location_textureOffset;
    private int location_textureScale;


    public GUIShader() {
        super("#shader vertex\n" +
                "#version 400 core\n" +
                "in vec2 position;\n" +
                "in vec2 texCoords;\n" +
                "out vec2 pass_texCoords;\n" +
                "uniform vec2 elementPosition;\n" +
                "uniform vec2 elementSize;\n" +
                "uniform vec2 screenSize;\n" +
                "uniform float rotation;\n" +
                "uniform vec2 textureScale;\n" +
                "uniform vec2 textureOffset;\n" +
                "void main(void){\n" +
                "    float angle = radians(rotation);\n" +
                "    float cs = cos(angle);\n" +
                "    float sn = sin(angle);\n" +
                "    vec2 worldPosition = position;\n" +
                "    worldPosition.x = worldPosition.x / screenSize.x * elementSize.x;\n" +
                "    worldPosition.y = worldPosition.y / screenSize.y * elementSize.y;\n" +
                "    worldPosition.x = worldPosition.x * cs - worldPosition.y * sn;\n" +
                "    worldPosition.y = worldPosition.x * sn + worldPosition.y * cs;\n" +
                "    worldPosition += elementPosition / screenSize * 2;\n" +
                "    gl_Position =  vec4(worldPosition, 0.0, 1.0);\n" +
                "    vec2 finalTexCoords = texCoords;\n" +
                "    finalTexCoords.x = texCoords.x / textureScale.x + textureOffset.x;\n" +
                "    finalTexCoords.y = texCoords.y / textureScale.y + textureOffset.y;\n" +
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
                "}", true);
    }

    @Override
    protected void bindAttributes(Scene scene) {
        bindAttribute(scene,0, "position");
        bindAttribute(scene,1, "texCoords");
    }

    @Override
    protected void getAllUniformLocations(Scene scene) {
        location_elementPosition = getUniformLocation(scene,"elementPosition");
        location_elementSize = getUniformLocation(scene,"elementSize");
        location_screenSize = getUniformLocation(scene,"screenSize");
        location_rotation = getUniformLocation(scene,"rotation");
        location_color = getUniformLocation(scene,"color");
        location_textureOffset = getUniformLocation(scene, "textureOffset");
        location_textureScale = getUniformLocation(scene, "textureScale");
    }

    public void loadScreenSize(Vector2f size){
        loadVector(location_screenSize, size);
    }

    public void loadElementValues(UIElement element){
        loadVector(location_elementPosition, new Vector2f(element.position.x, element.position.y));
        loadVector(location_elementSize, new Vector2f(element.scale.x, element.scale.y));
        Vector3f euler = new Vector3f();
                element.rotation.getEulerAnglesXYZ(euler);
        loadFloat(location_rotation, euler.x);
        //loadVector(location_color, element.guiMaterial.color);
    }

    public void loadOffset(Vector2f offset, Vector2f scale) {
        this.loadVector(location_textureOffset, offset);
        this.loadVector(location_textureScale, scale);
    }


}
