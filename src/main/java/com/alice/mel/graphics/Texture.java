package com.alice.mel.graphics;

import com.alice.mel.utils.Disposable;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public final class Texture implements Disposable {

    public transient int id = 0;
    public final int width, height;
    public final int[] pixels;

    public Texture (String file) throws IOException {

        BufferedImage img = ImageIO.read(new File(file));
        width = img.getWidth();
        height = img.getHeight();
        pixels = img.getRGB(0, 0, width, height, null, 0, width);

    }


    public void genTexture(){

        ByteBuffer pixels = BufferUtils.createByteBuffer(width*height*4);

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int pixel = this.pixels[i*width + j];
                pixels.put((byte)((pixel >> 16) & 0xFF)); //RED
                pixels.put((byte)((pixel >> 8) & 0xFF)); //GREEN
                pixels.put((byte)(pixel & 0xFF)); //BLUE
                pixels.put((byte)((pixel >> 24) & 0xFF)); //ALPHA
            }
        }

        pixels.flip();

        id = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void bind(){
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
    }

    @Override
    public void dispose() {
        if(id != 0)
            GL11.glDeleteTextures(id);
    }
}

