package ee.taltech.iti0200;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.graphics.Graphics;
import ee.taltech.iti0200.input.Input;
import ee.taltech.iti0200.network.ClientNetwork;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.network.ServerNetwork;
import ee.taltech.iti0200.physics.Physics;

import java.io.IOException;

public class Game {

    private World world;
    private Graphics graphics;
    private Physics physics;
    private Network network;
    private Input input;
    private Timer timer;
    private long tick = 0;

    private void run(World world, Network network) {
        this.world = world;
        graphics = new Graphics(world);
        physics = new Physics(world);

        this.network = network;
        input = new Input();
        timer = new Timer(10F);

        initialize();
        loop();
        terminate();
    }

    private void initialize() {
        timer.initialize();
        graphics.initialize();
        try {
            network.initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loop() {
        while (isGameRunning()) {
            network.update(tick);
            physics.step(tick);
            graphics.render(tick);
            input.update(tick);
            network.propagate(tick);

            tick = timer.sleep();
        }
    }

    private boolean isGameRunning() {
        return graphics.isWindowOpen();
    }

    private void terminate() {
        graphics.terminate();
        network.terminate();
    }

    public static void main(String[] args) {
        World world = new World(0, 0, 10000, 10000, 10);
        boolean isServer = args.length >= 1 && args[0].equalsIgnoreCase("server");

        try {
            new Game().run(world, isServer ? new ServerNetwork(world) : new ClientNetwork(world));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
