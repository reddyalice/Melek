package com.alice.mel.engine;


import com.alice.mel.components.Component;
import com.alice.mel.graphics.*;
import com.alice.mel.systems.ComponentSystem;
import com.alice.mel.utils.Event;
import com.alice.mel.utils.KeyedEvent;
import com.alice.mel.utils.collections.*;
import com.sun.jdi.VoidType;
import org.javatuples.Pair;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.util.remotery.Remotery;
import org.lwjgl.util.remotery.RemoteryGL;

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

    public final KeyedEvent<Pair<Window, Scene>> init = new KeyedEvent<>();
    private final KeyedEvent<Window> multiInit = new KeyedEvent<>();
    public final KeyedEvent<Float> preUpdate = new KeyedEvent<>();
    public final KeyedEvent<Float> update = new KeyedEvent<>();
    public final KeyedEvent<Float> postUpdate = new KeyedEvent<>();

    public final KeyedEvent<Pair<Window, Float>> preRender = new KeyedEvent<>();
    public final KeyedEvent<Pair<Window, Float>> render = new KeyedEvent<>();
    public final KeyedEvent<Pair<Window, Float>> postRender = new KeyedEvent<>();

    public final Event<?> dispose = new Event<>();

    private boolean initialized = false;

    private final SnapshotArray<Class<? extends Shader>> shaders = new SnapshotArray<>();
    private final SnapshotArray<String> textures = new SnapshotArray<>();
    private final SnapshotArray<String> meshes = new SnapshotArray<>();
    private final SnapshotArray<ComponentSystem> componentSystems = new SnapshotArray<>();

    public final World world = new World(new Vec2(0, 9.8f));
    public final EntityManager entityManager = new EntityManager();



    /**
     * Creates a scene with a loader window
     */
    public Scene(){
        if(Game.loaderScene == null) {
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

            Game.loaderScene = this;

        }


        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        loaderWindow = createWindow(CameraType.Orthographic, "Loader", 640, 480);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE);
        removeWindow(loaderWindow);
        preRender.add("hotReload", x -> {
            if(Game.hotReload)
            Game.assetManager.hotReload(this, x.getValue0());
        });

        render.add("systems", x -> {
            for(ComponentSystem system : componentSystems) {
                if(Game.remoteProfiler) RemoteryGL.rmt_BeginOpenGLSample(system.toString(), BufferUtils.createIntBuffer(1));
                system.render(x.getValue0(), x.getValue1());
                if(Game.remoteProfiler) RemoteryGL.rmt_EndOpenGLSample();
            }
        });

    }

    /**
     * Create or obtain an entity from freed ones
     * @return Obtained and added entity
     */
    public int createEntity(Component... components){
       return entityManager.createEntity(components);
    }

    public void removeEntity(int entity){
        entityManager.removeEntity(entity);
    }

    /**
     * Add a ComponentSystem to the Scene
     * @param system ComponentSystem that will be added
     */
    public void addSystem(ComponentSystem system){
        componentSystems.add(system);
        componentSystems.sort();
        system.addedToSceneInternal(this);
    }

    /**
     * Remove ComponentSystem from the scene
     * @param system System that will be removed
     */
    public void removeSystem(ComponentSystem system){
        componentSystems.removeValue(system, false);
        componentSystems.sort();
        system.removedFromSceneInternal(this);
    }





    /**
     * Load the Texture registered in the Asset Manager to the scene
     * @param name Registered name of the texture that will be loaded
     */
    public void loadTexture(String name){
        Texture texture = Game.assetManager.getTexture(name);
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
        Mesh mesh = Game.assetManager.getMesh(name);
        assert mesh != null;
        if(!meshes.contains(name, false)) {
            meshes.add(name);
            loaderWindow.makeContextCurrent();
            mesh.genMesh(this, loaderWindow);
            for (Window window : windows) {
                if(window != loaderWindow) {
                    window.makeContextCurrent();
                    mesh.genMesh(this, window);
                }
            }
            if (currentContext != null)
                currentContext.makeContextCurrent();

            multiInit.add("mesh" + mesh.getVAOid(this, loaderWindow), x -> {if(mesh.getVAOid(this, x) == 0) mesh.genMesh(this, x);});
        }else
            System.err.println("Mesh already loaded!");
    }

    /**
     * Load the shader registered in the Asset Manager to the Scene
     * @param shaderClass Class of the Shader that will be load
     */
    public void loadShader(Class<? extends Shader> shaderClass){
        Shader shader = Game.assetManager.getShader(shaderClass);
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
        Texture texture = Game.assetManager.getTexture(name);
        assert texture != null;
        if(textures.contains(name, false)) {
            loaderWindow.makeContextCurrent();
            texture.clear(this);
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
        Mesh mesh = Game.assetManager.getMesh(name);
        assert mesh != null;
        if(meshes.contains(name, false)) {
            meshes.removeValue(name, false);
            multiInit.remove("mesh" + mesh.getVAOid(this, loaderWindow));
            loaderWindow.makeContextCurrent();
            mesh.disposeVAO(this, loaderWindow);
            mesh.clear(this);
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
        Shader shader = Game.assetManager.getShader(shaderClass);
        assert shader != null;
        if(shaders.contains(shaderClass, false)) {
            shaders.removeValue(shaderClass, false);
            loaderWindow.makeContextCurrent();
            shader.clear(this);
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


    public final ImmutableArray<Integer> getFor(RelationType relation, Class<? extends Component>... componentClasses){
        return entityManager.getFor(relation, componentClasses);
    }

    @SafeVarargs
    public final ImmutableArray<Integer> getForAll(Class<? extends Component>... componentClasses) {
        return entityManager.getForAll(componentClasses);
    }

    @SafeVarargs
    public final ImmutableArray<Integer> getForAny(Class<? extends Component>... componentClasses) {
        return entityManager.getForAny(componentClasses);
    }

    @SafeVarargs
    public final ImmutableArray<Integer> getForNone(Class<? extends Component>... componentClasses) {
        return entityManager.getForNone(componentClasses);
    }



    /**
     * Update the scene
     * @param delta deltaTime passed between Updates in game
     */
    public void Update(float delta){

        if(!initialized){
            loaderWindow.makeContextCurrent();
            init.broadcast(Pair.with(loaderWindow, this));
            initialized = true;
            if(currentContext != null)
                currentContext.makeContextCurrent();
        }


        preUpdate.broadcast(delta);

        for (int i = 1; i < componentSystems.size; i++) {
            int finalI = i;

            Game.forkJoinPool.submit(() -> {
                if(Game.remoteProfiler) Remotery.rmt_BeginCPUSample(componentSystems.get(finalI).toString(), 0, BufferUtils.createIntBuffer(1));
                componentSystems.get(finalI).update(delta);
                if(Game.remoteProfiler) Remotery.rmt_EndCPUSample();
            });

        }
        if(componentSystems.size > 0) {
            if(Game.remoteProfiler) Remotery.rmt_BeginCPUSample(componentSystems.get(0).toString(), 0, BufferUtils.createIntBuffer(1));
            componentSystems.get(0).update(delta);
            if(Game.remoteProfiler) Remotery.rmt_EndCPUSample();
        }
        //noinspection StatementWithEmptyBody
        while (!Game.forkJoinPool.isQuiescent()) {}
        update.broadcast(delta);

        for(Window window : windows)
        {
            if(!window.initialised) {
                window.init.broadcast(this);
                window.initialised = true;
            }

            currentContext = window;
            window.preRender.broadcast(delta);


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
        dispose.broadcast(null);
        dispose.dispose();
    }



}
