package ee.taltech.iti0200;

import ee.taltech.iti0200.domain.Bot;
import ee.taltech.iti0200.domain.Player;
import ee.taltech.iti0200.domain.Terrain;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.graphics.Graphics;
import ee.taltech.iti0200.input.Input;
import ee.taltech.iti0200.network.ClientNetwork;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.network.ServerNetwork;
import ee.taltech.iti0200.physics.Physics;

import javax.vecmath.Vector2d;

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
        timer = new Timer(20F);

        initialize();
        loop();
        terminate();
    }

    private void initialize() {
        timer.initialize();
        graphics.initialize();
        network.initialize();
        initializeBasicWorld();
    }

    private void initializeBasicWorld() {
        world.addBody(new Player(new Vector2d(20.0, 4.0)), true);
        world.addBody(new Bot(new Vector2d(10.0, 4.0)), true);
        world.addBody(new Bot(new Vector2d(30.0, 4.0)), true);
        for (int i = 0; i < 20; i++) {
            world.addBody(new Terrain(new Vector2d(i * 2.0 + 1.0, 1.0)), false);
        }
        world.addBody(new Terrain(new Vector2d(1.0, 3.0)), false);
        world.addBody(new Terrain(new Vector2d(39.0, 3.0)), false);
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
        World world = new World(0.0, 40.0, 0.0, 40.0, 10);
        boolean isServer = args.length >= 1 && args[0].equalsIgnoreCase("server");

        new Game().run(world, isServer ? new ServerNetwork(world) : new ClientNetwork(world));
    }

}
