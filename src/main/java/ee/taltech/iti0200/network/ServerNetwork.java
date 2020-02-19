package ee.taltech.iti0200.network;

import ee.taltech.iti0200.domain.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class ServerNetwork extends Network {

    private Logger logger;
    private DatagramSocket serverSocket;
    private HashMap<InetAddress, Integer> clients = new HashMap<>();

    public ServerNetwork(World world) throws IOException {
        super(world);
        logger = LogManager.getLogger(ServerNetwork.class);
        serverSocket = new DatagramSocket(8880);
    }

    @Override
    public void initialize() throws IOException {

    }

    @Override
    public void update(long tick) {
        DatagramPacket requestPacket = new DatagramPacket(new byte[4], 4);
        try {
            serverSocket.receive(requestPacket);
            clients.put(requestPacket.getAddress(), requestPacket.getPort());

            String data = new String(requestPacket.getData()).trim().replaceAll("\u0000.*", "");

            logger.info("Client {} response {}", requestPacket.getAddress().toString(), data);
        } catch (IOException e) {
            logger.error("Failed to read client response", e);
        }
    }

    @Override
    public void propagate(long tick) {
        if (clients.isEmpty()) {
            logger.info("No clients connected");
            return;
        }

        for (Map.Entry<InetAddress, Integer> client: clients.entrySet()) {
            InetAddress address = client.getKey();
            int port = client.getValue();

            String answer = "Server time: " + tick;
            byte[] response = answer.concat("\n").getBytes();

            DatagramPacket responsePacket = new DatagramPacket(response, response.length, address, port);
            try {
                serverSocket.send(responsePacket);
                logger.info("Sent tick update to " + address.toString());
            } catch (IOException e) {
                logger.error("Failed to update client " + address.toString(), e);
            }
        }
    }

}
