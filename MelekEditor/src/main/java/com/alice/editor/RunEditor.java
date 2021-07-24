package com.alice.editor;

import com.alice.mel.engine.Game;

public class RunEditor {

    public static final Game game = new Game();

    public static void main(String[] args) {
        EditorScene scene = new EditorScene();
        game.run();
    }
}
