package ee.taltech.iti0200.network;

import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.GameWon;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PacketObjectOutputStreamTest {

    @Test
    void writeObjectOverridePassesObjectPacketToSocket() throws IOException {
        // given
        ArgumentCaptor<DatagramPacket> captor = ArgumentCaptor.forClass(DatagramPacket.class);

        DatagramSocket socket = mock(DatagramSocket.class);
        InetAddress address = InetAddress.getLocalHost();

        Message message = new GameWon(mock(Player.class, RETURNS_DEEP_STUBS), Receiver.EVERYONE);

        PacketObjectOutputStream outputStream = new PacketObjectOutputStream(socket, address, 6666);

        // when
        outputStream.writeObject(message);

        // then
        verify(socket).send(captor.capture());

        DatagramPacket packet = captor.getValue();

        assertThat(packet.getAddress()).isSameAs(address);
        assertThat(packet.getPort()).isEqualTo(6666);
        assertThat(packet.getLength()).isEqualTo(431);
    }

}
