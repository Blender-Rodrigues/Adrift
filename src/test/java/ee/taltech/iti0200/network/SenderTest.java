package ee.taltech.iti0200.network;

import ee.taltech.iti0200.network.message.Message;
import org.apache.logging.log4j.core.net.Protocol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectOutputStream;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SenderTest {

    private ObjectOutputStream output;
    private Messenger messenger;
    private Connection connection;
    private Sender sender;

    @BeforeEach
    void setUp() {
        output = mock(ObjectOutputStream.class);
        connection = mock(Connection.class);
        messenger = mock(Messenger.class);

        sender = new Sender("Test", output, messenger, connection);
    }

    @Test
    void runTerminatesWhenConnectionIsNotOpen() {
        when(messenger.isAlive()).thenReturn(true);
        when(connection.isOpen()).thenReturn(false);

        sender.run();

        verify(messenger).isAlive();
        verify(connection).isOpen();
        verify(messenger, never()).readOutbox();
    }

    @Test
    void runTerminatesWhenMessengerIsNotAlive() {
        when(messenger.isAlive()).thenReturn(false);

        sender.run();

        verify(messenger).isAlive();
        verify(connection, never()).isOpen();
        verify(messenger, never()).readOutbox();
    }

    @Test
    void runIgnoresIoExceptions() throws IOException {
        Message message = mock(Message.class);

        when(messenger.isAlive()).thenReturn(true, false);
        when(connection.isOpen()).thenReturn(true);
        when(messenger.readOutbox()).thenReturn(message);

        doThrow(new IOException("bad write")).when(output).writeObject(message);

        sender.run();

        verify(messenger, times(2)).isAlive();
        verify(output, never()).flush();
    }

    @Test
    void runIgnoresNullMessages() throws IOException {
        when(messenger.isAlive()).thenReturn(true, false);
        when(connection.isOpen()).thenReturn(true);

        when(messenger.readOutbox()).thenReturn(null);

        sender.run();

        verify(messenger, times(2)).isAlive();
        verify(output, never()).flush();
    }

    @Test
    void runWritesMessageToOutput() throws IOException {
        Message message = mock(Message.class);

        when(messenger.isAlive()).thenReturn(true, false);
        when(connection.isOpen()).thenReturn(true);
        when(messenger.readOutbox()).thenReturn(message);
        when(message.getChannel()).thenReturn(Protocol.TCP);

        sender.run();

        verify(messenger, times(2)).isAlive();
        verify(output).writeObject(message);
        verify(output).flush();
        verify(message).getChannel();
    }

}
