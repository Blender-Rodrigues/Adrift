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
    private int program;
    private int vertexShader; // processes the vertices that the shader takes
    private int fragmentShader; // gives everything color


    public Shader(String filename) {
        program = glCreateProgram();

        vertexShader = glCreateShader(GL_VERTEX_SHADER);
        // ./build/resources/main/smile.png
        glShaderSource(vertexShader, readFile(filename+".vs"));
        glCompileShader(vertexShader);
        if(glGetShaderi(vertexShader, GL_COMPILE_STATUS) != 1) { // if the shader is errorish
            System.err.println(glGetShaderInfoLog(vertexShader));
            System.exit(1);
        }


        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, readFile(filename+".fs"));
        glCompileShader(fragmentShader);
        if(glGetShaderi(fragmentShader, GL_COMPILE_STATUS) != 1) { // if the shader is errorish
            System.err.println(glGetShaderInfoLog(fragmentShader));
            System.exit(1);
        }

        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);

        glBindAttribLocation(program, 0, "vertices");
        glBindAttribLocation(program, 1, "textures");

        glLinkProgram(program);
        if(glGetProgrami(program, GL_LINK_STATUS) != 1) {
            System.err.println(glGetProgramInfoLog(program));
            System.exit(1);
        }

        glValidateProgram(program);
        if(glGetProgrami(program, GL_VALIDATE_STATUS) != 1) {
            System.err.println(glGetProgramInfoLog(program));
            System.exit(1);
        }
    }

    public void setUniform(String name, int value) {
        int location = glGetUniformLocation(program, name);
        if(location != -1) {
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

    private String readFile(String filename) {
        StringBuilder string = new StringBuilder();
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(new File("./build/resources/main/shaders/" + filename)));
            String line;
            while((line = br.readLine()) != null) {
                string.append(line);
                string.append("\n");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return string.toString();
    }
}
