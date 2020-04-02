package ee.taltech.iti0200.graphics;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;

public class Shader {

    private static final String PATH = "shaders/";

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
        InputStream resource = getClass().getClassLoader().getResourceAsStream(PATH + filename);
        if (resource == null) {
            throw new IllegalArgumentException("Unable to find resource " + PATH + filename);
        }

        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                lines.add(line);
            }
        }

        return String.join("\n", lines);
    }

}
