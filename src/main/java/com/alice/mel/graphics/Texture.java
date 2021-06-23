package com.alice.mel.graphics;

import com.alice.mel.engine.Scene;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public final class Texture {

    private final HashMap<Scene, Integer> ids = new HashMap<>();
    private int width, height;
    private  ByteBuffer pixels;

    public Texture (String file) {

        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert img != null;
        width = img.getWidth();
        height = img.getHeight();
        pixels = BufferUtils.createByteBuffer(width*height*4);


        int [] rawPixels = img.getRGB(0, 0, width, height, null, 0, width);

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int pixel = rawPixels[i*width + j];
                pixels.put((byte)((pixel >> 16) & 0xFF)); //RED
                pixels.put((byte)((pixel >> 8) & 0xFF)); //GREEN
                pixels.put((byte)(pixel & 0xFF)); //BLUE
                pixels.put((byte)((pixel >> 24) & 0xFF)); //ALPHA
            }
        }

        pixels.flip();
    }

        public Texture(BufferedImage bufferedImage){

        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();
        pixels = BufferUtils.createByteBuffer(width*height*4);


        int [] rawPixels = bufferedImage.getRGB(0, 0, width, height, null, 0, width);

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int pixel = rawPixels[i*width + j];
                pixels.put((byte)((pixel >> 16) & 0xFF)); //RED
                pixels.put((byte)((pixel >> 8) & 0xFF)); //GREEN
                pixels.put((byte)(pixel & 0xFF)); //BLUE
                pixels.put((byte)((pixel >> 24) & 0xFF)); //ALPHA
            }
        }

        pixels.flip();
    }



    public Texture (int width, int height, int[] rawPixels) {

        this.width = width;
        this.height = height;

        pixels = BufferUtils.createByteBuffer(width*height*4);

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int pixel = rawPixels[i*width + j];
                pixels.put((byte)((pixel >> 16) & 0xFF)); //RED
                pixels.put((byte)((pixel >> 8) & 0xFF)); //GREEN
                pixels.put((byte)(pixel & 0xFF)); //BLUE
                pixels.put((byte)((pixel >> 24) & 0xFF)); //ALPHA
            }
        }

        pixels.flip();
    }


    public void regenTexture(Scene scene, String file){
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert img != null;
        width = img.getWidth();
        height = img.getHeight();
        pixels = BufferUtils.createByteBuffer(width*height*4);


        int [] rawPixels = img.getRGB(0, 0, width, height, null, 0, width);

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int pixel = rawPixels[i*width + j];
                pixels.put((byte)((pixel >> 16) & 0xFF)); //RED
                pixels.put((byte)((pixel >> 8) & 0xFF)); //GREEN
                pixels.put((byte)(pixel & 0xFF)); //BLUE
                pixels.put((byte)((pixel >> 24) & 0xFF)); //ALPHA
            }
        }

        pixels.flip();
        bind(scene);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
        unbind();
    }

    public void regenTexture(Scene scene, int width, int height, int[] rawPixels){
        this.width = width;
        this.height = height;

        pixels = BufferUtils.createByteBuffer(width*height*4);

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int pixel = rawPixels[i*width + j];
                pixels.put((byte)((pixel >> 16) & 0xFF)); //RED
                pixels.put((byte)((pixel >> 8) & 0xFF)); //GREEN
                pixels.put((byte)(pixel & 0xFF)); //BLUE
                pixels.put((byte)((pixel >> 24) & 0xFF)); //ALPHA
            }
        }

        pixels.flip();
        bind(scene);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
        unbind();
    }

    public void regenTexture(Scene scene, BufferedImage bufferedImage){
        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();
        pixels = BufferUtils.createByteBuffer(width*height*4);


        int [] rawPixels = bufferedImage.getRGB(0, 0, width, height, null, 0, width);

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int pixel = rawPixels[i*width + j];
                pixels.put((byte)((pixel >> 16) & 0xFF)); //RED
                pixels.put((byte)((pixel >> 8) & 0xFF)); //GREEN
                pixels.put((byte)(pixel & 0xFF)); //BLUE
                pixels.put((byte)((pixel >> 24) & 0xFF)); //ALPHA
            }
        }

        pixels.flip();
        bind(scene);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
        unbind();
    }

    public void genTexture(Scene scene){

        int id = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        ids.put(scene, id);
    }

    public void bind(Scene scene){
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ids.get(scene));
    }

    public void unbind(){
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ByteBuffer getPixels() {
        return pixels;
    }

    public int getID(Scene scene){
        return ids.get(scene);
    }

    public void dispose(Scene scene) {
        GL11.glDeleteTextures(ids.get(scene));
        ids.remove(scene);
    }
}

