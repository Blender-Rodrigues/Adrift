package ee.taltech.iti0200.graphics;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.LocalPlayer;
import ee.taltech.iti0200.domain.entity.Player;
import org.joml.Vector3f;

public class Camera extends ViewPort {

    private static final float CAMERA_SENSITIVITY = 20f;
    private static final int RENDER_SCALE_MULTIPLIER = 1;
    /* zoom in and zoom out are "inverted" -> the smaller the value, the farther the camera is from the map */
    private static final float MINIMUM_ZOOM_VALUE = 0.005f;
    private static final float MAXIMUM_ZOOM_VALUE = 0.2f;

    private final Player player;

    private boolean followingPlayer = true;

    @Inject
    public Camera(@LocalPlayer Player player) {
        this.player = player;
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

    @Override
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
