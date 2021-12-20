package com.alice.mel.graphics;

import org.javatuples.Pair;

import java.io.File;
import java.io.Serializable;

public abstract class Asset implements Serializable {
    public Pair<File, Long> fileInfo;
    public abstract void dispose();
}
