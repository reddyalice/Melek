package com.alice.mel.graphics;

import com.alice.mel.engine.Scene;
import org.javatuples.Pair;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * Texture data class
 * @author Bahar Demircan
 */
public final class Texture extends Asset {

    private final HashMap<Scene, Integer> ids = new HashMap<>();
    private int width, height;
    private  byte[] pixels;



    public TextureFilter minFilter = TextureFilter.Nearest;
    public TextureFilter magFilter = TextureFilter.Nearest;
    public TextureWrap uWrap = TextureWrap.ClampToEdge;
    public TextureWrap vWrap = TextureWrap.ClampToEdge;

    /**
     * @param file Texture filepath
     */
    public Texture (String file){
        this(new File(file));
    }

    /**
     * @param file Texture file
     */
    public Texture (File file) {

        BufferedImage img = null;

        fileInfo = Pair.with(file, file.lastModified());

        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert img != null;
        width = img.getWidth();
        height = img.getHeight();
        pixels = new byte[width*height*4];


        int [] rawPixels = img.getRGB(0, 0, width, height, null, 0, width);

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int pixel = rawPixels[i*width + j];
                pixels[(i*width + j) * 4] = ((byte)((pixel >> 16) & 0xFF)); //RED
                pixels[(i*width + j) * 4 + 1] = ( (byte)((pixel >> 8) & 0xFF)); //GREEN
                pixels[(i*width + j) * 4 + 2] = ((byte)(pixel & 0xFF)); //BLUE
                pixels[(i*width + j) * 4 + 3] = ((byte)((pixel >> 24) & 0xFF)); //ALPHA
            }
        }
        Collections.reverse(Arrays.asList(pixels));
    }

    /**
     * @param bufferedImage Buffered Image Texture is loaded to
     */
    public Texture(BufferedImage bufferedImage){

        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();
        pixels = new byte[width*height*4];


        int [] rawPixels = bufferedImage.getRGB(0, 0, width, height, null, 0, width);

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int pixel = rawPixels[i*width + j];
                pixels[(i*width + j) * 4] = ((byte)((pixel >> 16) & 0xFF)); //RED
                pixels[(i*width + j) * 4 + 1] = ( (byte)((pixel >> 8) & 0xFF)); //GREEN
                pixels[(i*width + j) * 4 + 2] = ((byte)(pixel & 0xFF)); //BLUE
                pixels[(i*width + j) * 4 + 3] = ((byte)((pixel >> 24) & 0xFF)); //ALPHA
            }
        }
    }


    /**
     * @param width Texture Width
     * @param height Texture Height
     * @param rawPixels Raw Pixels
     */
    public Texture (int width, int height, int[] rawPixels) {

        this.width = width;
        this.height = height;

        pixels = new byte[width*height*4];

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int pixel = rawPixels[i*width + j];
                pixels[(i*width + j) * 4] = ((byte)((pixel >> 16) & 0xFF)); //RED
                pixels[(i*width + j) * 4 + 1] = ( (byte)((pixel >> 8) & 0xFF)); //GREEN
                pixels[(i*width + j) * 4 + 2] = ((byte)(pixel & 0xFF)); //BLUE
                pixels[(i*width + j) * 4 + 3] = ((byte)((pixel >> 24) & 0xFF)); //ALPHA
            }
        }

    }


    /**
     * Regenerate Texture from a file
     * @param scene Scene Texture loaded to
     * @param file New Texture filepath
     */
    public void regenTexture(Scene scene, String file){
        regenTexture(scene, new File(file));
    }

    /**
     * Regenerate Texture from a file
     * @param scene Scene Texture loaded to
     * @param file New Texture file
     */
    public void regenTexture(Scene scene, File file){
        BufferedImage img = null;
        try {
            img = ImageIO.read(file);
            regenTexture(scene, img);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Regenerate Texture from data
     * @param scene Scene Texture loaded to
     * @param width Texture Width
     * @param height Texture Height
     * @param rawPixels Raw Pixels
     */
    public void regenTexture(Scene scene, int width, int height, int[] rawPixels){
        this.width = width;
        this.height = height;

        pixels = new byte[width*height*4];

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int pixel = rawPixels[i*width + j];
                pixels[(i*width + j) * 4] = ((byte)((pixel >> 16) & 0xFF)); //RED
                pixels[(i*width + j) * 4 + 1] = ( (byte)((pixel >> 8) & 0xFF)); //GREEN
                pixels[(i*width + j) * 4 + 2] = ((byte)(pixel & 0xFF)); //BLUE
                pixels[(i*width + j) * 4 + 3] = ((byte)((pixel >> 24) & 0xFF)); //ALPHA
            }
        }
        setTextureFilter(scene, minFilter, magFilter);
        setTextureWrap(scene, uWrap, vWrap);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, BufferUtils.createByteBuffer(pixels.length).put(pixels).flip());
        unbind();
    }

    /**
     * Regenerate Texture from a Buffered Image
     * @param scene Scene Texture loaded to
     * @param bufferedImage Buffered Image texture will be regenerated from
     */
    public void regenTexture(Scene scene, BufferedImage bufferedImage){
        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();
        pixels = new byte[width*height*4];


        int [] rawPixels = bufferedImage.getRGB(0, 0, width, height, null, 0, width);

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int pixel = rawPixels[i*width + j];
                pixels[(i*width + j) * 4] = ((byte)((pixel >> 16) & 0xFF)); //RED
                pixels[(i*width + j) * 4 + 1] = ( (byte)((pixel >> 8) & 0xFF)); //GREEN
                pixels[(i*width + j) * 4 + 2] = ((byte)(pixel & 0xFF)); //BLUE
                pixels[(i*width + j) * 4 + 3] = ((byte)((pixel >> 24) & 0xFF)); //ALPHA
            }
        }

        setTextureFilter(scene, minFilter, magFilter);
        setTextureWrap(scene, uWrap, vWrap);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, BufferUtils.createByteBuffer(pixels.length).put(pixels).flip());
        unbind();
    }

    /**
     * Generate Texture
     * @param scene Scene to be loaded
     */
    public void genTexture(Scene scene){
        int id = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        ids.put(scene, id);
        setTextureFilter(scene, minFilter, magFilter);
        setTextureWrap(scene, uWrap, vWrap);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, BufferUtils.createByteBuffer(pixels.length).put(pixels).flip());
        unbind();
    }

    /**
     * @param scene Scene is gonna be loaded to
     * @param minFilter Min Filter Mode
     * @param magFilter Mag Filter Mode
     */
    public void setTextureFilter(Scene scene, TextureFilter minFilter, TextureFilter magFilter){
        this.minFilter = minFilter;
        this.magFilter = magFilter;
        bind(scene);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minFilter.value);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, magFilter.value);
    }

    /**
     * @param scene Sceme to be bind
     * @param uWrap U Wrap
     * @param vWrap V Wrap
     */
    public void setTextureWrap(Scene scene, TextureWrap uWrap, TextureWrap vWrap){
        this.uWrap = uWrap;
        this.vWrap = vWrap;
        bind(scene);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, uWrap.value);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, vWrap.value);
    }

    /**
     * Bind the texture
     * @param scene Scene Texture loaded to
     */
    public void bind(Scene scene){
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ids.get(scene));
    }

    /**
     * Unbind Texture
     */
    public void unbind(){
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    /**
     * Get Texture Width
     * @return Texture Width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get Texture Height
     * @return Texture Height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get Pixel Buffer
     * @return Pixel Buffer
     */
    public byte[] getPixels() {
        return pixels;
    }

    /**
     * Get Texture ID
     * @param scene Scene Texture loaded to
     * @return Texture ID
     */
    public int getID(Scene scene){
        return ids.get(scene);
    }

    /**
     * Dispose Texture
     * @param scene Scene Texture loaded to
     */
    public void dispose(Scene scene) {
        GL11.glDeleteTextures(ids.get(scene));
        ids.remove(scene);
    }

    public enum TextureFilter {
        Nearest(GL20.GL_NEAREST),
        Linear(GL20.GL_LINEAR),
        MipMap(GL20.GL_LINEAR_MIPMAP_LINEAR),
        MipMapNearestNearest(GL20.GL_NEAREST_MIPMAP_NEAREST),
        MipMapLinearNearest(GL20.GL_LINEAR_MIPMAP_NEAREST),
        MipMapNearestLinear(GL20.GL_NEAREST_MIPMAP_LINEAR),
        MipMapLinearLinear(GL20.GL_LINEAR_MIPMAP_LINEAR);
        final int value;
        TextureFilter (int value) {
            this.value = value;
        }
        public boolean isMipMap () {
            return value != GL20.GL_NEAREST && value != GL20.GL_LINEAR;
        }
        public int getValue () {
            return value;
        }
    }

    public enum TextureWrap {
        MirroredRepeat(GL20.GL_MIRRORED_REPEAT), ClampToEdge(GL20.GL_CLAMP_TO_EDGE), Repeat(GL20.GL_REPEAT);
        final int value;
        TextureWrap (int value) {
            this.value = value;
        }

        public int getValue () {
            return value;
        }
    }

}

