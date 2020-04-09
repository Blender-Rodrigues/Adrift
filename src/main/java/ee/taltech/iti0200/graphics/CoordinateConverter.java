package ee.taltech.iti0200.graphics;

import com.google.inject.Inject;
import ee.taltech.iti0200.physics.Vector;

public class CoordinateConverter {

    private Camera camera;

    @Inject
    public CoordinateConverter(Camera camera) {
        this.camera = camera;
    }

    public Vector cameraToPhysics(Vector cameraPosition) {
        double x = cameraPosition.getX() - camera.getPosition().get(0);
        double y = cameraPosition.getY() + camera.getPosition().get(1);
        y *= -1;
        return new Vector(x, y);
    }

    public Vector screenToCamera(Vector screenPosition) {
        Vector cameraPosition = new Vector();
        cameraPosition.scaleAdd(-0.5, new Vector(camera.getWidth(), camera.getHeight()), screenPosition);
        cameraPosition.scale(camera.getZoom());
        return cameraPosition;
    }

    public Vector cameraToScreen(Vector cameraVector) {
        Vector screenVector = new Vector(cameraVector);
        screenVector.scale(1 / camera.getZoom());
        screenVector.scaleAdd(0.5, new Vector(camera.getWidth(), camera.getHeight()), screenVector);
        return screenVector;
    }

    public Vector physicsToCamera(Vector physicsPosition) {
        double y = - physicsPosition.getY();
        double x = physicsPosition.getX();
        x += camera.getPosition().get(0);
        y -= camera.getPosition().get(1);
        return new Vector(x, y);
    }

}
