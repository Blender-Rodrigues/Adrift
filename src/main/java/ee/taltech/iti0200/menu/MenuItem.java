package ee.taltech.iti0200.menu;

import java.util.HashMap;
import java.util.Map;

public class MenuItem {

    private final Map<Direction, MenuItem> neighbours = new HashMap<>();
    private final boolean writable;
    private final String label;

    private Runnable action;
    private boolean active = false;
    private String value;

    public MenuItem(String label, String value, boolean writable) {
        this.label = label;
        this.value = value;
        this.writable = writable;
    }

    public Runnable getAction() {
        return action;
    }

    public MenuItem setAction(Runnable action) {
        this.action = action;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public MenuItem setActive(boolean active) {
        this.active = active;
        return this;
    }

    public String getValue() {
        return value;
    }

    public MenuItem setValue(String value) {
        this.value = value;
        return this;
    }

    public boolean isWritable() {
        return writable;
    }

    public Map<Direction, MenuItem> getNeighbours() {
        return neighbours;
    }

    public MenuItem addNeighbour(Direction direction, MenuItem other) {
        neighbours.put(direction, other);
        return this;
    }

    public String getText(String prefix) {
        String text = label + " " + value;
        if (active) {
            return prefix + text;
        }
        return " " + text;
    }

}
