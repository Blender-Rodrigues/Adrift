package ee.taltech.iti0200.application;

import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.graphics.Graphics;
import ee.taltech.iti0200.input.Input;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.network.client.ClientNetwork;

import java.net.UnknownHostException;
import java.util.UUID;

public class ClientGame extends Game {

    public static final UUID CLIENT_ID = UUID.randomUUID();

    protected Player player;

    private Graphics graphics;
    private Input input;
    private Network network;
    private String host;
    private Integer tcpPort;

    public ClientGame(String host, Integer tcpPort) {
        super(CLIENT_ID);
        this.host = host;
        this.tcpPort = tcpPort;
        Game.isClient = true;

        player = new Player(world.nextPlayerSpawnPoint(), world);
        player.setId(CLIENT_ID);

        graphics = new Graphics(world, player);
        input = new Input(graphics.getWindow(), player, graphics.getCamera());

        components.add(graphics);
        components.add(input);
    }

    @Override
    protected void loop(long tick) {
        network.propagate(tick);
    }

    @Override
    protected void initialize() throws UnknownHostException {
        network = new ClientNetwork(world, host, tcpPort, player);
        components.add(network);
        world.addEntity(player);
    }

    @Override
    protected boolean isGameRunning() {
        return graphics.isWindowOpen();
    }

}
