package ee.taltech.iti0200.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FallTest {

    @Test
    void testToString() {
        Fall fall = new Fall(11);

        assertThat(fall.getDamage()).isEqualTo(11);
        assertThat(fall.toString()).isEqualTo("Fall{11}");
    }

}
