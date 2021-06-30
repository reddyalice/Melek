package com.alice.mel.engine;

import com.alice.mel.graphics.CameraType;
import com.alice.mel.graphics.Window;
import com.alice.mel.systems.ComponentSystem;
import org.jbox2d.dynamics.World;

/**
 * Adaptor Class for the scene to work with it easier
 * @author Bahar Demircan
 */
public abstract class SceneAdaptor {

    public final Scene scene;
    public final Game game;
    public final World world;

    /**
     * @param game Game the Scene will load to
     */
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

    /**
     * Initialization stage of the scene
     * @param loaderWindow Loader Window for loading assets
     */
    public abstract void Init(Window loaderWindow);

    /**
     * Stage before the main Update
     * @param deltaTime Delta Time
     */
    public abstract void PreUpdate(float deltaTime);

    /**
     * Scene Update
     * @param deltaTime Delta Time
     */
    public abstract void Update(float deltaTime);

    /**
     * Stage after main Update
     * @param deltaTime Delta Time
     */
    public abstract void PostUpdate(float deltaTime);

    /**
     * Stage before the main render
     * @param currentWindow Window that is being rendered
     * @param deltaTime Delta Time
     */
    public abstract void PreRender(Window currentWindow, float deltaTime);

    /**
     * Render the Scene
     * @param currentWindow Window that is being rendered
     * @param deltaTime Delta Time
     */
    public abstract void Render(Window currentWindow, float deltaTime);

    /**
     * Stage after the Scene
     * @param currentWindow Window that is being rendered
     * @param deltaTime Delta Time
     */
    public abstract void PostRender(Window currentWindow, float deltaTime);

    /**
     * Called when an entity is added to the scene
     * @param entity Entity that has been added
     */
    public abstract void entityAdded(Entity entity);

    /**
     * Called when an entity in the scene modified
     * @param entity Entity that has been modified
     */
    public abstract void entityModified(Entity entity);

    /**
     * Called when an entity removed
     * @param entity Entity that has been removed
     */
    public abstract void entityRemoved(Entity entity);

    /**
     * Create or obtain a freed window
     * @param cameraType CameraType window camera will have
     * @param title Window Title
     * @param width Window Width
     * @param height Window Height
     * @param transparentFrameBuffer Does Window has a transparent Frame Buffer
     * @return Window that is created and added
     * */
    public final Window createWindow(CameraType cameraType, String title, int width, int height, boolean transparentFrameBuffer){
        return scene.createWindow(cameraType, title, width, height, transparentFrameBuffer);
    }

    /**
     * Create or obtain a freed window
     * @param cameraType CameraType window camera will have
     * @param title Window Title
     * @param width Window Width
     * @param height Window Height
     * @return Window that is created and added
     */
    public final Window createWindow(CameraType cameraType, String title, int width, int height){
        return scene.createWindow(cameraType, title, width, height);
    }

    /**
     * Remove Window from scene and free the window
     * @param window Window to be removed and freed
     */
    public final void removeWindow(Window window){
        scene.removeWindow(window);
    }

    /**
     * Create or obtain an entity from freed entities and add to the scene
     * @return Entity to be obtained
     */
    public final Entity createEntity(){
        return scene.createEntity();
    }

    /**
     * Remove and free an entity from scene
     * @param entity Entity to be removed and freed
     */
    public final void removeEntity(Entity entity){
        scene.removeEntity(entity);
    }

    /**
     * Add a Component System to the scene
     * @param system System to be added
     */
    public final void addSystem(ComponentSystem system){
        scene.addSystem(system);
    }

    /**
     * Remove a Component System from the scene
     * @param system System to be removed
     */
    public final void removeSystem(ComponentSystem system){
        scene.removeSystem(system);
    }

    /**
     * Add the scene to the active scenes in the game
     */
    public final void addToGame(){
        game.addActiveScene(scene);
    }

    /**
     * Remove the scene from the active scenes in the game
     * @param destroyScene Destroy the scene after removing
     */
    public final void removeFromGame(boolean destroyScene){
        game.removeActiveScene(scene, destroyScene);
    }

    /**
     * Remove the scene from the active scenes in the game (Destroys the scene after removing)
     */
    public final void removeFromGame(){
        removeFromGame(true);
    }

}
