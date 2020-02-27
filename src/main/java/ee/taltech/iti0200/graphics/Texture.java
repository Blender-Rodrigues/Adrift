package ee.taltech.iti0200.graphics;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Texture {
    private int id;
    private int width;
    private int height;

    public Texture(String filename) throws IOException {
        BufferedImage image;

        image = ImageIO.read(new File(filename));
        width = image.getWidth();
        height = image.getHeight();

        int[] pixelsRaw = image.getRGB(0, 0, width, height, null, 0, width);

        ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = pixelsRaw[i*width + j];
                pixels.put((byte)((pixel >> 16) & 0xFF));
                pixels.put((byte)((pixel >> 8) & 0xFF));
                pixels.put((byte)(pixel & 0xFF));
                pixels.put((byte)((pixel >> 24) & 0xFF));

            }
        }

        pixels.flip();

        id = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, id);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
    }


    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }
}
