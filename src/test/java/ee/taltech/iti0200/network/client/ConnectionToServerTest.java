package ee.taltech.iti0200.network.client;

import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.network.message.LoadWorld;
import ee.taltech.iti0200.network.message.TcpRegistrationResponse;
import ee.taltech.iti0200.network.message.UdpRegistrationResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConnectionToServerTest {

     @Test
    void initialize() throws IOException, ClassNotFoundException {
        // given
        InetAddress address = InetAddress.getLocalHost();
        Player player = mock(Player.class);
        ConnecionBuilder factory = mock(ConnecionBuilder.class, RETURNS_DEEP_STUBS);

        Socket tcpSocket = mock(Socket.class);
        ObjectInputStream tcpInput = mock(ObjectInputStream.class);
        ObjectOutputStream tcpOutput = mock(ObjectOutputStream.class);
        DatagramSocket udpSocket = mock(DatagramSocket.class);
        ObjectInputStream udpInput = mock(ObjectInputStream.class);
        ObjectOutputStream udpOutput = mock(ObjectOutputStream.class);

        TcpRegistrationResponse tcpResponse = mock(TcpRegistrationResponse.class);
        UdpRegistrationResponse udpResponse = mock(UdpRegistrationResponse.class);
        LoadWorld worldResponse = mock(LoadWorld.class);

        when(factory.createTcpSocket()).thenReturn(tcpSocket);
        when(factory.createTcpInput()).thenReturn(tcpInput);
        when(factory.createTcpOutput()).thenReturn(tcpOutput);
        when(factory.createUdpSocket()).thenReturn(udpSocket);
        when(factory.createUdpInput()).thenReturn(udpInput);
        when(factory.createUdpOutput(9999)).thenReturn(udpOutput);

        when(factory.createTcpInput().readObject()).thenReturn(tcpResponse, worldResponse);
        when(factory.createUdpInput().readObject()).thenReturn(tcpResponse, udpResponse);
        when(tcpResponse.getUdpPort()).thenReturn(9999);

        // when
        ConnectionToServer connection = new ConnectionToServer(address, 8888, player, factory);
        connection.initialize();
        LoadWorld actual = connection.getWorldData();

        // then
        assertThat(actual).isSameAs(worldResponse);
    }

}
