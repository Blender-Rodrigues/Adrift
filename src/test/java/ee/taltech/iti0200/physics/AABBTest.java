package ee.taltech.iti0200.physics;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class AABBTest {

    @Test
    void testIntersectSamePosition() {
        BoundingBox box1 = new BoundingBox(
            new Vector(5.0, 5.0),
            new Vector(1.0, 1.0),
            true
        );
        BoundingBox box2 = new BoundingBox(
            new Vector(5.0, 5.0),
            new Vector(2.0, 2.0),
            true
        );
        assertTrue(box1.intersects(box2));
        assertTrue(box2.intersects(box1));
    }

    @Test
    void testIntersectCenterOutsideOtherBoxButIntersecting() {
        BoundingBox box1 = new BoundingBox(
            new Vector(5.0, 5.0),
            new Vector(1.0, 1.0),
            true
        );
        BoundingBox box2 = new BoundingBox(
            new Vector(5.75, 5.75),
            new Vector(2.0, 2.0),
            true
        );
        assertTrue(box1.intersects(box2));
        assertTrue(box2.intersects(box1));
    }

    @Test
    void testIntersectPlayerFallingThroughFloorClosest() {
        BoundingBox player = new BoundingBox(
            new Vector(20.0, 1.05),
            new Vector(1.5, 1.5),
            true
        );
        BoundingBox terrain = new BoundingBox(
            new Vector(19.0, 1.0),
            new Vector(2.0, 2.0),
            true
        );
        assertTrue(player.intersects(terrain));
        assertTrue(terrain.intersects(player));
    }

}