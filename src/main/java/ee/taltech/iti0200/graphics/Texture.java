package ee.taltech.iti0200.graphics;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Texture {

    private static final String PATH = "textures/";

    private int id;
    private int width;
    private int height;

    public Texture(String directory, String filename) throws IOException {
        Image image = new Image(PATH + directory + filename + ".png");

        width = image.getWidth();
        height = image.getHeight();
        ByteBuffer pixels = image.getPixels();

        id = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, id);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
    }

    public void bind(int sampler) {
        if (sampler >= 0 && sampler <= 31) {
            glActiveTexture(GL_TEXTURE0 + sampler);
            glBindTexture(GL_TEXTURE_2D, id);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Returns [width, height] matched to cover whole viewport
     */
    public float[] scaleToViewPort(ViewPort viewPort) {
        float viewPortWidth = viewPort.getWidth();
        float viewPortHeight = viewPort.getHeight();

        float cameraRatio = viewPortWidth / viewPortHeight;
        float ratio = (float) width / (float) height;

        if (cameraRatio >= ratio) {
            return new float[] {viewPortWidth, viewPortWidth / ratio};
        } else {
            return new float[] {viewPortHeight * ratio, viewPortHeight};
        }
    }

}
