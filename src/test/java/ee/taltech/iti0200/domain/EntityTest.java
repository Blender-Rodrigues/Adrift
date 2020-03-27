package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityTest {

    @Test
    void makeNewEntity() {
        Entity entity = new Entity(1.0, new BoundingBox(new Vector(0.0, 0.0), new Vector(2.0, 2.0)));
        assertEquals(0.0, entity.getBoundingBox().getCentre().getX());
        assertEquals(0.0, entity.getBoundingBox().getCentre().getY());
    }

}
