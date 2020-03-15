package ee.taltech.iti0200.physics;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BoundingBoxTest {

    @Test
    void testIntersectSamePosition() {
        BoundingBox box1 = new BoundingBox(
            new Vector(5.0, 5.0),
            new Vector(1.0, 1.0)
        );
        BoundingBox box2 = new BoundingBox(
            new Vector(5.0, 5.0),
            new Vector(2.0, 2.0)
        );

        assertThat(box1.intersects(box2)).isTrue();
        assertThat(box2.intersects(box1)).isTrue();
    }

    @Test
    void testIntersectCenterOutsideOtherBoxButIntersecting() {
        BoundingBox box1 = new BoundingBox(
            new Vector(5.0, 5.0),
            new Vector(1.0, 1.0)
        );
        BoundingBox box2 = new BoundingBox(
            new Vector(5.75, 5.75),
            new Vector(2.0, 2.0)
        );

        assertThat(box1.intersects(box2)).isTrue();
        assertThat(box2.intersects(box1)).isTrue();
    }

    @Test
    void testIntersectPlayerFallingThroughFloorClosest() {
        BoundingBox player = new BoundingBox(
            new Vector(20.0, 1.05),
            new Vector(1.5, 1.5)
        );
        BoundingBox terrain = new BoundingBox(
            new Vector(19.0, 1.0),
            new Vector(2.0, 2.0)
        );

        assertThat(player.intersects(terrain)).isTrue();
        assertThat(terrain.intersects(player)).isTrue();
    }

}
