import com.alice.mel.engine.Game;
import scenes.ExampleScene;

public class RunGame {

    public static void main(String[] args) {
        Game game = new Game();
        ExampleScene es = new ExampleScene(game);
        es.addToGame();
        game.run();
    }

}
