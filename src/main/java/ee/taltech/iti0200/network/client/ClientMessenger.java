package ee.taltech.iti0200.network.client;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.ConcurrentInbox;
import ee.taltech.iti0200.di.annotations.ConcurrentTcpOutbox;
import ee.taltech.iti0200.di.annotations.ConcurrentUdpOutbox;
import ee.taltech.iti0200.di.annotations.ConnectionAlive;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.message.Message;
import org.apache.logging.log4j.core.net.Protocol;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ClientMessenger extends Messenger {

    private final ConcurrentLinkedQueue<Message> tcpOutbox;
    private final ConcurrentLinkedQueue<Message> udpOutbox;

    @Inject
    public ClientMessenger(
        @ConcurrentInbox ConcurrentLinkedQueue<Message> inbox,
        @ConcurrentTcpOutbox ConcurrentLinkedQueue<Message> tcpOutbox,
        @ConcurrentUdpOutbox ConcurrentLinkedQueue<Message> udpOutbox,
        @ConnectionAlive AtomicBoolean alive
    ) {
        super(inbox, null, alive);
        this.tcpOutbox = tcpOutbox;
        this.udpOutbox = udpOutbox;
    }

    public Message readOutbox() {
        throw new UnsupportedOperationException("Outbox of ClientMessenger should not be read directly");
    }

    public void writeOutbox(List<Message> messages) {
        writeOutbox(messages, tcpOutbox, Protocol.TCP);
        writeOutbox(messages, udpOutbox, Protocol.UDP);
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    private void writeOutbox(List<Message> messages, ConcurrentLinkedQueue<Message> outbox, Protocol protocol) {
        messages = messages.stream()
            .filter(message -> message.getChannel().equals(protocol))
            .collect(Collectors.toList());

        if (!messages.isEmpty()) {
            synchronized(outbox) {
                outbox.addAll(messages);
            }
        }
    }

}
