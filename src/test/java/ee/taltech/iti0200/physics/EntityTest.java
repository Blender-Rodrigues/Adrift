package ee.taltech.iti0200.physics;

import ee.taltech.iti0200.domain.Entity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    @Test
    void entityGrowsToMassAndSizeOfComponentsOnCreation() {
        List<Body> components = asList(
            new Body(1, new Vector(3, 5), new Vector(7, 9), true),
            new Body(10, new Vector(20, 30), new Vector(40, 50), true)
        );

        Entity entity = new Entity(components, true);

        assertThat(entity.mass).isEqualTo(11);
        assertThat(entity.getBoundingBox().getMinX()).isEqualTo(3);
        assertThat(entity.getBoundingBox().getMaxX()).isEqualTo(40);
        assertThat(entity.getBoundingBox().getMinY()).isEqualTo(5);
        assertThat(entity.getBoundingBox().getMaxY()).isEqualTo(50);
    }

}
