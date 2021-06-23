package com.alice.mel.engine;

import com.alice.mel.graphics.Window;

public abstract class SceneAdaptor {

    public final Scene scene;
    public final Game game;

    public SceneAdaptor(Game game){
        scene = new Scene(game);
        this.game = game;
        scene.init.add("fromAdaptor", this::Init);
        scene.preUpdate.add("fromAdaptor", this::PreUpdate);
        scene.update.add("fromAdaptor", this::Update);
        scene.postUpdate.add("fromAdaptor", this::PostUpdate);
        scene.preRender.add("fromAdaptor", x -> PreRender(x.getValue0(), x.getValue1()));
        scene.render.add("fromAdaptor", x -> Render(x.getValue0(), x.getValue1()));
        scene.postRender.add("fromAdaptor", x -> PostRender(x.getValue0(), x.getValue1()));
        scene.entityAdded.add("fromAdaptor", this::entityAdded);
        scene.entityRemoved.add("fromAdaptor", this::entityRemoved);
    }

    public abstract void Init(Window loaderWindow);
    public abstract void PreUpdate(float deltaTime);
    public abstract void Update(float deltaTime);
    public abstract void PostUpdate(float deltaTime);
    public abstract void PreRender(Window currentWindow, float deltaTime);
    public abstract void Render(Window currentWindow, float deltaTime);
    public abstract void PostRender(Window currentWindow, float deltaTime);

    public void entityAdded(Entity entity){

    }

    public void entityRemoved(Entity entity){

    }


    public final void addToGame(){
        game.addActiveScene(scene);
    }

    public final void removeFromGame(boolean destroyScene){
        game.removeActiveScene(scene, destroyScene);
    }

    public final void removeFromGame(){
        removeFromGame(true);
    }

}
