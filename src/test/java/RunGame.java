import com.alice.mel.engine.Game;
import scenes.ExampleScene;

public class RunGame {

    public static void main(String[] args) {
        ExampleScene s = new ExampleScene();

        Game.addActiveScene(s.scene);
        Game.run();
    }

}
