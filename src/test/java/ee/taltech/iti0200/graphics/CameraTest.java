package ee.taltech.iti0200.graphics;

import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CameraTest {

    Player player;
    Camera camera;

    @BeforeEach
    void setUp() {
        camera = new Camera(1200, 800, player);
    }

    @Test
    void cameraToPhysicsAndBack() {
        Vector testVector = new Vector(500, 25);
        Vector answerVector = camera.physicsToCamera(camera.cameraToPhysics(testVector));
        Vector roundedAnswer = new Vector(Math.round(answerVector.getX()), Math.round(answerVector.getY()));
        assertEquals(testVector, roundedAnswer);
    }

    @Test
    void physicsToCameraAndBack() {
        Vector testVector = new Vector(500, 25);
        Vector answerVector = camera.cameraToPhysics(camera.physicsToCamera(testVector));
        Vector roundedAnswer = new Vector(Math.round(answerVector.getX()), Math.round(answerVector.getY()));
        assertEquals(testVector, roundedAnswer);
    }

    @Test
    void screenToCameraAndBack() {
        Vector testVector = new Vector(500, 25);
        Vector answerVector = camera.cameraToScreen(camera.screenToCamera(testVector));
        Vector roundedAnswer = new Vector(Math.round(answerVector.getX()), Math.round(answerVector.getY()));
        assertEquals(testVector, roundedAnswer);
    }

    @Test
    void cameraToScreenAndBack() {
        Vector testVector = new Vector(500, 25);
        Vector answerVector = camera.screenToCamera(camera.cameraToScreen(testVector));
        Vector roundedAnswer = new Vector(Math.round(answerVector.getX()), Math.round(answerVector.getY()));
        assertEquals(testVector, roundedAnswer);
    }
}