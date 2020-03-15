package ee.taltech.iti0200.application;

import ee.taltech.iti0200.ai.Intelligence;
import ee.taltech.iti0200.graphics.Graphics;
import ee.taltech.iti0200.input.Input;
import ee.taltech.iti0200.network.client.ClientNetwork;
import ee.taltech.iti0200.network.server.ServerNetwork;
import ee.taltech.iti0200.physics.Physics;

import java.util.HashMap;
import java.util.Map;

public interface Component {

    Map<Class<? extends Component>, Integer> priorities = new HashMap<Class<? extends Component>, Integer>() {{
        put(ClientNetwork.class, 1);
        put(ServerNetwork.class, 1);
        put(Physics.class, 2);
        put(Intelligence.class, 2);
        put(Graphics.class, 3);
        put(Input.class, 4);
    }};

    default void initialize() throws Exception {

    }

    void update(long tick);

    default void terminate() {

    }

}
