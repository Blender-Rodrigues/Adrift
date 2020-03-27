package ee.taltech.iti0200.physics;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BodyTest {

    @Test
    void testIntersectPlayerFallingThroughFloorClosest() {
        Body player = new Body(
            70.0,
            new BoundingBox(new Vector(20.0, 1.057), new Vector(1.5, 1.5))
        );
        Body terrain = new Body(
            Float.POSITIVE_INFINITY,
            new BoundingBox(new Vector(19.0, 1.0), new Vector(2.0, 2.0))
        );

        assertThat(player.intersects(terrain)).isTrue();
        assertThat(terrain.intersects(player)).isTrue();
    }

}
