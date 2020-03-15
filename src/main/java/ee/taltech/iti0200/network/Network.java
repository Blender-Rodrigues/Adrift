package ee.taltech.iti0200.network;

import ee.taltech.iti0200.application.Component;
import ee.taltech.iti0200.domain.World;

abstract public class Network implements Component {

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
