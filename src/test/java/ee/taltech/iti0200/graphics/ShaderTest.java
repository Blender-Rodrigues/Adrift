package ee.taltech.iti0200.graphics;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class ShaderTest extends GuiTestBase {

    @Test
    void readFileDoesNotThrow() throws IOException {
        new Shader("shader");
    }

}
