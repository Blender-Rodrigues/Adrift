package ee.taltech.iti0200.domain.event.entity;

import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;

import java.util.UUID;

import static java.lang.String.format;

public class ChangeEquipment extends Event implements Message {

    private static final long serialVersionUID = 1L;

    private final UUID playerId;
    private final int slot;

    public ChangeEquipment(Player player, int slot, Receiver receiver) {
        super(receiver);
        this.playerId = player.getId();
        this.slot = slot;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public int getSlot() {
        return slot;
    }

    @Override
    public String toString() {
        return format("ChangeEquipment{%s, slot=%d}", playerId, slot);
    }

}
