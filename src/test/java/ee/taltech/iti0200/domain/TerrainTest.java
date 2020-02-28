package ee.taltech.iti0200.domain;

import org.junit.jupiter.api.Test;

import javax.vecmath.Vector2d;

import static org.junit.jupiter.api.Assertions.*;

class TerrainTest {

    @Test
    void makeNewTerrain() {
        Terrain terrain = new Terrain(new Vector2d(0.0, 0.0));
        assertEquals(1.0, terrain.getBoundingBox().getSize().getX());
        assertEquals(1.0, terrain.getBoundingBox().getSize().getY());
    }
}