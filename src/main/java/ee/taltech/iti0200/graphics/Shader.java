package ee.taltech.iti0200.graphics;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private static final String PATH = "./build/resources/main/shaders/";

    private int program = glCreateProgram();

    /**
     * Processes the vertices that the shader takes
     */
    private int vertexShader = glCreateShader(GL_VERTEX_SHADER);

    /**
     * Gives everything color
     */
    private int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);

    public Shader(String filename) throws IOException {
        glShaderSource(vertexShader, readFile(filename + ".vs"));
        glCompileShader(vertexShader);
        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) != 1) {
            throw new IllegalStateException(glGetShaderInfoLog(vertexShader));
        }

        glShaderSource(fragmentShader, readFile(filename + ".fs"));
        glCompileShader(fragmentShader);
        if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) != 1) {
            throw new IllegalStateException(glGetShaderInfoLog(fragmentShader));
        }

        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);

        glBindAttribLocation(program, 0, "vertices");
        glBindAttribLocation(program, 1, "textures");

        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) != 1) {
            throw new IllegalStateException(glGetProgramInfoLog(program));
        }

        glValidateProgram(program);
        if (glGetProgrami(program, GL_VALIDATE_STATUS) != 1) {
            throw new IllegalStateException(glGetProgramInfoLog(program));
        }
    }

    public void setUniform(String name, int value) {
        int location = glGetUniformLocation(program, name);
        if (location != -1) {
            glUniform1i(location, value);
        }
    }

    public void setUniform(String name, Matrix4f value) {
        int location = glGetUniformLocation(program, name);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        value.get(buffer);
        if (location != -1) {
            glUniformMatrix4fv(location, false, buffer);
        }
    }

    public void bind() {
        glUseProgram(program);
    }

    private String readFile(String filename) throws IOException {
        StringBuilder string = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(PATH + filename)))) {
            String line;
            while ((line = br.readLine()) != null) {
                string.append(line);
                string.append("\n");
            }
        }

        return string.toString();
    }

}
