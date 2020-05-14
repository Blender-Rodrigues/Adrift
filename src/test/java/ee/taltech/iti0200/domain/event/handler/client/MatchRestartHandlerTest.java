package ee.taltech.iti0200.domain.event.handler.client;

import ee.taltech.iti0200.application.RestartGame;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.event.GameWon;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;

class MatchRestartHandlerTest {

    @Test
    void handleThrowsRestart() {
        GameWon event = mock(GameWon.class);
        MatchRestartHandler handler = new MatchRestartHandler(mock(World.class));

        Throwable throwable = catchThrowable(() -> handler.handle(event));

        assertThat(throwable)
            .isInstanceOf(RestartGame.class)
            .hasMessageStartingWith("Game won by")
            .hasNoCause();
    }

}
