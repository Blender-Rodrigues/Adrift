package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    void makeNewEntity() {
        Entity entity = new Entity(
            new Body(
                1.0,
                new Vector(2.0, 2.0),
                new Vector(0.0, 0.0),
                true,
                true
            ),
            true
        );
        assertEquals(0.0, entity.getBoundingBox().getCentre().getX());
        assertEquals(0.0, entity.getBoundingBox().getCentre().getY());
    }

}