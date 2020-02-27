package ee.taltech.iti0200.application;

import ee.taltech.iti0200.ai.Intelligence;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.network.ServerNetwork;

class ServerGame extends Game {

    private Network network;
    private Integer tcpPort;

    public ServerGame(Integer tcpPort) {
        this.tcpPort = tcpPort;
    }

    @Override
    protected void initialize() {
        network = new ServerNetwork(world);
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
