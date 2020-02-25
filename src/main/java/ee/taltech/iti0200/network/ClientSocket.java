package ee.taltech.iti0200.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.UUID;

public class ClientSocket extends DatagramSocket {

    public static final String CONFIRMATION = "CONFIRMATION";

    private final InetAddress address;
    private final int port;
    private final UUID clientId;
    private final String name;

    public ClientSocket(String host, int port, UUID id, String name) throws SocketException, UnknownHostException {
        this.address = InetAddress.getByName(host);
        this.port = port;
        this.clientId = id;
        this.name = name;
    }

    public void confirmConnection(int port) throws IOException {
        String message = CONFIRMATION + " " + port + " " + name + " " + clientId;
        byte[] request = message.getBytes();

        send(request);

        String response = receive();

        System.out.println("ClientSocket: " + response);

        if (response == null || !response.startsWith(CONFIRMATION)) {
            throw new RuntimeException("Unable to get a socket confirmation from server");
        }
    }

    public void send(byte[] request) throws IOException {
        DatagramPacket requestPacket = new DatagramPacket(request, request.length, address, port);

        super.send(requestPacket);
    }

    public String receive() throws IOException {
        DatagramPacket responsePacket = new DatagramPacket(new byte[255], 255);

        super.receive(responsePacket);

        return new String(responsePacket.getData()).trim().replaceAll("\u0000.*", "");
    }

}
