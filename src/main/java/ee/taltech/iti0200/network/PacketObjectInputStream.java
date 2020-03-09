package ee.taltech.iti0200.network;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * See: https://www.javaworld.com/article/2077539/java-tip-40--object-transport-via-datagram-packets.html
 */
public class PacketObjectInputStream extends ObjectInputStream {

    private DatagramSocket socket;

    public PacketObjectInputStream(DatagramSocket socket) throws IOException, SecurityException {
        this.socket = socket;
    }

    @Override
    protected Object readObjectOverride() throws IOException, ClassNotFoundException {
        byte[] buffer = new byte[5000]; // TODO: do we need to handle buffer size here?
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        try (
            ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(buffer)))
        ) {
            return input.readObject();
        }
    }

    @Override
    public void close() throws IOException {
        // Socket is closed separately
    }

}
