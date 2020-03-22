package ee.taltech.iti0200.graphics;

import ee.taltech.iti0200.domain.entity.Player;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    public static final float CAMERA_SENSITIVITY = 20f;
    public static final int RENDER_SCALE_MULTIPLIER = 1;

    /* zoom in and zoom out are "inverted" -> the smaller the value, the farther the camera is from the map */
    public static final float MINIMUM_ZOOM_VALUE = 0.005f;
    public static final float MAXIMUM_ZOOM_VALUE = 0.2f;

    private Vector3f position;
    private Matrix4f projection;
    private Matrix4f target;
    private Matrix4f pos;

    private int width;
    private int height;

    private float zoom;

    private Player player;
    private boolean followingPlayer = true;

    public Camera(int width, int height, Player player) {
        this.width = width;
        this.height = height;
        this.zoom = 0.03f;
        this.player = player;

        position = new Vector3f(0, 0, 0);
        projection = new Matrix4f().setOrtho2D(
            -width * zoom / 2f,
            width * zoom / 2f,
            -height * zoom / 2f,
            height * zoom / 2f);
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    // TODO: a better way to set zoom, rather than creating a new projection.
    public void setZoom(float zoom) {
        projection = new Matrix4f().setOrtho2D(
            -width * zoom / 2f,
            width * zoom / 2f,
            -height * zoom / 2f,
            height * zoom / 2f
        );
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
