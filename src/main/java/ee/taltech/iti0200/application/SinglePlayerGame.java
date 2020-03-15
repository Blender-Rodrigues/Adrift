package ee.taltech.iti0200.application;

import ee.taltech.iti0200.ai.Intelligence;

class SinglePlayerGame extends ClientGame {

    public SinglePlayerGame() {
        super(null, null);
    }

    @Override
    protected void initialize() {
        world.initialize();
        components.add(new Intelligence(world));
        world.addEntity(player);
    }

    @Override
    protected void loop(long tick) {

    }

}
