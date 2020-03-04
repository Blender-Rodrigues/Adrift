package ee.taltech.iti0200.network.client;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.network.TcpListener;
import ee.taltech.iti0200.network.TcpSender;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Ping;
import ee.taltech.iti0200.network.message.RegisterClientRequest;
import ee.taltech.iti0200.network.message.RegisterClientResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ClientNetwork extends Network {

    private static final int TIMEOUT = 30000;

    private final Logger logger = LogManager.getLogger(ClientNetwork.class);
    private final Messenger messenger = new Messenger();
    private final UUID id = UUID.randomUUID();
    private final String host;
    private final Integer tcpPort;

    private Socket tcpSocket;
    private ObjectInputStream tcpInput;
    private ObjectOutputStream tcpOutput;

    public ClientNetwork(World world, String host, Integer tcpPort) {
        super(world);
        this.host = host;
        this.tcpPort = tcpPort;
    }

    @Override
    public void initialize() throws IOException, ClassNotFoundException {
        tcpSocket = new Socket(InetAddress.getByName(host), tcpPort);
        tcpOutput = new ObjectOutputStream(tcpSocket.getOutputStream());
        tcpOutput.flush();

        tcpInput = new ObjectInputStream(tcpSocket.getInputStream());

        tcpOutput.writeObject(new RegisterClientRequest(id));
        tcpOutput.flush();

        RegisterClientResponse response = null;
        long now = System.currentTimeMillis();
        long until = System.currentTimeMillis() + TIMEOUT;

        while (now < until) {
            Object message = tcpInput.readObject();
            if (message instanceof RegisterClientResponse) {
                response = (RegisterClientResponse) message;
                break;
            } else {
                logger.debug("Received premature message {} from server", message.getClass());
            }
            now = System.currentTimeMillis();
        }

        if (response == null) {
            throw new RuntimeException(String.format("Failed to get a response from server in %d ms", TIMEOUT));
        }

        logger.info(
            "Received sender/listener UDP ports from server: {}/{}",
            response.getUdpSenderPort(),
            response.getUdpListenerPort()
        );

        new TcpSender(tcpOutput, messenger).start();

        Map<Class<? extends Message>, Consumer<Message>> handlers = new HashMap<>();

        handlers.put(Ping.class, (message) -> {
            Ping ping = (Ping) message;
            logger.info("Ping received from server at {}", ping.getTime());
            messenger.writeInbox(ping);
        });

        new TcpListener(tcpInput, messenger, handlers).start();
    }

    @Override
    public void update(long tick) {

    }

    @Override
    public void propagate(long tick) {
        Thread.yield();
        if (tick % 300 == 0) {
            LinkedList<Message> messages = new LinkedList<>();

            messages.add(new Ping(tick, id));

            messenger.writeOutbox(messages);
        }
    }

    @Override
    public void terminate() {
        super.terminate();
        Stream.of(tcpInput, tcpOutput, tcpSocket)
            .forEach(closeable -> {
                try {
                    closeable.close();
                } catch (IOException e) {
                    logger.error("Failed to close " + closeable.getClass() + " with: " + e.getMessage(), e);
                }
            });
    }

}
