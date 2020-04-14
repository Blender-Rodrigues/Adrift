package ee.taltech.iti0200.di;

import com.google.inject.Key;
import com.google.inject.Singleton;
import ee.taltech.iti0200.application.Game;
import ee.taltech.iti0200.application.SinglePlayerGame;
import ee.taltech.iti0200.di.annotations.GameId;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.SinglePlayerEventBus;
import ee.taltech.iti0200.domain.event.common.CollisionHandler;
import ee.taltech.iti0200.domain.event.client.EntityDamageHandler;
import ee.taltech.iti0200.domain.event.server.ServerCollisionHandler;
import ee.taltech.iti0200.domain.event.server.ServerDamageHandler;
import ee.taltech.iti0200.physics.Physics;
import ee.taltech.iti0200.physics.ServerPhysics;

import java.util.UUID;

public class SinglePlayerModule extends CommonModule {

    protected void configure() {
        super.configure();
        bind(Key.get(UUID.class, GameId.class)).toInstance(id);

        bind(Game.class).to(SinglePlayerGame.class);

        bind(CollisionHandler.class).to(ServerCollisionHandler.class).in(Singleton.class);
        bind(EntityDamageHandler.class).to(ServerDamageHandler.class).in(Singleton.class);
        bind(EventBus.class).to(SinglePlayerEventBus.class).in(Singleton.class);
        bind(Physics.class).to(ServerPhysics.class).in(Singleton.class);
    }

}
