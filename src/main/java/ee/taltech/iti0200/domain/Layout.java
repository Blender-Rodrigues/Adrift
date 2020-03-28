package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.domain.entity.Terrain;
import ee.taltech.iti0200.graphics.Image;
import ee.taltech.iti0200.physics.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Layout {

    private static final String PATH = "layouts/";
    private static final int AIR = -16700000;
    private static final int SPAWN = -1;

    private final Logger logger = LogManager.getLogger(Layout.class);

    private List<Vector> terrain = new ArrayList<>();
    private List<Vector> spawnPoints = new ArrayList<>();
    private String name;
    private int width;
    private int height;

    /**
     * Air is almost black to total black pixels - to count for noise around black areas
     * Spawn points are white pixels
     */
    public Layout(String name) {
        this.name = name;
    }

    public void populateWorld(World world) throws IOException {
        initialize();

        world.setSpawnPoints(new ArrayDeque<>(spawnPoints));
        terrain.forEach(position -> world.addEntity(new Terrain(position)));

        logger.info(
            "Populated {}x{} world {} with {} terrain blocks and {} spawn points",
            height,
            width,
            name,
            terrain.size(),
            spawnPoints.size()
        );
        logger.debug("Spawn points: " + spawnPoints);
    }

    private void initialize() throws IOException {
        readTerrain(PATH + name);
        if (spawnPoints.isEmpty()) {
            throw new IllegalStateException("Layout " + name + " has no spawn points (white pixels)");
        }
        Collections.shuffle(spawnPoints);
    }

    /**
     * Height is flipped around to match image orientation.
     */
    private void readTerrain(String file) throws IOException {
        Image image = new Image(file);

        width = image.getWidth();
        height = image.getHeight();
        int[] rawPixels = image.getRawPixels();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = rawPixels[i * width + j];
                if (pixel < AIR) {
                    continue;
                }

                Vector pos = new Vector(j, height - i - 1);

                if (pixel == SPAWN) {
                    spawnPoints.add(pos);
                } else {
                    terrain.add(pos);
                }
            }
        }
    }

}
