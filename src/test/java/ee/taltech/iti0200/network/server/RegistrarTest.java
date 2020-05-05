package ee.taltech.iti0200.network.server;

import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.TcpRegistrationRequest;
import ee.taltech.iti0200.network.message.TcpRegistrationResponse;
import ee.taltech.iti0200.network.message.UdpRegistrationRequest;
import ee.taltech.iti0200.network.message.UdpRegistrationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegistrarTest {

    private ServerSocket serverSocket;
    private HashSet<ConnectionToClient> clients;
    private AtomicBoolean alive;
    private ServerThreadFactory factory;
    private Registrar registrar;

    @BeforeEach
    void setUp() {
        serverSocket = mock(ServerSocket.class);
        clients = new HashSet<>();
        alive = mock(AtomicBoolean.class);
        factory = mock(ServerThreadFactory.class);

        registrar = new Registrar(serverSocket, clients, alive, factory);
    }

    @Test
    void runSkipsIfNotAlive() throws IOException {
        when(alive.get()).thenReturn(false);

        registrar.run();

        verify(serverSocket, never()).accept();
    }

    @Test
    void runIgnoresAlreadyRegisteredClients() throws IOException {
        InetAddress address = InetAddress.getLocalHost();
        clients.add(new ConnectionToClient(address, 6666));

        Socket socket = mock(Socket.class);

        when(alive.get()).thenReturn(true, false);
        when(serverSocket.accept()).thenReturn(socket);
        when(socket.getInetAddress()).thenReturn(address);
        when(socket.getPort()).thenReturn(6666);

        registrar.run();

        verify(factory, never()).getBuilderFor(any(), any());
        verify(socket).getInetAddress();
        verify(socket).getPort();
    }

    @Test
    void runHandlesRegistration() throws IOException {
        // given
        ArgumentCaptor<Map<Class<? extends Message>, Consumer<Message>>> captor = ArgumentCaptor.forClass(Map.class);
        UUID clientId = UUID.randomUUID();
        TcpRegistrationRequest tcpRegistration = new TcpRegistrationRequest(clientId, 9999);
        UdpRegistrationRequest udpRegistration = new UdpRegistrationRequest();
        InetAddress address = InetAddress.getLocalHost();

        ConnectionBuilder builder = mock(ConnectionBuilder.class);
        Socket tcpSocket = mock(Socket.class);
        ObjectInputStream tcpInput = mock(ObjectInputStream.class);
        ObjectOutputStream tcpOutput = mock(ObjectOutputStream.class);
        DatagramSocket udpSocket = mock(DatagramSocket.class);
        ObjectInputStream udpInput = mock(ObjectInputStream.class);
        ObjectOutputStream udpOutput = mock(ObjectOutputStream.class);

        when(serverSocket.accept()).thenReturn(tcpSocket);
        when(factory.getBuilderFor(eq(tcpSocket), any())).thenReturn(builder);
        when(tcpSocket.getInetAddress()).thenReturn(address);
        when(tcpSocket.getPort()).thenReturn(6666);
        when(alive.get()).thenReturn(true, false);
        when(udpSocket.getLocalPort()).thenReturn(9998);

        when(builder.getTcpSocket()).thenReturn(tcpSocket);
        when(builder.createTcpInput()).thenReturn(tcpInput);
        when(builder.createTcpOutput()).thenReturn(tcpOutput);
        when(builder.createUdpSocket()).thenReturn(udpSocket);
        when(builder.createUdpInput()).thenReturn(udpInput);
        when(builder.createUdpOutput(9999)).thenReturn(udpOutput);

        // when
        registrar.run();

        verify(builder).createTcpThreads(captor.capture());
        captor.getValue().get(TcpRegistrationRequest.class).accept(tcpRegistration);
        verify(builder).createUdpThreads(captor.capture());
        captor.getValue().get(UdpRegistrationRequest.class).accept(udpRegistration);

        // then
        verify(tcpOutput).writeObject(any(TcpRegistrationResponse.class));
        verify(udpOutput).writeObject(any(UdpRegistrationResponse.class));

        assertThat(clients).hasSize(1);

        ConnectionToClient client = clients.iterator().next();
        assertThat(client.getId()).isEqualTo(clientId);
        assertThat(client.getAddress()).isEqualTo(address);
        assertThat(client.getTcpOutput()).isEqualTo(tcpOutput);
        assertThat(client.getUdpInput()).isEqualTo(udpInput);
        assertThat(client.getUdpOutput()).isEqualTo(udpOutput);
        assertThat(client.isFinalized()).isTrue();
    }

}
