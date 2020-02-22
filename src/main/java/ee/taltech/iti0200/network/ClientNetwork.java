package ee.taltech.iti0200.network;

import ee.taltech.iti0200.domain.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientNetwork extends Network {

    private Logger logger;

    public ClientNetwork(World world) {
        super(world);
        logger = LogManager.getLogger(ClientNetwork.class);
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
