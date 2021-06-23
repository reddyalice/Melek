package com.alice.mel.engine;

import com.alice.mel.graphics.CameraType;
import com.alice.mel.graphics.Window;
import com.alice.mel.systems.ComponentSystem;
import org.jbox2d.dynamics.World;

public abstract class SceneAdaptor {

    public final Scene scene;
    public final Game game;
    public final World world;

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
        scene.entityModified.add("fromAdaptor", this::entityModified);
        scene.entityRemoved.add("fromAdaptor", this::entityRemoved);
        world = scene.world;
    }

    public abstract void Init(Window loaderWindow);
    public abstract void PreUpdate(float deltaTime);
    public abstract void Update(float deltaTime);
    public abstract void PostUpdate(float deltaTime);
    public abstract void PreRender(Window currentWindow, float deltaTime);
    public abstract void Render(Window currentWindow, float deltaTime);
    public abstract void PostRender(Window currentWindow, float deltaTime);

    public abstract void entityAdded(Entity entity);
    public abstract void entityModified(Entity entity);
    public abstract void entityRemoved(Entity entity);

    public final Window createWindow(CameraType cameraType, String title, int width, int height, boolean transparentFrameBuffer){
        return scene.createWindow(cameraType, title, width, height, transparentFrameBuffer);
    }

    public final Window createWindow(CameraType cameraType, String title, int width, int height){
        return scene.createWindow(cameraType, title, width, height);
    }

    public final void removeWindow(Window window){
        scene.removeWindow(window);
    }

    public final Entity createEntity(){
        return scene.createEntity();
    }

    public final void removeEntity(Entity entity){
        scene.removeEntity(entity);
    }

    public final void addSystem(ComponentSystem system){
        scene.addSystem(system);
    }

    public final void removeSystem(ComponentSystem system){
        scene.removeSystem(system);
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
