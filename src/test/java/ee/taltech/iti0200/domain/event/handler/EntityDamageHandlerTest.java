package ee.taltech.iti0200.domain.event.handler;

import ee.taltech.iti0200.domain.Score;
import ee.taltech.iti0200.domain.ScoreData;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.DamageSource;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.DealDamage;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EntityDamageHandlerTest {

    @Test
    void shotFromPlayerThatKillsPlayerAddsToScore() {
        Player killer = mock(Player.class);
        Player victim = mock(Player.class);
        Projectile bullet = mock(Projectile.class);
        World world = mock(World.class);
        EventBus eventBus = mock(EventBus.class);
        DealDamage event = mock(DealDamage.class);
        UUID victimId = UUID.randomUUID();
        Score score = new Score();
        EntityDamageHandler handler = new EntityDamageHandler(world, score, eventBus);

        when(world.getEntity(victimId)).thenReturn(victim);
        when(victim.getId()).thenReturn(victimId);
        when(bullet.getOwner()).thenReturn(killer);
        when(victim.getHealth()).thenReturn(0);
        when(event.getSource()).thenReturn(bullet);
        when(event.getTarget()).thenReturn(victim);

        handler.handle(event);

        assertThat(score.getPlayerScores().size()).isEqualTo(2);
        assertThat(score.getKills(killer)).isEqualTo(1);
        assertThat(score.getKills(victim)).isEqualTo(0);
        assertThat(score.getDeaths(killer)).isEqualTo(0);
        assertThat(score.getDeaths(victim)).isEqualTo(1);
    }
}