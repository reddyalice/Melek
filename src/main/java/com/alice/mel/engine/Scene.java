package com.alice.mel.engine;


import com.alice.mel.components.Component;
import com.alice.mel.components.ComponentType;
import com.alice.mel.graphics.*;
import com.alice.mel.systems.ComponentSystem;
import com.alice.mel.systems.Family;
import com.alice.mel.utils.KeyedEvent;
import com.alice.mel.utils.collections.*;
import org.javatuples.Pair;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.HashMap;

/**
 * Scene class where the magic happens
 * @author Bahar Demircan
 */
public final class Scene {


    private final SnapshotArray<Window> windows = new SnapshotArray<>();
    public final Window loaderWindow;
    public Window currentContext;

    public final WindowPool windowPool = new WindowPool(this);
    public final CameraPool cameraPool = new CameraPool();
    public final EntityPool entityPool = new EntityPool();

    public final KeyedEvent<Window> init = new KeyedEvent<>();
    private final KeyedEvent<Window> multiInit = new KeyedEvent<>();
    public final KeyedEvent<Float> preUpdate = new KeyedEvent<>();
    public final KeyedEvent<Float> update = new KeyedEvent<>();
    public final KeyedEvent<Float> postUpdate = new KeyedEvent<>();

    public final KeyedEvent<Pair<Window, Float>> preRender = new KeyedEvent<>();
    public final KeyedEvent<Pair<Window, Float>> render = new KeyedEvent<>();
    public final KeyedEvent<Pair<Window, Float>> postRender = new KeyedEvent<>();

    public final KeyedEvent<Entity> entityAdded = new KeyedEvent<>();
    public final KeyedEvent<Entity> entityModified = new KeyedEvent<>();

    public final KeyedEvent<Entity> entityRemoved = new KeyedEvent<>();


    private boolean initialized = false;

    private final SnapshotArray<Class<? extends Shader>> shaders = new SnapshotArray<>();
    private final SnapshotArray<String> textures = new SnapshotArray<>();
    private final SnapshotArray<String> meshes = new SnapshotArray<>();
    private final HashMap<ComponentType, Array<Entity>> componentEntityMap = new HashMap<>();
    private final ObjectMap<Family, Array<Entity>> families = new ObjectMap<>();
    private final SnapshotArray<Entity> entities = new SnapshotArray<>();
    private final SnapshotArray<ComponentSystem> componentSystems = new SnapshotArray<>();

    public final World world = new World(new Vec2(0, 9.8f));
    private final Game game;

