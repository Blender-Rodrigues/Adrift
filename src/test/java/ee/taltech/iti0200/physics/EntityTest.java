package ee.taltech.iti0200.physics;

import ee.taltech.iti0200.domain.Entity;
import org.junit.jupiter.api.Test;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    @Test
    void entityGrowsToMassAndSizeOfComponentsOnCreation() {
        List<Body> components = asList(new Body(1, 3, 5, 7, 9), new Body(10, 20, 30, 40, 50));

        Entity entity = new Entity(components);

        assertThat(entity.mass).isEqualTo(11);
        assertThat(entity.xMin).isEqualTo(3);
        assertThat(entity.xMax).isEqualTo(30);
        assertThat(entity.yMin).isEqualTo(7);
        assertThat(entity.yMax).isEqualTo(50);
    }

}
