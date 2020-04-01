package ee.taltech.iti0200.graphics;

import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordinateConverterTest {

    CoordinateConverter converter;
    Camera camera;
    Player player;

    @BeforeEach
    void setUp() {
        converter = new CoordinateConverter(new Camera(player));
    }

    @Test
    void cameraToPhysicsAndBack() {
        Vector testVector = new Vector(500, 25);
        Vector answerVector = converter.physicsToCamera(converter.cameraToPhysics(testVector));
        Vector roundedAnswer = new Vector(Math.round(answerVector.getX()), Math.round(answerVector.getY()));
        assertEquals(testVector, roundedAnswer);
    }

    @Test
    void physicsToCameraAndBack() {
        Vector testVector = new Vector(500, 25);
        Vector answerVector = converter.cameraToPhysics(converter.physicsToCamera(testVector));
        Vector roundedAnswer = new Vector(Math.round(answerVector.getX()), Math.round(answerVector.getY()));
        assertEquals(testVector, roundedAnswer);
    }

    @Test
    void screenToCameraAndBack() {
        Vector testVector = new Vector(500, 25);
        Vector answerVector = converter.cameraToScreen(converter.screenToCamera(testVector));
        Vector roundedAnswer = new Vector(Math.round(answerVector.getX()), Math.round(answerVector.getY()));
        assertEquals(testVector, roundedAnswer);
    }

    @Test
    void cameraToScreenAndBack() {
        Vector testVector = new Vector(500, 25);
        Vector answerVector = converter.screenToCamera(converter.cameraToScreen(testVector));
        Vector roundedAnswer = new Vector(Math.round(answerVector.getX()), Math.round(answerVector.getY()));
        assertEquals(testVector, roundedAnswer);
    }
}