package ee.taltech.iti0200.application;

import ee.taltech.iti0200.ai.Intelligence;
import ee.taltech.iti0200.domain.Layout;
import ee.taltech.iti0200.domain.event.entity.GunShot;
import ee.taltech.iti0200.domain.event.handler.GunShotHandler;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.network.server.ServerNetwork;
import ee.taltech.iti0200.physics.ServerPhysics;

import java.util.UUID;

public class ServerGame extends Game {

    public static final UUID SERVER_ID = new UUID(0, 0);

    private Network network;
    private Integer tcpPort;

    public ServerGame(Integer tcpPort) {
        super(SERVER_ID);
        this.tcpPort = tcpPort;
    }

    @Override
    protected void initialize() throws Exception {
        new Layout(LAYOUT_NAME).populateWorld(world);
        world.initialize();
        eventBus.subscribe(GunShot.class, new GunShotHandler(world));
        components.add(new ServerPhysics(world));
        network = new ServerNetwork(world, tcpPort);
        components.add(network);
        components.add(new Intelligence(world));
    }

    @Override
    protected void loop(long tick) {
        network.propagate(tick);
    }

    @Override
    protected boolean isGameRunning() {
        return true;
    }

}
