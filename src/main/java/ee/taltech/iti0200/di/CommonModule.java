package ee.taltech.iti0200.di;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import ee.taltech.iti0200.application.Timer;
import ee.taltech.iti0200.di.annotations.LocalPlayer;
import ee.taltech.iti0200.domain.Layout;
import ee.taltech.iti0200.domain.Score;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.physics.Vector;

import java.util.UUID;

public class CommonModule extends AbstractModule {

    protected static final String LAYOUT_NAME = "station_small.jpg";

    protected World world = new World(0.0, 600, 0.0, 600.0, 0.05);
    protected UUID id = UUID.randomUUID();
    protected Score score = new Score();
    protected Player player = (Player) new Player(new Vector(0, 0), world).setId(id);

    protected void configure() {
        bind(Layout.class).toInstance(new Layout(LAYOUT_NAME));
        bind(World.class).toInstance(world);
        bind(Timer.class).toInstance(new Timer(60));
        bind(Score.class).toInstance(score);
        bind(Key.get(Player.class, LocalPlayer.class)).toInstance(player);
    }

}
