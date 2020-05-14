package ee.taltech.iti0200.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ViewPort {

    public static final float INITIAL_ZOOM_VALUE = 0.03f;

    protected Vector3f position = new Vector3f(0, 0, 0);
    protected Matrix4f projection;
    protected float zoom = INITIAL_ZOOM_VALUE;
    protected int width;
    protected int height;

    public int getWidth() {
        return width;
    }

    public ViewPort setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public ViewPort setHeight(int height) {
        this.height = height;
        return this;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    // TODO: a better way to set zoom, rather than creating a new projection.
    public Vector3f getPosition() {
        return position;
    }

    public Matrix4f getProjection() {
        Matrix4f target = new Matrix4f();
        Matrix4f pos = new Matrix4f().setTranslation(position);

        target = projection.mul(pos, target);
        return target;
    }

    /**
     * By some magical mathemagics by Kristjan, the dimensions are in pixels.
     */
    public Matrix4f getStaticProjection(int x, int y, float width, float height) {
        Matrix4f projection = new Matrix4f();
        projection.translate(-1, 1, 0);
        projection.translate(width / this.width, - height / this.height, 0);
        projection.translate(2f * x / this.width, - 2f * y / this.height, 0);
        projection.scale(width / this.width, height / this.height, 1f);

        return projection;
    }

    public ViewPort setZoom(float zoom) {
        projection = new Matrix4f().setOrtho2D(
            -width * zoom / 2f,
            width * zoom / 2f,
            -height * zoom / 2f,
            height * zoom / 2f
        );
        return this;
    }

    public float getZoom() {
        return zoom;
    }

    public void update() {

    }

}
