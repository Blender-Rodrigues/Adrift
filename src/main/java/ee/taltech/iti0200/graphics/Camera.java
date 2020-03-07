package ee.taltech.iti0200.graphics;

import ee.taltech.iti0200.domain.Player;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    // millel peaksid meetodid olema ja millel mitte? kas nt addPosition on vajalik?
    // kas kaamerat tuleks kontrollida graphics v camera alt?
    // kas kaamera asukohta tuleks ka siis muuta, kui see muutuma ei peaks? mingi if check update alla?

    public static final int CAMERA_SENSITIVITY = 25;
    public static final int RENDER_SCALE_MULTIPLIER = 32;

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
        this.zoom = 1f;
        this.player = player;

        position = new Vector3f(0, 0, 0);
        projection = new Matrix4f().setOrtho2D(-width/2f, width/2f, -height/2f, height/2f);
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void addPosition(Vector3f position) {
        this.position.add(position);
    }

    // TODO: a better way to set zoom, rather than creating a new projection.
    public void setZoom(float zoom) {
        projection = new Matrix4f().setOrtho2D(-width*zoom/2f, width*zoom/2f, -height*zoom/2f, height*zoom/2f);
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

    public void zoomIn() {
        zoom = Math.round(zoom / 1.1 * 100)/100f;
        setZoom(zoom);
    }

    public void zoomOut() {
        zoom = Math.round(zoom * 1.1 * 100)/100f;
        setZoom(zoom);
    }

    public void togglePlayerCam() {
        followingPlayer = !followingPlayer;
    }

    public void update() {
        if(followingPlayer) {
            setPosition(new Vector3f(
                    (float)-player.getBoundingBox().getCentre().x * RENDER_SCALE_MULTIPLIER,
                    (float)-player.getBoundingBox().getCentre().y * RENDER_SCALE_MULTIPLIER,
                    0)
            );
        }
    }
}
