package ee.taltech.iti0200.domain.event.handler.server;

import ee.taltech.iti0200.domain.Score;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.UpdateScore;
import ee.taltech.iti0200.domain.event.entity.DealDamage;
import ee.taltech.iti0200.domain.event.entity.DropLoot;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;
import ee.taltech.iti0200.domain.event.entity.RespawnPlayer;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServerEntityDamageHandlerTest {

    private ArgumentCaptor<Event> captor;

    private World world;
    private Projectile bullet;
    private EventBus eventBus;
    private Player killer;
    private Player victim;

    private DealDamage event;
    private Score score;
    private ServerDamageHandler handler;

    @BeforeEach
    void setUp() {
        captor = ArgumentCaptor.forClass(Event.class);

        world = mock(World.class);
        bullet = mock(Projectile.class);
        eventBus = mock(EventBus.class);
        killer = mock(Player.class);
        victim = mock(Player.class, RETURNS_DEEP_STUBS);

        event = new DealDamage(bullet, victim, null);
        score = new Score();
        handler = new ServerDamageHandler(world, score, eventBus);
    }

    @Test
    void shotFromPlayerThatKillsPlayerAddsToScore() {
        UUID victimId = UUID.randomUUID();

        when(world.getEntity(victimId)).thenReturn(victim);
        when(victim.getId()).thenReturn(victimId);
        when(bullet.getOwner()).thenReturn(killer);
        when(victim.getHealth()).thenReturn(0);

        handler.handle(event);

        assertThat(score.getPlayerScores().size()).isEqualTo(2);
        assertThat(score.getKills(killer)).isEqualTo(1);
        assertThat(score.getKills(victim)).isEqualTo(0);
        assertThat(score.getDeaths(killer)).isEqualTo(0);
        assertThat(score.getDeaths(victim)).isEqualTo(1);

        verify(eventBus, times(3)).dispatch(captor.capture());
        List<Event> events = captor.getAllValues();

        assertThat(events.get(0)).isInstanceOf(DropLoot.class);
        assertThat(events.get(1)).isInstanceOf(RemoveEntity.class);
        assertThat(events.get(2)).isInstanceOf(UpdateScore.class);
    }

    @Test
    void fatalRespawnsPlayerIfLivesLeft() {
        Vector spawn = new Vector(1, 3);
        UUID victimId = UUID.randomUUID();

        when(world.getEntity(victimId)).thenReturn(victim);
        when(world.nextSpawnPoint()).thenReturn(spawn);
        when(victim.getId()).thenReturn(victimId);
        when(bullet.getOwner()).thenReturn(killer);
        when(victim.getHealth()).thenReturn(0);
        when(victim.getLives()).thenReturn(2);

        handler.handle(event);

        assertThat(score.getPlayerScores().size()).isEqualTo(2);
        assertThat(score.getKills(killer)).isEqualTo(1);
        assertThat(score.getKills(victim)).isEqualTo(0);
        assertThat(score.getDeaths(killer)).isEqualTo(0);
        assertThat(score.getDeaths(victim)).isEqualTo(1);

        verify(victim).setPosition(spawn);
        verify(eventBus, times(3)).dispatch(captor.capture());
        List<Event> events = captor.getAllValues();

        assertThat(events.get(0)).isInstanceOf(DropLoot.class);
        assertThat(events.get(1)).isInstanceOf(RespawnPlayer.class);
        assertThat(events.get(2)).isInstanceOf(UpdateScore.class);

        RespawnPlayer respawn = (RespawnPlayer) events.get(1);
        assertThat(respawn.getId()).isEqualTo(victimId);
        assertThat(respawn.getPosition()).isEqualTo(spawn);
        assertThat(respawn.getLives()).isEqualTo(1);
    }

}
