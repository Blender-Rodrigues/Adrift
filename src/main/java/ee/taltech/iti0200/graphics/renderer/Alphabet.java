package ee.taltech.iti0200.graphics.renderer;

import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.Model;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.Image;
import ee.taltech.iti0200.graphics.CharacterTexture;

import java.io.IOException;
import java.util.HashMap;

import org.joml.Matrix4f;

public class Alphabet {

    private static final String PATH = "textures/";

    private static final HashMap<Character, CharacterTexture> textures = new HashMap<>();

    private int width;
    private int height;
    private Image image;
    private Shader shader;
    private Model model;

    public Alphabet(String directory, String filename) throws IOException {
        image = new Image(PATH + directory + filename + ".png");

        width = image.getWidth();
        height = image.getHeight();

        for (int i = 0; i < 256; i++) {
            textures.put((char) i, new CharacterTexture(image, i));
        }

        shader = new Shader("alphabet");
    }

    public void initialize() {
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

        model = new Model(vertices, texture, indices);
    }

    public void render(char letter, Camera camera, int x, int y, float size) {
        CharacterTexture texture = textures.get(letter);

        Matrix4f projection = new Matrix4f();
        projection.translate(-1, 1, 0);
        projection.translate(size / camera.getWidth(), - size / camera.getHeight(), 0);
        projection.translate(2f * x / camera.getWidth(), - 2f * y / camera.getHeight(), 0);
        projection.scale(size / camera.getWidth(), size / camera.getHeight(), 1f);

        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("projection", projection);

        texture.bind(0);
        model.render();
    }

    public Shader getShader() {
        return shader;
    }

}
