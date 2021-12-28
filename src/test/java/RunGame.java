import com.alice.mel.engine.Game;
import scenes.ExampleScene;

public class RunGame {




    public static <String> void main(String[] args) {
        Game.addScene( new ExampleScene());
        Game.run();


    }

}
