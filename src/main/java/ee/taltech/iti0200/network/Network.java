package ee.taltech.iti0200.network;

import ee.taltech.iti0200.domain.World;

import java.io.IOException;

abstract public class Network {

    public static final int UPSTREAM_PORT = 8880;
    public static final int DOWNSTREAM_PORT = 8881;
    public static final String HOST = "localhost";

    protected World world;

    protected final Messenger messenger = new Messenger();

    public Network(World world) {
        this.world = world;
    }

    abstract public void initialize() throws IOException;

    abstract public void update(long tick);

    abstract public void propagate(long tick);

    public void terminate() {
        messenger.terminate();
    }

}
