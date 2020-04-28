package ee.taltech.iti0200.graphics.renderer;

import ee.taltech.iti0200.graphics.CharacterTexture;
import ee.taltech.iti0200.graphics.Image;
import ee.taltech.iti0200.graphics.Model;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.ViewPort;

import java.io.IOException;
import java.util.HashMap;

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
        model = Renderer.square;
    }

    public void render(char letter, ViewPort viewPort, int x, int y, float size) {
        CharacterTexture texture = textures.get(letter);

        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("projection", viewPort.getStaticProjection(x, y, size, size));

        texture.bind(0);
        model.render();
    }

    public Shader getShader() {
        return shader;
    }

}
