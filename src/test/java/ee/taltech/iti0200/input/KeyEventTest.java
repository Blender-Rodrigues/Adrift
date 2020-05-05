package ee.taltech.iti0200.input;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({"EqualsBetweenInconvertibleTypes", "ConstantConditions", "UnnecessaryLocalVariable"})
class KeyEventTest {

    @Test
    void testEqualsSame() {
        KeyEvent a = new KeyEvent(1, null);
        KeyEvent b = a;

        assertThat(a.equals(b)).isTrue();
    }

    @Test
    void testEqualsKey() {
        KeyEvent a = new KeyEvent(1, null);
        KeyEvent b = new KeyEvent(1, null);

        assertThat(a.equals(b)).isTrue();
    }

    @Test
    void testEqualsNotKeyEvent() {
        KeyEvent a = new KeyEvent(1, null);

        assertThat(a.equals("String")).isFalse();
    }

}
