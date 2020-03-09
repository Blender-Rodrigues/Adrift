package ee.taltech.iti0200.network.server;

import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.message.Message;
import org.apache.logging.log4j.core.net.Protocol;

import java.util.LinkedList;
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
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public void writeOutbox(LinkedList<Message> data) {
        if (data.isEmpty()) {
            return;
        }

        List<Message> tcpMessages = data.stream()
            .filter(message -> message.getChannel().equals(Protocol.TCP))
            .collect(Collectors.toList());

        List<Message> udpMessages = data.stream()
            .filter(message -> message.getChannel().equals(Protocol.UDP))
            .collect(Collectors.toList());

        for (ConnectionToClient client: clients) {
            Thread.yield();

            if (!client.isOpen() || !client.isFinalized()) {
                return;
            }

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

}
