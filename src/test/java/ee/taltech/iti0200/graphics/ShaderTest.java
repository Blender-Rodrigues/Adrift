package ee.taltech.iti0200.graphics;

import java.io.IOException;

class ShaderTest extends GraphicsTest {

    @GuiTest
    void readFileDoesNotThrow() throws IOException {
        new Shader("shader");
    }

}
