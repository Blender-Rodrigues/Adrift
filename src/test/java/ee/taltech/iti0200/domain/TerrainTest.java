package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.domain.entity.Terrain;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TerrainTest {

    @Test
    void makeNewTerrain() {
        Terrain terrain = new Terrain(new Vector(0.0, 0.0));
        assertEquals(1.0, terrain.getBoundingBox().getSize().getX());
        assertEquals(1.0, terrain.getBoundingBox().getSize().getY());
    }

}
