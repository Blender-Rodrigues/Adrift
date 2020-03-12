package ee.taltech.iti0200.input;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Arrays.asList;

public class KeyEvent {

    public final int key;
    public final Runnable event;
    public final Set<Integer> actions;

    public KeyEvent(int key, Runnable event, Integer... actions) {
        this.key = key;
        this.event = event;
        this.actions = Collections.unmodifiableSet(new HashSet<>(asList(actions)));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof KeyEvent) {
            return key == ((KeyEvent) other).key;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

}
