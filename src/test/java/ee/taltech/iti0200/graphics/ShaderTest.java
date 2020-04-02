package ee.taltech.iti0200.graphics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import java.io.IOException;

@DisabledIfEnvironmentVariable(named = "NO_GUI", matches = "true")
class ShaderTest extends GraphicsTest {

    @Test
    void readFileDoesNotThrow() throws IOException {
        new Shader("shader");
    }

}
