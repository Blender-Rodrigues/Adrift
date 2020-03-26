package ee.taltech.iti0200.graphics;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

public class Image {

    private int width;
    private int height;
    private int[] rawPixels;
    private ByteBuffer pixels;

    public Image(String path) throws IOException {
        BufferedImage image = ImageIO.read(getFile(path));
        width = image.getWidth();
        height = image.getHeight();
        rawPixels = image.getRGB(0, 0, width, height, null, 0, width);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getRawPixels() {
        return rawPixels;
    }

    public ByteBuffer getPixels() {
        if (pixels == null) {
            pixels = BufferUtils.createByteBuffer(width * height * 4);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int pixel = rawPixels[i * width + j];
                    pixels.put((byte) ((pixel >> 16) & 0xFF));
                    pixels.put((byte) ((pixel >> 8) & 0xFF));
                    pixels.put((byte) (pixel & 0xFF));
                    pixels.put((byte) ((pixel >> 24) & 0xFF));
                }
            }
            pixels.flip();
        }
        return pixels;
    }

    private URL getFile(String path) {
        URL resource = Texture.class.getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Unable to find resource " + path);
        }
        return resource;
    }

}
