package ee.taltech.iti0200.physics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BodyTest {

    @Test
    void testIntersectPlayerFallingThroughFloorClosest() {

        Body player = new Body(
            70.0,
            new Vector(20.0, 1.057),
            new Vector(1.5, 1.5),
            true,
            true
        );
        Body terrain = new Body(
            Float.POSITIVE_INFINITY,
            new Vector(19.0, 1.0),
            new Vector(2.0, 2.0),
            true,
            true
        );
        assertTrue(player.intersects(terrain));
        assertTrue(terrain.intersects(player));
    }

}