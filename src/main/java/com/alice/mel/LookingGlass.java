package com.alice.mel;



import com.alice.mel.engine.*;
import com.alice.mel.graphics.Texture;
import com.alice.mel.graphics.shaders.Basic2DShader;
import com.alice.mel.scenes.ExampleScene;


public class LookingGlass {

    public static void main(String[] args) {

            Texture texture = new Texture("src/main/resources/textures/cactus.png");
            //Mesh mesh = OBJLoader.loadOBJ("src/main/resources/models/cactus.obj");
            Game game = new Game();
            game.assetManager.addShader(Basic2DShader.class);
            game.assetManager.addTexture("Texture1", texture);
            ExampleScene ex = new ExampleScene(game);
            ex.addToGame();
            game.Update();

    }
}
