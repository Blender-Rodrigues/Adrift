package ee.taltech.iti0200.menu;

import ee.taltech.iti0200.graphics.Model;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.TextBox;
import ee.taltech.iti0200.graphics.Texture;
import ee.taltech.iti0200.graphics.ViewPort;
import ee.taltech.iti0200.graphics.renderer.Alphabet;
import ee.taltech.iti0200.graphics.renderer.Renderer;

import javax.inject.Inject;
import java.io.IOException;

public class MenuRenderer implements Renderer {

    private static final int FONT_SIZE = 16;
    private static final int TOP_OFFSET = 300;
    private static final int LEFT_OFFSET = 450;
    private static final int RIGHT_OFFSET = 100;
    private static final int TOP_MARGIN = 50;
    private static final String PREFIX = ">";

    private final Menu menu;
    private final Alphabet alphabet;

    private String cursor = PREFIX;
    private Texture background;
    private Model model;

    @Inject
    public MenuRenderer(Alphabet alphabet, Menu menu) {
        this.alphabet = alphabet;
        this.menu = menu;
    }

    @Override
    public void initialize() throws IOException {
        alphabet.initialize();
        background = new Texture("", "asteroids");
        model = Renderer.square;
    }

    @Override
    public void render(Shader shader, ViewPort viewPort, long tick) {
        float[] dimensions = background.scaleToViewPort(viewPort);
        alphabet.getShader().bind();
        alphabet.getShader().setUniform("sampler", 0);
        alphabet.getShader().setUniform("projection", viewPort.getStaticProjection(0, 0, dimensions[0], dimensions[1]));
        background.bind(0);
        model.render();

        if (tick % 20 == 0) {
            cursor = PREFIX.equals(cursor) ? " " : PREFIX;
        }

        int center = viewPort.getWidth() / 2;
        int left_offset = center - LEFT_OFFSET;
        int right_offset = center + RIGHT_OFFSET;

        if (!menu.getMessage().isEmpty()) {
            new TextBox(left_offset, TOP_MARGIN, menu.getMessage(), FONT_SIZE).render(alphabet, viewPort);
        }

        new TextBox(left_offset, TOP_OFFSET, menu.getSinglePlayer().getText(cursor), FONT_SIZE).render(alphabet, viewPort);
        new TextBox(right_offset, TOP_OFFSET, menu.getClient().getText(cursor), FONT_SIZE).render(alphabet, viewPort);
        new TextBox(right_offset, TOP_OFFSET + TOP_MARGIN, menu.getHost().getText(cursor), FONT_SIZE).render(alphabet, viewPort);
        new TextBox(right_offset, TOP_OFFSET + TOP_MARGIN * 2, menu.getPort().getText(cursor), FONT_SIZE).render(alphabet, viewPort);
        new TextBox(right_offset, TOP_OFFSET + TOP_MARGIN * 3, menu.getPlayerName().getText(cursor), FONT_SIZE).render(alphabet, viewPort);
        new TextBox(right_offset, TOP_OFFSET + TOP_MARGIN * 6, menu.getExit().getText(cursor), FONT_SIZE).render(alphabet, viewPort);
    }

}
