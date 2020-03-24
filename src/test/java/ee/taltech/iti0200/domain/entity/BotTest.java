package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.ai.HealthyBrain;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

class BotTest {

    @TempDir
    File directory;

    @Test
    void serializationPreservesMotionAndHealth() throws Exception {
        File file = new File(directory, "Bot.txt");

        Vector position = new Vector(1, 3);
        World world = mock(World.class);

        Bot given = new Bot(position, world, null);
        given.setHealth(5);
        given.setXSpeed(7);
        given.setYSpeed(9);
        UUID id = given.getId();

        serialize(given, file);
        Bot actual = deserialize(file);

        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getBoundingBox().getMinX()).isEqualTo(0.25);
        assertThat(actual.getBoundingBox().getMaxY()).isEqualTo(3.75);
        assertThat(actual.health).isEqualTo(5);
        assertThat(actual.getSpeed().x).isEqualTo(7);
        assertThat(actual.getSpeed().y).isEqualTo(9);
        assertThat(actual.isMovable()).isTrue();
    }

    @Test
    void deserializedGetsABabyBrain() throws Exception {
        File file = new File(directory, "Bot.txt");

        Vector position = new Vector(1, 3);
        World world = mock(World.class);
        HealthyBrain brain = mock(HealthyBrain.class);

        Bot given = new Bot(position, world, brain);

        serialize(given, file);
        Bot actual = deserialize(file);

        actual.update(1);

        verifyZeroInteractions(brain);
    }

    private void serialize(Bot bot, File file) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);

        out.writeObject(bot);
        out.close();
        fileOut.close();
    }

    private Bot deserialize(File file) throws Exception {
        FileInputStream fileIn = new FileInputStream(file);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Bot bot = (Bot) in.readObject();
        in.close();
        fileIn.close();
        return bot;
    }

}
