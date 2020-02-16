package ee.taltech.iti0200.physics;

import ee.taltech.iti0200.domain.Entity;
import org.junit.jupiter.api.Test;

import javax.vecmath.Vector2d;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    @Test
    void entityGrowsToMassAndSizeOfComponentsOnCreation() {
        List<Body> components = asList(
                new Body(1, new Vector2d(3, 5), new Vector2d(7, 9), true),
                new Body(10, new Vector2d(20, 30), new Vector2d(40, 50), true)
        );

        Entity entity = new Entity(components, true);

        assertThat(entity.mass).isEqualTo(11);
        assertThat(entity.min.getX()).isEqualTo(3);
        assertThat(entity.max.getX()).isEqualTo(40);
        assertThat(entity.min.getY()).isEqualTo(5);
        assertThat(entity.max.getY()).isEqualTo(50);
    }

}
