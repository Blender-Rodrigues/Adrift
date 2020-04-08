package ee.taltech.iti0200.graphics;

import java.io.IOException;

public class GuiRenderFacade implements Renderer {

    private TextBox gameName;
    private Alphabet alphabet;

    @Override
    public void initialize() throws IOException {
        gameName = new TextBox(0, 0, "0123456789", 3);
        alphabet = new Alphabet("fonts/", "basicFont");
        alphabet.initialize();
    }

    @Override
    public void render(Shader shader, Camera camera, long tick) {
        gameName.render(alphabet, camera);
    }


}
