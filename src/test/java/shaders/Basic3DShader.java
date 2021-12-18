package shaders;

import com.alice.mel.components.Component;
import com.alice.mel.components.TransformComponent;
import com.alice.mel.engine.AssetManager;
import com.alice.mel.engine.Game;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.*;
import com.alice.mel.utils.maths.MathUtils;
import materials.Basic3DMaterial;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Basic3DShader extends Shader {


    private int location_transformationMatrix;
    private int location_viewMatrix;
    private int location_projectionMatrix;
    private int location_color;
    private int location_textureOffset;
    private int location_textureScale;

    public Basic3DShader() {
        super("src/test/resources/shaders/Basic3DShader.glsl", false);
    }

    @Override
    protected void bindAttributes(Scene scene) {
        bindAttribute(scene,0, "position");
        bindAttribute(scene,1, "texCoords");
        bindAttribute(scene,2, "normal");
    }

    @Override
    protected void getAllUniformLocations(Scene scene) {
        location_transformationMatrix = getUniformLocation(scene,"transformationMatrix");
        location_viewMatrix = getUniformLocation(scene,"viewMatrix");
        location_projectionMatrix = getUniformLocation(scene,"projectionMatrix");
        location_color = getUniformLocation(scene,"color");
        location_textureOffset = getUniformLocation(scene, "textureOffset");
        location_textureScale = getUniformLocation(scene, "textureScale");
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
    public void loadElement(Scene scene,Window window, Component... components) {
        if(components[0] instanceof TransformComponent) {
            TransformComponent transform = (TransformComponent) components[0];
            loadTransformationMatrix(MathUtils.CreateTransformationMatrix(transform.position, transform.rotation, transform.scale));
        }
    }

    public void loadColor(Vector4f color){
        this.loadVector(location_color, color);
    }

    public void loadOffset(Vector2f offset, Vector2f scale){
        this.loadVector(location_textureOffset, offset);
        this.loadVector(location_textureScale, scale);
    }

}

