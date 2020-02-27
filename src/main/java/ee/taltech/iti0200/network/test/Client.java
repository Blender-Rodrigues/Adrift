package ee.taltech.iti0200.network.test;

import ee.taltech.iti0200.network.ClientSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import static ee.taltech.iti0200.network.Network.DOWNSTREAM_PORT;
import static ee.taltech.iti0200.network.Network.HOST;
import static ee.taltech.iti0200.network.Network.UPSTREAM_PORT;

public class Client {

    public static void main(String[] args) throws IOException {

        UUID clientId = UUID.randomUUID();
        ClientSocket outSocket = new ClientSocket(HOST, UPSTREAM_PORT, clientId, "Sender");
        ClientSocket inSocket = new ClientSocket(HOST, DOWNSTREAM_PORT, clientId, "Receiver");

        Sender sender = new Sender(outSocket, inSocket.getLocalPort());
        Receiver receiver = new Receiver(inSocket);

        sender.initialize();
        receiver.initialize();
    }

    public static class Receiver extends Thread {

        private ClientSocket socket;

        public Receiver(ClientSocket socket) {
            this.socket = socket;
        }

        public void initialize() {
            super.start();
        }

        @Override
        public void run() {
            System.out.println("Client Receiver ready");
            while (true) {
                try {
                    String message = socket.receive();
                    System.out.println("Client Receiver got: " + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Thread.yield();
            }
        }

    }

    public static class Sender extends Thread {

        private ClientSocket socket;
        int receiverPort;

        public Sender(ClientSocket socket, int receiverPort) {
            this.socket = socket;
            this.receiverPort = receiverPort;
        }

        public void initialize() throws IOException {
            socket.confirmConnection(receiverPort);
            super.start();
        }

        @Override
        public void run() {
            System.out.println("Client Sender ready");
            while (true) {
                try {
                    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                    String message = input.readLine();
                    socket.send(message.getBytes());
                    System.out.println("Client Sender sent: " + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Thread.yield();
            }
        }
    }

}
