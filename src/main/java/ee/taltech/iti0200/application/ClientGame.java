package ee.taltech.iti0200.application;

import ee.taltech.iti0200.domain.Player;
import ee.taltech.iti0200.graphics.Graphics;
import ee.taltech.iti0200.input.Input;
import ee.taltech.iti0200.network.client.ClientNetwork;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.physics.Vector;

import java.net.UnknownHostException;

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

        player = new Player(new Vector(20.0, 40.0));
        graphics = new Graphics(world, player);
        input = new Input(graphics.getWindow(), player, graphics.getCamera(), world);

        components.add(graphics);
        components.add(input);
    }

    @Override
    protected void loop(long tick) {
        network.propagate(tick);
    }

    @Override
    protected void initialize() throws UnknownHostException {
        network = new ClientNetwork(world, host, tcpPort);
        components.add(network);
        world.addBody(player, true);
    }

    @Override
    protected boolean isGameRunning() {
        return graphics.isWindowOpen();
    }

}
