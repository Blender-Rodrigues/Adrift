package ee.taltech.iti0200.network.server;

import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.message.Message;
import org.apache.logging.log4j.core.net.Protocol;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class GroupMessenger extends Messenger {

    private final Set<ConnectionToClient> clients;

    public GroupMessenger(Set<ConnectionToClient> clients, ConcurrentLinkedQueue<Message> inbox, AtomicBoolean alive) {
        super(inbox, null, alive);
        this.clients = clients;
    }

    @Override
    public Message readOutbox() {
        throw new UnsupportedOperationException("Outbox of GroupMessenger should not be read directly");
    }

    @Override
    public void writeOutbox(List<Message> messages) {
        if (messages.isEmpty()) {
            return;
        }

        clients.forEach(client -> sendToClient(
            messages.stream()
                .filter(message -> message.getReceiver().matches(client.getId()))
                .collect(Collectors.toList()),
            client
        ));
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    private void sendToClient(List<Message> messages, ConnectionToClient client) {
        Thread.yield();

        if (!client.isOpen() || !client.isFinalized()) {
            return;
        }

        List<Message> tcpMessages = messages.stream()
            .filter(message -> message.getChannel().equals(Protocol.TCP))
            .collect(Collectors.toList());

        List<Message> udpMessages = messages.stream()
            .filter(message -> message.getChannel().equals(Protocol.UDP))
            .collect(Collectors.toList());

        if (!tcpMessages.isEmpty()) {
            final ConcurrentLinkedQueue<Message> outbox = client.getTcpOutbox();
            synchronized(outbox) {
                outbox.addAll(tcpMessages);
            }
        }

        if (!udpMessages.isEmpty()) {
            final ConcurrentLinkedQueue<Message> outbox = client.getUdpOutbox();
            synchronized(outbox) {
                outbox.addAll(udpMessages);
            }
        }
    }

}
