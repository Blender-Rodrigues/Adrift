package ee.taltech.iti0200.network;

import ee.taltech.iti0200.domain.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerNetwork extends Network {

    private Logger logger;

    public ServerNetwork(World world) {
        super(world);
        logger = LogManager.getLogger(ServerNetwork.class);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void update(long tick) {

    }

    @Override
    public void propagate(long tick) {

    }

}
