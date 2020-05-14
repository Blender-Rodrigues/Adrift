package ee.taltech.iti0200.network;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * See: https://www.javaworld.com/article/2077539/java-tip-40--object-transport-via-datagram-packets.html
 */
public class PacketObjectOutputStream extends ObjectOutputStream {

    private final DatagramSocket socket;
    private final InetAddress address;
    private final int port;

    public PacketObjectOutputStream(
        DatagramSocket socket,
        InetAddress address,
        int port
    ) throws IOException, SecurityException {
        this.socket = socket;
        this.address = address;
        this.port = port;
    }

    @Override
    protected void writeObjectOverride(Object message) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);  // TODO: do we need to handle buffer size here?

        try (ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(byteStream))) {
            output.flush();
            output.writeObject(message);
            output.flush();

            byte[] sendBuf = byteStream.toByteArray();
            DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length, address, port);

            socket.send(packet);
        }
    }

    @Override
    public void flush() throws IOException {
        // Not using underlying implementation to flush
    }

    @Override
    public void close() throws IOException {
        // Socket is closed separately
    }

}
