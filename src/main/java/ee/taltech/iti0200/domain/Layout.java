package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.domain.entity.Terrain;
import ee.taltech.iti0200.physics.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Layout {

    private static final String PATH = "./build/resources/main/layouts/";
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
    public Layout(String name) throws IOException {
        this.name = name;
        readTerrain(PATH + name);
        if (spawnPoints.isEmpty()) {
            throw new IllegalStateException("Layout " + name + " has no spawn points (white pixels)");
        }
        Collections.shuffle(spawnPoints);
    }

    public void populateWorld(World world) {
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

    /**
     * Height is flipped around to match image orientation.
     */
    private void readTerrain(String filename) throws IOException {
        BufferedImage image = ImageIO.read(new File(filename));

        width = image.getWidth();
        height = image.getHeight();

        int[] pixelsRaw = image.getRGB(0, 0, width, height, null, 0, width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = pixelsRaw[i * width + j];
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
