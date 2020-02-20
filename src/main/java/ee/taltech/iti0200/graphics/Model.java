package ee.taltech.iti0200.graphics;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Model {
    private int drawCount;
    private int vertexId;
    private int textureId;

    public Model(float[] vertices, float[] textureCoordinates) {
        drawCount = vertices.length / 2;


        vertexId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexId);
        glBufferData(GL_ARRAY_BUFFER, createBuffer(vertices), GL_STATIC_DRAW);

        textureId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, textureId);
        glBufferData(GL_ARRAY_BUFFER, createBuffer(textureCoordinates), GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void render() {
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        glBindBuffer(GL_ARRAY_BUFFER, vertexId);
        glVertexPointer(2, GL_FLOAT, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, textureId);
        glTexCoordPointer(2, GL_FLOAT, 0, 0);

        glDrawArrays(GL_TRIANGLES, 0, drawCount);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);

    }

    private FloatBuffer createBuffer(float[] vertices) {

        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices);
        buffer.flip();

        return buffer;
    }
}
