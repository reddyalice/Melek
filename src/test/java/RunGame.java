import com.alice.mel.engine.Game;
import com.alice.mel.engine.Scene;
import scenes.ExampleScene;

import java.io.IOException;

public class RunGame {

    public static void main(String[] args) {
        Scene es = new Scene();
        try {
            es.Load("0.scene");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        Game.addActiveScene(es);
        Game.run();
    }

}
