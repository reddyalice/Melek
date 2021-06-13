package com.alice.mel.engine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Game {

    private static final int coreCount = Runtime.getRuntime().availableProcessors();
    public final ExecutorService executor = Executors.newFixedThreadPool(coreCount);
    public final AssetManager assetManager = new AssetManager();








}
