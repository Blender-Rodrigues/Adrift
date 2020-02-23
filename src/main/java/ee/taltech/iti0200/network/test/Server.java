package ee.taltech.iti0200.network.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static ee.taltech.iti0200.network.ClientSocket.CONFIRMATION;
import static ee.taltech.iti0200.network.Network.DOWNSTREAM_PORT;
import static ee.taltech.iti0200.network.Network.UPSTREAM_PORT;

public class Server {

    public static void main(String[] args) throws IOException {

        final Set<Entry<InetAddress, Integer>> clients = ConcurrentHashMap.newKeySet();

        Receiver receiver = new Receiver(clients, new DatagramSocket(UPSTREAM_PORT));
        Sender sender = new Sender(clients, new DatagramSocket(DOWNSTREAM_PORT));

        receiver.start();
        sender.start();
    }

    public static class Receiver extends Thread {

        private final Set<Entry<InetAddress, Integer>> clients;
        private DatagramSocket socket;

        public Receiver(Set<Entry<InetAddress, Integer>> clients, DatagramSocket socket) {
            this.clients = clients;
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Server Receiver ready");
            while (true) {
                try {
                    DatagramPacket requestPacket = new DatagramPacket(new byte[255], 255);
                    socket.receive(requestPacket);

                    String data = new String(requestPacket.getData()).trim().replaceAll("\u0000.*", "");
                    InetAddress address = requestPacket.getAddress();
                    int port = requestPacket.getPort();

                    if (data.startsWith(CONFIRMATION)) {

                        String[] parts = data.split(" ");
                        int listenerPort = Integer.parseInt(parts[1]);

                        System.out.println("Server Receiver: registering new listener " + address.toString() + ":" + listenerPort);
                        clients.add(new SimpleEntry<>(address, listenerPort));

                        byte[] response = CONFIRMATION.getBytes();

                        DatagramPacket responsePacket = new DatagramPacket(response, response.length, address, port);

                        System.out.println("Server Receiver: responding with confirmation to " + address.toString() + ":" + port);

                        socket.send(responsePacket);

                    } else {
                        System.out.println("Server Receiver got: " + data + " from: " + address.toString() + ":" + port);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Thread.yield();
            }
        }

    }

    public static class Sender extends Thread {

        private final Set<Entry<InetAddress, Integer>> clients;
        private DatagramSocket socket;

        public Sender(Set<Entry<InetAddress, Integer>> clients, DatagramSocket socket) {
            this.clients = clients;
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Server Sender: ready");
            while (true) {
                try {
                    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                    String message = input.readLine();

                    byte[] response = message.getBytes();

                    Set<Entry<InetAddress, Integer>> current;

                    synchronized (clients) {
                        current = new HashSet<>(clients);
                    }

                    if (current.isEmpty()) {
                        System.out.println("Server Sender: No listeners");
                    } else {
                        System.out.println("Server Sender: listeners: " + current.size());
                    }

                    for (Entry<InetAddress, Integer> entry: current) {
                        try {
                            System.out.println("Server Sender sent [" + message + "]: to listener " + entry.getKey().toString() + ":" + entry.getValue());

                            DatagramPacket responsePacket = new DatagramPacket(response, response.length, entry.getKey(), entry.getValue());
                            socket.send(responsePacket);
                        }  catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Thread.yield();
            }
        }
    }

}
