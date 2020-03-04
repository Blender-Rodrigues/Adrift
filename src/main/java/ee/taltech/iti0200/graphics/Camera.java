package ee.taltech.iti0200.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position;
    private Matrix4f projection;
    private Matrix4f target;
    private Matrix4f pos;

    public Camera(int width, int height) {
        position = new Vector3f(0, 0, 0);
        projection = new Matrix4f().setOrtho2D(-width/2f, width/2f, -height/2f, height/2f);
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void addPosition(Vector3f position) {
        this.position.add(position);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Matrix4f getProjection() {
        target = new Matrix4f();
        pos = new Matrix4f().setTranslation(position);

        target = projection.mul(pos, target);
        return target;
    }
}
