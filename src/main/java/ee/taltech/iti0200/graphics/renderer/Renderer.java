package ee.taltech.iti0200.graphics.renderer;

import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.Shader;

import java.io.IOException;

public interface Renderer {

    void initialize() throws IOException;
    void render(Shader shader, Camera camera, long tick);

}
