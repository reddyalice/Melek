package com.alice.mel.multithreading;
import java.util.function.Consumer;

public class SystemThread extends Thread {
    private Consumer<Float> updateFunction;
    private float deltaTime = 1f/60f;

    public SystemThread(Consumer<Float> updateFunction, float deltaTime){
        this.deltaTime = deltaTime;
        this.updateFunction = updateFunction;
    }
    @Override
    public void run() {
        updateFunction.accept(deltaTime);
    }

}
