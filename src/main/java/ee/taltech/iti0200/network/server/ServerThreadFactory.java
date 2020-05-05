package ee.taltech.iti0200.network.server;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.ConcurrentInbox;
import ee.taltech.iti0200.di.annotations.ConnectionAlive;
import ee.taltech.iti0200.di.factory.ThreadFactory;
import ee.taltech.iti0200.network.message.Message;

import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerThreadFactory extends ThreadFactory {

    private final ConcurrentLinkedQueue<Message> inbox;
    private final AtomicBoolean alive;

    @Inject
    public ServerThreadFactory(
        @ConcurrentInbox ConcurrentLinkedQueue<Message> inbox,
        @ConnectionAlive AtomicBoolean alive
    ) {
        this.inbox = inbox;
        this.alive = alive;
    }

    public ConnectionBuilder getBuilderFor(Socket tcpSocket, ConnectionToClient connection) {
        return new ConnectionBuilder(tcpSocket, inbox, alive, connection);
    }

}
