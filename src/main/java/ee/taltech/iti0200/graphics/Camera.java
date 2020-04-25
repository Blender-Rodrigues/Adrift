package ee.taltech.iti0200.graphics;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.LocalPlayer;
import ee.taltech.iti0200.domain.entity.Player;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    public static final float CAMERA_SENSITIVITY = 20f;
    public static final int RENDER_SCALE_MULTIPLIER = 1;
    /* zoom in and zoom out are "inverted" -> the smaller the value, the farther the camera is from the map */
    public static final float MINIMUM_ZOOM_VALUE = 0.005f;
    public static final float MAXIMUM_ZOOM_VALUE = 0.2f;
    public static final float INITIAL_ZOOM_VALUE = 0.03f;

    private Vector3f position = new Vector3f(0, 0, 0);
    private Matrix4f projection;
    private Matrix4f target;
    private Matrix4f pos;
    private float zoom = INITIAL_ZOOM_VALUE;
    private int width;
    private int height;

    private Player player;
    private boolean followingPlayer = true;

    @Inject
    public Camera(@LocalPlayer Player player) {
        this.player = player;
    }

    public int getWidth() {
        return width;
    }

    public Camera setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public Camera setHeight(int height) {
        this.height = height;
        return this;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    // TODO: a better way to set zoom, rather than creating a new projection.
    public Camera setZoom(float zoom) {
        projection = new Matrix4f().setOrtho2D(
            -width * zoom / 2f,
            width * zoom / 2f,
            -height * zoom / 2f,
            height * zoom / 2f
        );
        return this;
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

    public void moveLeft() {
        position.add(new Vector3f(CAMERA_SENSITIVITY * zoom, 0, 0));
    }

    public void moveRight() {
        position.add(new Vector3f(-CAMERA_SENSITIVITY * zoom, 0, 0));
    }

    public void moveUp() {
        position.add(new Vector3f(0, -CAMERA_SENSITIVITY * zoom, 0));
    }

    public void moveDown() {
        position.add(new Vector3f(0, CAMERA_SENSITIVITY * zoom, 0));
    }

    public void zoomIn() {
        zoom = Math.max(zoom / 1.05f, MINIMUM_ZOOM_VALUE);
        setZoom(zoom);
    }

    public void zoomOut() {
        zoom = Math.min(zoom * 1.05f, MAXIMUM_ZOOM_VALUE);
        setZoom(zoom);
    }

    public float getZoom() {
        return zoom;
    }

    public void togglePlayerCam() {
        followingPlayer = !followingPlayer;
    }

    public void update() {
        if (followingPlayer) {
            setPosition(new Vector3f(
                (float) -player.getBoundingBox().getCentre().x * RENDER_SCALE_MULTIPLIER,
                (float) -player.getBoundingBox().getCentre().y * RENDER_SCALE_MULTIPLIER,
                0
            ));
        }
    }

}
