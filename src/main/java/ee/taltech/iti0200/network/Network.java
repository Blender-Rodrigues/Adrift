package ee.taltech.iti0200.network;

import ee.taltech.iti0200.application.Component;
import ee.taltech.iti0200.domain.World;

abstract public class Network implements Component {

    public static final int UPSTREAM_PORT = 8880;
    public static final int DOWNSTREAM_PORT = 8881;
    public static final String HOST = "localhost";

    protected World world;

    protected final Messenger messenger = new Messenger();

    public Network(World world) {
        this.world = world;
    }

    abstract public void propagate(long tick);

    @Override
    public void terminate() {
        messenger.terminate();
    }

}
