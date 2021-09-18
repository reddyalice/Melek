package com.alice.mel.data;

import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.Window;
import com.alice.mel.utils.KeyedEvent;
import org.javatuples.Pair;

import java.io.Serializable;

public class SceneData implements Serializable {
    public final KeyedEvent<Pair<Window, Scene>> init;
    public final KeyedEvent<Float> preUpdate;
    public final KeyedEvent<Float> update;
    public final KeyedEvent<Float> postUpdate;
    public final KeyedEvent<Pair<Window, Float>> preRender;
    public final KeyedEvent<Pair<Window, Float>> render;
    public final KeyedEvent<Pair<Window, Float>> postRender;

    public SceneData(KeyedEvent<Pair<Window, Scene>> init, KeyedEvent<Float> preUpdate, KeyedEvent<Float> update, KeyedEvent<Float> postUpdate, KeyedEvent<Pair<Window, Float>> preRender, KeyedEvent<Pair<Window, Float>> render, KeyedEvent<Pair<Window, Float>> postRender) {
        this.init = init;
        this.preUpdate = preUpdate;
        this.update = update;
        this.postUpdate = postUpdate;
        this.preRender = preRender;
        this.render = render;
        this.postRender = postRender;
    }
}
