import scenes.ExampleSceneJava;
import com.alice.mel.engine.Game;

public class RunGame {

    public static void main(String[] args) {
        Game game = new Game();
        ExampleSceneJava es = new ExampleSceneJava(game);
        es.addToGame();
        game.run();
    }

}
