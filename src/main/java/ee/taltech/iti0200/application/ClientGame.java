package ee.taltech.iti0200.application;

import ee.taltech.iti0200.domain.Player;
import ee.taltech.iti0200.graphics.Graphics;
import ee.taltech.iti0200.input.Input;
import ee.taltech.iti0200.network.client.ClientNetwork;
import ee.taltech.iti0200.network.Network;

import javax.vecmath.Vector2d;

class ClientGame extends Game {

    protected Player player;

    private Graphics graphics;
    private Input input;
    private Network network;
    private Integer tcpPort;
    private String host;

    public ClientGame(String host, Integer tcpPort) {
        this.tcpPort = tcpPort;
        this.host = host;

        graphics = new Graphics(world);
        player = new Player(new Vector2d(20.0, 4.0));
        input = new Input(graphics.getWindow(), player);

        components.add(graphics);
        components.add(input);
    }

    @Override
    protected void loop(long tick) {
        network.propagate(tick);
    }

    @Override
    protected void initialize() {
        network = new ClientNetwork(world, host, tcpPort);
        components.add(network);
        world.addBody(player, true);
    }

    @Override
    protected boolean isGameRunning() {
        return graphics.isWindowOpen();
    }

}
