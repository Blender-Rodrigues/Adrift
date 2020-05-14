package ee.taltech.iti0200.graphics.renderer;

import ee.taltech.iti0200.graphics.Model;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.ViewPort;

import java.io.IOException;

public interface Renderer {

    Model square = createSquare();

    void initialize() throws IOException;
    void render(Shader shader, ViewPort viewPort, long tick);

    static Model createSquare() {
        float[] vertices = new float[]{
            -1f, 1f, 0,
            1f, 1f, 0,
            1f, -1f, 0,
            -1f, -1f, 0
        };

        float[] texture = new float[]{
            0, 0,
            1, 0,
            1, 1,
            0, 1
        };

        int[] indices = new int[]{
            0, 1, 2,
            2, 3, 0
        };

        return new Model(vertices, texture, indices);
    }

}
