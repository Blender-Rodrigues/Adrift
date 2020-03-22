package ee.taltech.iti0200.graphics;

import ee.taltech.iti0200.physics.Body;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*; // needed for samplers, which are targets for textures

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Texture {

    private static final String PATH = "./build/resources/main/textures/";

    private int id;
    private int width;
    private int height;

    private final Logger logger = LogManager.getLogger(Body.class);

    public Texture(String directory, String filename) {
        try {
            BufferedImage image = ImageIO.read(new File(PATH + directory + filename + ".png"));

            width = image.getWidth();
            height = image.getHeight();

            int[] pixelsRaw = image.getRGB(0, 0, width, height, null, 0, width);

            ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int pixel = pixelsRaw[i * width + j];
                    pixels.put((byte) ((pixel >> 16) & 0xFF));
                    pixels.put((byte) ((pixel >> 8) & 0xFF));
                    pixels.put((byte) (pixel & 0xFF));
                    pixels.put((byte) ((pixel >> 24) & 0xFF));

                }
            }

            pixels.flip();

            id = glGenTextures();

            glBindTexture(GL_TEXTURE_2D, id);

            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);

        } catch (IOException e) {
            logger.error("Failed to change texture: " + e.getMessage(), e);
        }
    }

    public void bind(int sampler) {
        if (sampler >= 0 && sampler <= 31) {
            glActiveTexture(GL_TEXTURE0 + sampler);
            glBindTexture(GL_TEXTURE_2D, id);
        }
    }

}
