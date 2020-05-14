package ee.taltech.iti0200.network;

import ee.taltech.iti0200.network.message.Message;
import org.apache.logging.log4j.core.net.Protocol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListenerTest {

    private Map<Class<? extends Message>, Consumer<Message>> handlers;
    private ObjectInputStream input;
    private Connection connection;
    private Messenger messenger;
    private Listener listener;

    @BeforeEach
    void setUp() {
        handlers = new HashMap<>();
        input = mock(ObjectInputStream.class);
        connection = mock(Connection.class);
        messenger = mock(Messenger.class);

        listener = new Listener("Test", input, messenger, connection, handlers);
    }

    @Test
    void runTerminatesWhenConnectionIsNotOpen() throws IOException, ClassNotFoundException {
        when(messenger.isAlive()).thenReturn(true);
        when(connection.isOpen()).thenReturn(false);

        listener.run();

        verify(messenger).isAlive();
        verify(connection).isOpen();
        verify(input, never()).readObject();
    }

    @Test
    void runTerminatesWhenMessengerIsNotAlive() throws IOException, ClassNotFoundException {
        when(messenger.isAlive()).thenReturn(false);

        listener.run();

        verify(messenger).isAlive();
        verify(connection, never()).isOpen();
        verify(input, never()).readObject();
    }

    @Test
    void runTerminatesWhenEOFException() throws IOException, ClassNotFoundException {
        when(messenger.isAlive()).thenReturn(true);
        when(connection.isOpen()).thenReturn(true);

        doThrow(new EOFException("ended")).when(input).readObject();

        listener.run();

        verify(connection).close();
        verify(messenger, never()).writeInbox(any());
    }

    @Test
    void runTerminatesAfterMultipleRetriesOnException() throws IOException, ClassNotFoundException {
        when(messenger.isAlive()).thenReturn(true);
        when(connection.isOpen()).thenReturn(true, true, true, true, true, true, true, true, true, true, true, true, false);

        doThrow(
            new IOException("io"),
            new IOException("io"),
            new ClassNotFoundException("no class"),
            new ClassNotFoundException("no class"),
            new IOException("io")
        ).when(input).readObject();

        listener.run();

        verify(connection).close();
        verify(connection, times(10)).isOpen();
        verify(input, times(5)).readObject();
        verify(messenger, never()).writeInbox(any());
    }

    @Test
    void runPassesToHandlerWhenTypeRegistered() throws IOException, ClassNotFoundException {
        Message response = mock(Message.class);
        Consumer<Message> handler = mock(Consumer.class);

        handlers.put(response.getClass(), handler);

        when(messenger.isAlive()).thenReturn(true, false);
        when(connection.isOpen()).thenReturn(true);
        when(response.getChannel()).thenReturn(Protocol.TCP);
        when(input.readObject()).thenReturn(response);

        listener.run();

        verify(handler).accept(response);
        verify(messenger, never()).writeInbox(any());
    }

    @Test
    void runPassesToInboxWhenTypeNotRegistered() throws IOException, ClassNotFoundException {
        Message response = mock(Message.class);
        Consumer<Message> handler = mock(Consumer.class);

        handlers.put(Message.class, handler);

        when(messenger.isAlive()).thenReturn(true, false);
        when(connection.isOpen()).thenReturn(true);
        when(response.getChannel()).thenReturn(Protocol.TCP);
        when(input.readObject()).thenReturn(response);

        listener.run();

        verify(handler, never()).accept(response);
        verify(messenger).writeInbox(response);
    }

}