    /**
     * Creates a scene with a loader window
     * @param game Game the scene loaded to
     */
    public Scene(Game game){
        this.game = game;
        if(game.loaderScene == null) {
            GLFWErrorCallback.createPrint(System.err).set();
            boolean isInitialized = GLFW.glfwInit();
            if (!isInitialized) {
                System.err.println("Failed To initialized!");
                System.exit(1);
            }

            PointerBuffer pf = GLFW.glfwGetMonitors();
            for(int i = 0; i < pf.remaining();)
                new Monitor(pf.get());

            GLFW.glfwSetMonitorCallback((mon, event) -> {
                switch (event) {
                    case GLFW.GLFW_CONNECTED -> new Monitor(mon);
                    case GLFW.GLFW_DISCONNECTED -> Monitor.monitors.remove(mon);
                }
            });



            game.loaderScene = this;
        }

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        loaderWindow = createWindow(CameraType.Orthographic, "Loader", 640, 480);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE);
        removeWindow(loaderWindow);

    }

    /**
     * Create or obtain an entity from freed ones
     * @return Obtained and added entity
     */
    public Entity createEntity(){
        Entity en = entityPool.obtain();
        addEntity(en);
        return en;
    }

    /**
     * Add entity to scene
     * @param entity Entity to be add
     */
    public void addEntity(Entity entity){
        entities.add(entity);
        for(Component component : entity.getComponents())
        {
            Array<Entity> ens = componentEntityMap.get(ComponentType.getFor(component.getClass()));
            if(ens == null)
                ens = new Array<>();
            ens.add(entity);
            componentEntityMap.put(ComponentType.getFor(component.getClass()), ens);
        }
        entity.componentAdded.add(component -> {
            Array<Entity> ens = componentEntityMap.get(ComponentType.getFor(component.getClass()));
            if(ens == null)
                ens = new Array<>();
            ens.add(entity);
            componentEntityMap.put(ComponentType.getFor(component.getClass()), ens);
            entityModified.broadcast(entity);
        });
        entity.componentRemoved.add(component -> {
            Array<Entity> ens = componentEntityMap.get(ComponentType.getFor(component.getClass()));
            if(ens != null){
                
                ens.removeValue(entity, false);
                if(ens.isEmpty())
                    componentEntityMap.remove(ComponentType.getFor(component.getClass()));
                else
                    componentEntityMap.put(ComponentType.getFor(component.getClass()), ens);
            }
            entityModified.broadcast(entity);
        });
        updateEntityFamily(entity);
        entityAdded.broadcast(entity);
    }

    /**
     * Remove entity from the Scene and free the entity to scenes pool
     * @param entity Entity to be removed
     */
    public void removeEntity(Entity entity){
        entityRemoved.broadcast(entity);
        for(Component component : entity.getComponents())
        {
            Array<Entity> ens = componentEntityMap.get(ComponentType.getFor(component.getClass()));
            if(ens != null) {
                ens.removeValue(entity, false);
                componentEntityMap.put(ComponentType.getFor(component.getClass()), ens);
            }
        }
        entities.removeValue(entity, false);
        entityPool.free(entity);
    }

    /**
     * Add a ComponentSystem to the Scene
     * @param system ComponentSystem that will be added
     */
    public void addSystem(ComponentSystem system){
        componentSystems.add(system);
        componentSystems.sort();
        update.add(system.getClass().getSimpleName(), system::update);
        render.add(system.getClass().getSimpleName(), x -> system.render(x.getValue0(), x.getValue1()));
        system.addedToSceneInternal(this);
    }

    /**
     * Remove ComponentSystem from the scene
     * @param system System that will be removed
     */
    public void removeSystem(ComponentSystem system){
        componentSystems.removeValue(system, false);
        update.remove(system.getClass().getSimpleName());
        render.remove(system.getClass().getSimpleName());
        system.removedFromSceneInternal(this);
    }

    /**
     * Load the Texture registered in the Asset Manager to the scene
     * @param name Registered name of the texture that will be loaded
     */
    public void loadTexture(String name){
        Texture texture = game.assetManager.getTexture(name);
        assert texture != null;
        if(!textures.contains(name, false)) {
            textures.add(name);
            loaderWindow.makeContextCurrent();
            texture.genTexture(this);
            if (currentContext != null)
                currentContext.makeContextCurrent();
        }else
            System.err.println("Texture already loaded!");
    }

    /**
     * Load the Mesh registered in the Asset Manager to the scene
     * @param name Registered name of the texture that will be loaded
     */
    public void loadMesh(String name){
        Mesh mesh = game.assetManager.getMesh(name);
        assert mesh != null;
        if(!meshes.contains(name, false)) {
            meshes.add(name);
            loaderWindow.makeContextCurrent();
            mesh.genMesh(this, loaderWindow);
            for (Window window : windows) {
                window.makeContextCurrent();
                mesh.genMesh(this, window);
            }
            if (currentContext != null)
                currentContext.makeContextCurrent();

            multiInit.add("mesh" + mesh.getVAOid(this, loaderWindow), x -> mesh.genMesh(this, x));
        }else
            System.err.println("Mesh already loaded!");
    }

    /**
     * Load the shader registered in the Asset Manager to the Scene
     * @param shaderClass Class of the Shader that will be load
     */
    public void loadShader(Class<? extends Shader> shaderClass){
        Shader shader = game.assetManager.getShader(shaderClass);
        assert shader != null;
        if(!shaders.contains(shaderClass, false)) {
            shaders.add(shaderClass);
            loaderWindow.makeContextCurrent();
            shader.compile(this);
            if (currentContext != null)
                currentContext.makeContextCurrent();
        }else
            System.err.println("Shader already loaded!");
    }

    /**
     * Does the Scene has the texture registered in the Asset Manager loaded
     * @param textureName Name the Texture registered as
     * @return true if scene has the texture loaded else false
     */
    public boolean hasTexture(String textureName){
        return textures.contains(textureName, false);
    }

    /**
     * Does the Scene has the texture registered in the Asset Manager loaded
     * @param meshName Name the Mesh registered as
     * @return true if the scene has the mesh loaded else false
     */
    public boolean hasMesh(String meshName){
        return meshes.contains(meshName, false);
    }

    /**
     * Does the Scene has the Shader registered in the Asset Manager loaded
     * @param shaderClass Class of the Shader registered
     * @return true if the scene has the shader loaded else false
     */
    public boolean hasShader(Class<? extends Shader> shaderClass){
        return shaders.contains(shaderClass, false);
    }

    /**
     * Unload the Texture from scene using registered name from Asset Manager
     * @param name Name the texture registered as
     */
    public void unloadTexture(String name){
        Texture texture = game.assetManager.getTexture(name);
        assert texture != null;
        if(textures.contains(name, false)) {
            loaderWindow.makeContextCurrent();
            texture.dispose(this);
            if (currentContext != null)
                currentContext.makeContextCurrent();
            textures.removeValue(name, false);
        }else
            System.err.println("No such Texture already loaded!");
    }

    /**
     * Unload the Mesh from scene using registered name from Asset Manager
     * @param name Name the Mesh registered as
     */
    public void unloadMesh(String name){
        Mesh mesh = game.assetManager.getMesh(name);
        assert mesh != null;
        if(meshes.contains(name, false)) {
            meshes.removeValue(name, false);
            multiInit.remove("mesh" + mesh.getVAOid(this, loaderWindow));
            loaderWindow.makeContextCurrent();
            mesh.disposeVAO(this, loaderWindow);
            mesh.dispose(this);
            for (Window window : windows) {
                window.makeContextCurrent();
                if(window != loaderWindow)
                mesh.disposeVAO(this, window);
            }
            if (currentContext != null)
                currentContext.makeContextCurrent();
        }else
            System.err.println("No such Mesh already loaded!");

    }

    /**
     * Unload the Shader from scene using shader class
     * @param shaderClass Class of the shader
     */
    public void unloadShader(Class<? extends Shader> shaderClass){
        Shader shader = game.assetManager.getShader(shaderClass);
        assert shader != null;
        if(shaders.contains(shaderClass, false)) {
            shaders.removeValue(shaderClass, false);
            loaderWindow.makeContextCurrent();
            shader.dispose(this);
            if (currentContext != null)
                currentContext.makeContextCurrent();
        }else
            System.err.println("No such Shader already loaded!");

    }

    /**
     * Create or obtain a freed window
     * @param cameraType CameraType window camera will have
     * @param title Window Title
     * @param width Window Width
     * @param height Window Height
     * @param transparentFrameBuffer Does Window has a transparent Frame Buffer
     * @return Window that is created and added
     */
    public Window createWindow(CameraType cameraType, String title, int width, int height, boolean transparentFrameBuffer){
        Window w = windowPool.obtain(cameraType, title, width, height, transparentFrameBuffer);
        w.show();
        addWindow(w);
        return w;
    }

    /**
     * Change Window Camera Type
     * @param window Window to change camera of
     * @param cameraType Type of the new Camera
     */
    public void changeWindowCameraType(Window window, CameraType cameraType){
        cameraPool.free(window.camera);
        window.camera = cameraPool.obtain(cameraType, window.getSize().x, window.getSize().y);
    }


    /**
     * Create or obtain a freed window
     * @param cameraType CameraType window camera will have
     * @param title Window Title
     * @param width Window Width
     * @param height Window Height
     * @return Window that is created and added
     */
    public Window createWindow(CameraType cameraType, String title, int width, int height){
        return createWindow(cameraType, title, width, height, false);
    }

    /**
     * Get Entities that has certain Component
     * @param componentType ComponentType that corresponds to componentClass
     * @return Immutable Array of entities that has that component
     */
    public ImmutableArray<Entity> getEntitiesFor(ComponentType componentType){
        if(componentEntityMap.get(componentType) != null)
            return new ImmutableArray<>(componentEntityMap.get(componentType));
        else
            return null;

    }

    /**
     * Get Entities that has certain Component
     * @param componentClass Component Class of the common components
     * @return Immutable Array of entities that has that components
     */
    public ImmutableArray<Entity> getEntitiesFor(Class<? extends Component> componentClass) {
        return getEntitiesFor(ComponentType.getFor(componentClass));
    }


    /**
     * Get Entities that has certain Components
     * @param componentFamily Family of components
     * @return Immutable Array of entities that has that components
     */
    public ImmutableArray<Entity> getEntitiesFor(Family componentFamily){
        return registerEntities(componentFamily);
    }

    private ImmutableArray<Entity> registerEntities(Family componentFamily){
        Array<Entity> entite = families.get(componentFamily);

        if(entite == null){
            families.put(componentFamily, new Array<>());

            for(Entity entity : entities)
                updateEntityFamily(entity);
        }



        return new ImmutableArray<>(entite);
    }

    private void updateEntityFamily(Entity entity){
        for(Family componentFamily : families.keys()){
            final int index = componentFamily.getIndex();
            final Bits entityFamilyBits = entity.getFamilyBits();

            boolean belongsToTheFamily = entityFamilyBits.get(index);
            boolean matches = componentFamily.matches(entity);

            if(belongsToTheFamily != matches){
                final Array<Entity> familyEntities = families.get(componentFamily);
                if(matches)
                    familyEntities.add(entity);
                else
                    familyEntities.removeValue(entity, false);
            }
        }
    }



    /**
     * Update the scene
     * @param delta deltaTime passed between Updates in game
     */
    public void Update(float delta){

        if(!initialized){
            loaderWindow.makeContextCurrent();
            init.broadcast(loaderWindow);
            initialized = true;
            if(currentContext != null)
                currentContext.makeContextCurrent();
        }


        preUpdate.broadcast(delta);
        update.broadcast(delta);



        for(Window window : windows)
        {
            if(!window.initialised) {
                window.init.broadcast(this);
                window.initialised = true;
            }

            window.preRender.broadcast(delta);
            currentContext = window;

            window.render.broadcast(delta);
            window.postRender.broadcast(delta);
        }
        postUpdate.broadcast(delta);

    }

    private void addWindow(Window window){
        window.setScene(this);
        preUpdate.add("window" + window.id, window.preUpdate::broadcast);
        update.add("window" + window.id, window.update::broadcast);
        postUpdate.add("window" + window.id, window.postUpdate::broadcast);

        window.render.add("scene",  x -> {
            if(GLFW.glfwWindowShouldClose(window.id)) {
                removeWindow(window);
                return;
            }

            Pair<Window, Float> renderPass = Pair.with(window,x);
            preRender.broadcast(renderPass);
            render.broadcast(renderPass);
            postRender.broadcast(renderPass);
        });


        window.makeContextCurrent();
        multiInit.broadcast(window);
        if(currentContext != null)
            currentContext.makeContextCurrent();



        windows.add(window);
    }

    /**
     * Remove the window from The Scene and free it for later use
     * @param window Window to be remove
     */
    public void removeWindow(Window window){
        windows.removeValue(window, false);
        preUpdate.remove("window" + window.id);
        update.remove("window" + window.id);
        postUpdate.remove("window" + window.id);
        window.render.remove("scene");
        windowPool.free(window);

    }

    /**
     * Get the window that is in the scene
     * @param index Index of the window
     * @return Window to be retrieved
     */
    public Window getWindow(int index){
        return windows.get(index);
    }

    /**
     * Get the Window Array
     * @return Window Array
     */
    public ImmutableArray<Window> getWindows(){
        return new ImmutableArray<>(windows);
    }

    /**
     * Get the current window count of the scene
     * @return Window count
     */
    public int getWindowCount(){
        return windows.size;
    }

    /**
     * Dispose and Unload the events, assets, arrays and pools
     */
    public void dispose() {
        preUpdate.dispose();
        update.dispose();
        postUpdate.dispose();
        preRender.dispose();
        render.dispose();
        postRender.dispose();

        for(Class<? extends Shader> shader : shaders)
            unloadShader(shader);


        shaders.clear();

        for(String texture : textures)
            unloadTexture(texture);
        textures.clear();

        for(String mesh : meshes)
           unloadMesh(mesh);
        meshes.clear();

        for(Window window : windows)
            removeWindow(window);
        windows.clear();

        windowPool.dispose();
        cameraPool.dispose();
    }


}
