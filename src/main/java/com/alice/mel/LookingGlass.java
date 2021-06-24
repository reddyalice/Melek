package com.alice.mel;



import com.alice.mel.engine.*;
import com.alice.mel.graphics.Texture;
import com.alice.mel.graphics.shaders.Basic3DShader;
import com.alice.mel.scenes.ExampleScene;


public class LookingGlass {

    public static void main(String[] args) {



            Game game = new Game();

            ExampleScene ex = new ExampleScene(game);
            ex.addToGame();
            game.run();

    }
}
