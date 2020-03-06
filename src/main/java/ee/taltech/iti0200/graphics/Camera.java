package ee.taltech.iti0200.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    public static final int CAMERA_SENSITIVITY = 100;

    private Vector3f position;
    private Matrix4f projection;
    private Matrix4f target;
    private Matrix4f pos;

    private int width;
    private int height;

    private float zoom;

    public Camera(int width, int height) {
        this.width = width;
        this.height = height;
        this.zoom = 1f;

        position = new Vector3f(0, 0, 0);
        projection = new Matrix4f().setOrtho2D(-width/2f, width/2f, -height/2f, height/2f);
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void addPosition(Vector3f position) {
        this.position.add(position);
    }

    // TODO: modify projection

    public Vector3f getPosition() {
        return position;
    }

    public Matrix4f getProjection() {
        target = new Matrix4f();
        pos = new Matrix4f().setTranslation(position);

        target = projection.mul(pos, target);
        return target;
    }

    public void moveLeft() {
        addPosition(new Vector3f(CAMERA_SENSITIVITY, 0, 0));
    }

    public void moveRight() {
        addPosition(new Vector3f(-CAMERA_SENSITIVITY, 0, 0));
    }

    public void moveUp() {
        addPosition(new Vector3f(0, -CAMERA_SENSITIVITY, 0));
    }

    public void moveDown() {
        addPosition(new Vector3f(0, CAMERA_SENSITIVITY, 0));
    }

    //TODO: solve zoom with a modify projection method
    //TODO: DRY!
    //TODO: zooming out and in again should reset the original zoom
    public void zoomIn() {
        zoom /= 1.1;

        projection = new Matrix4f().setOrtho2D(-width*zoom/2f, width*zoom/2f, -height*zoom/2f, height*zoom/2f);
    }

    public void zoomOut() {
        zoom *= 1.1;

        projection = new Matrix4f().setOrtho2D(-width*zoom/2f, width*zoom/2f, -height*zoom/2f, height*zoom/2f);
    }
}
