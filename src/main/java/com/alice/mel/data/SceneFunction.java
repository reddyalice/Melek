package com.alice.mel.data;

import com.alice.mel.components.Component;
import com.alice.mel.engine.Entity;
import com.alice.mel.engine.Scene;
import com.alice.mel.graphics.Window;
import com.alice.mel.utils.KeyedEvent;
import org.javatuples.Pair;

import java.io.Serializable;

public class SceneFunction implements Serializable {
    public final KeyedEvent<Pair<Window, Scene>> init;
    public final KeyedEvent<Float> preUpdate;
    public final KeyedEvent<Float> update;
    public final KeyedEvent<Float> postUpdate;
    public final KeyedEvent<Pair<Window, Float>> preRender;
    public final KeyedEvent<Pair<Window, Float>> render;
    public final KeyedEvent<Pair<Window, Float>> postRender;
    public final KeyedEvent<Entity> entityAdded;
    public final KeyedEvent<Pair<Entity, Component>> entityModified;
    public final KeyedEvent<Entity> entityRemoved;

    public SceneFunction(KeyedEvent<Pair<Window, Scene>> init, KeyedEvent<Float> preUpdate, KeyedEvent<Float> update, KeyedEvent<Float> postUpdate, KeyedEvent<Pair<Window, Float>> preRender, KeyedEvent<Pair<Window, Float>> render, KeyedEvent<Pair<Window, Float>> postRender, KeyedEvent<Entity> entityAdded, KeyedEvent<Pair<Entity, Component>> entityModified, KeyedEvent<Entity> entityRemoved) {
        this.init = init;
        this.preUpdate = preUpdate;
        this.update = update;
        this.postUpdate = postUpdate;
        this.preRender = preRender;
        this.render = render;
        this.postRender = postRender;
        this.entityAdded = entityAdded;
        this.entityModified = entityModified;
        this.entityRemoved = entityRemoved;
    }
}
