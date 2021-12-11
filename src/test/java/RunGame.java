import com.alice.mel.engine.Game;
import scenes.ExampleScene;

public class RunGame {




    public static void main(String[] args) {
        Game.addActiveScene( new ExampleScene());
        Game.run();
    }

}
