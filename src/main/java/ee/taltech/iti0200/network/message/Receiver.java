package ee.taltech.iti0200.network.message;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static ee.taltech.iti0200.application.ServerGame.SERVER_ID;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class Receiver implements Serializable {

    public static final Receiver SERVER = new Receiver(SERVER_ID);
    public static final Receiver ALL_CLIENTS = new Receiver(SERVER_ID).exclude();
    public static final Receiver EVERYONE = new Receiver(emptyList()).exclude();

    private boolean exclude = false;
    private List<UUID> target;

    public Receiver(UUID target) {
        this(singletonList(target));
    }

    public Receiver(List<UUID> target) {
        this.target = target;
    }

    public Receiver exclude() {
        this.exclude = true;
        return this;
    }

    public boolean matches(UUID id) {
        return target.contains(id) != exclude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Receiver other = (Receiver) o;
        return exclude == other.exclude && target.equals(other.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exclude, target);
    }

}
