package ee.taltech.iti0200.network.server;

import ee.taltech.iti0200.network.message.Message;
import org.apache.logging.log4j.core.net.Protocol;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GroupMessengerTest {

    @Test
    void readOutbox() {
        GroupMessenger messenger = new GroupMessenger(null, null, null);

        Throwable throwable = catchThrowable(messenger::readOutbox);

        assertThat(throwable)
            .isInstanceOf(UnsupportedOperationException.class)
            .hasMessageStartingWith("Outbox of GroupMessenger should not be read directly")
            .hasNoCause();

    }

    @Test
    void writeOutboxAddsMessagesToSpecificClientOutboxes() {
        // given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();

        ConcurrentLinkedQueue<Message> tcpOutbox1 = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Message> tcpOutbox2 = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Message> tcpOutbox3 = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Message> udpOutbox2 = new ConcurrentLinkedQueue<>();

        ConnectionToClient connection1 = mock(ConnectionToClient.class, RETURNS_DEEP_STUBS);
        ConnectionToClient connection2 = mock(ConnectionToClient.class, RETURNS_DEEP_STUBS);
        ConnectionToClient connection3 = mock(ConnectionToClient.class, RETURNS_DEEP_STUBS);

        when(connection1.getId()).thenReturn(id1);
        when(connection2.getId()).thenReturn(id2);
        when(connection3.getId()).thenReturn(id3);

        when(connection1.isOpen()).thenReturn(true);
        when(connection1.isFinalized()).thenReturn(true);
        when(connection2.isOpen()).thenReturn(true);
        when(connection2.isFinalized()).thenReturn(true);
        when(connection3.isOpen()).thenReturn(true);
        when(connection3.isFinalized()).thenReturn(true);

        when(connection1.getTcpOutbox()).thenReturn(tcpOutbox1);
        when(connection2.getTcpOutbox()).thenReturn(tcpOutbox2);
        when(connection2.getUdpOutbox()).thenReturn(udpOutbox2);
        when(connection3.getTcpOutbox()).thenReturn(tcpOutbox3);

        Set<ConnectionToClient> clients = new HashSet<>();
        clients.add(connection1);
        clients.add(connection2);
        clients.add(connection3);

        Message messageTcp = mock(Message.class, RETURNS_DEEP_STUBS);
        Message messageUdp = mock(Message.class, RETURNS_DEEP_STUBS);
        Message messageTcpAll = mock(Message.class, RETURNS_DEEP_STUBS);
        Message messageNone = mock(Message.class, RETURNS_DEEP_STUBS);

        when(messageTcp.getReceiver().matches(id1)).thenReturn(true);
        when(messageUdp.getReceiver().matches(id2)).thenReturn(true);
        when(messageTcpAll.getReceiver().matches(id1)).thenReturn(true);
        when(messageTcpAll.getReceiver().matches(id2)).thenReturn(true);
        when(messageTcpAll.getReceiver().matches(id3)).thenReturn(true);
        when(messageNone.getReceiver().matches(any(UUID.class))).thenReturn(false);

        when(messageTcp.getChannel()).thenReturn(Protocol.TCP);
        when(messageUdp.getChannel()).thenReturn(Protocol.UDP);
        when(messageTcpAll.getChannel()).thenReturn(Protocol.TCP);

        // when
        new GroupMessenger(clients, null, null).writeOutbox(asList(messageTcp, messageUdp, messageTcpAll, messageNone));

        // then
        verify(messageNone, never()).getChannel();
        verify(connection1, never()).getUdpOutbox();
        verify(connection3, never()).getUdpOutbox();

        assertThat(tcpOutbox1).containsExactlyInAnyOrder(messageTcp, messageTcpAll);
        assertThat(tcpOutbox2).containsExactly(messageTcpAll);
        assertThat(tcpOutbox3).containsExactly(messageTcpAll);
        assertThat(udpOutbox2).containsExactly(messageUdp);
    }

    @Test
    void writeOutboxIgnoresClosedAndNotFinalizedClients() {
        // given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();

        ConnectionToClient connection1 = mock(ConnectionToClient.class, RETURNS_DEEP_STUBS);
        ConnectionToClient connection2 = mock(ConnectionToClient.class, RETURNS_DEEP_STUBS);
        ConnectionToClient connection3 = mock(ConnectionToClient.class, RETURNS_DEEP_STUBS);

        when(connection1.getId()).thenReturn(id1);
        when(connection2.getId()).thenReturn(id2);
        when(connection3.getId()).thenReturn(id3);

        when(connection1.isOpen()).thenReturn(false);
        when(connection1.isFinalized()).thenReturn(false);
        when(connection2.isOpen()).thenReturn(false);
        when(connection2.isFinalized()).thenReturn(true);
        when(connection3.isOpen()).thenReturn(true);
        when(connection3.isFinalized()).thenReturn(false);

        Set<ConnectionToClient> clients = new HashSet<>();
        clients.add(connection1);
        clients.add(connection2);
        clients.add(connection3);

        Message messageTcp = mock(Message.class, RETURNS_DEEP_STUBS);
        when(messageTcp.getReceiver().matches(any(UUID.class))).thenReturn(true);
        when(messageTcp.getChannel()).thenReturn(Protocol.TCP);

        // when
        new GroupMessenger(clients, null, null).writeOutbox(asList(messageTcp));

        // then
        verify(connection1, never()).getTcpOutbox();
        verify(connection2, never()).getTcpOutbox();
        verify(connection3, never()).getTcpOutbox();
    }

}
