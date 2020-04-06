package ee.taltech.iti0200.graphics;

import java.io.IOException;

public interface Renderer {

    void initialize() throws IOException;
    void render(Shader shader, Camera camera, long tick);

}
