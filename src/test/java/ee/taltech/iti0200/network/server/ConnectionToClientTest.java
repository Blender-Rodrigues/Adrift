package ee.taltech.iti0200.network.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ConnectionToClientTest {

    @Test
    void closeDelegatesToClosables() throws IOException {
        Socket tcpSocket = mock(Socket.class);
        ObjectInputStream tcpInput = mock(ObjectInputStream.class);
        ObjectOutputStream tcpOutput = mock(ObjectOutputStream.class);
        DatagramSocket udpSocket = mock(DatagramSocket.class);
        ObjectInputStream udpInput = mock(ObjectInputStream.class);

        doThrow(new IOException("Closing failed")).when(udpInput).close();

        new ConnectionToClient(InetAddress.getLocalHost(), 6666)
            .setTcpSocket(tcpSocket)
            .setTcpInput(tcpInput)
            .setTcpOutput(tcpOutput)
            .setUdpSocket(udpSocket)
            .setUdpInput(udpInput)
            .setUdpOutput(null)
            .close();

        verify(tcpSocket).close();
        verify(tcpInput).close();
        verify(tcpOutput).close();
        verify(udpSocket).close();
        verify(udpInput).close();
    }

}
