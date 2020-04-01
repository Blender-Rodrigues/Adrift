package ee.taltech.iti0200.domain.event;

import ee.taltech.iti0200.network.message.Receiver;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class ReceiverTest {

    @Test
    void matches() {
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();

        assertThat(new Receiver(first).matches(first)).isTrue();
        assertThat(new Receiver(first).matches(second)).isFalse();

        assertThat(new Receiver(first).exclude().matches(first)).isFalse();
        assertThat(new Receiver(first).exclude().matches(second)).isTrue();

        assertThat(new Receiver(emptyList()).exclude().matches(first)).isTrue();
        assertThat(new Receiver(emptyList()).exclude().matches(second)).isTrue();
    }

}
