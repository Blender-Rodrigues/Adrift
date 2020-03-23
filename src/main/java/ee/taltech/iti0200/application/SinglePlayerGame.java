package ee.taltech.iti0200.application;

import ee.taltech.iti0200.ai.Intelligence;
import ee.taltech.iti0200.domain.event.SinglePlayerEventBus;
import ee.taltech.iti0200.domain.event.entity.GunShot;
import ee.taltech.iti0200.domain.event.handler.GunShotHandler;
import ee.taltech.iti0200.physics.ServerPhysics;

class SinglePlayerGame extends ClientGame {

    public SinglePlayerGame() {
        super(null, null);
        eventBus = new SinglePlayerEventBus();
    }

    @Override
    protected void initialize() {
        world.initialize();
        eventBus.subscribe(GunShot.class, new GunShotHandler(world));
        components.add(new ServerPhysics(world));
        components.add(new Intelligence(world));
        world.addEntity(player);
    }

    @Override
    protected void loop(long tick) {
        eventBus.propagateAll();
    }

}
