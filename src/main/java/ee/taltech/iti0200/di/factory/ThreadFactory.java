package ee.taltech.iti0200.di.factory;

import ee.taltech.iti0200.network.Connection;
import ee.taltech.iti0200.network.Listener;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.Sender;
import ee.taltech.iti0200.network.message.Message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.function.Consumer;

public class ThreadFactory {

    public Sender createSender(String name, ObjectOutputStream output, Messenger messenger, Connection connection) {
        return new Sender(name, output, messenger, connection);
    }

    public Listener createListener(String name, ObjectInputStream input, Messenger messenger, Connection connection) {
        return new Listener(name, input, messenger, connection);
    }

    public Listener createListener(
        String name,
        ObjectInputStream input,
        Messenger messenger,
        Connection connection,
        Map<Class<? extends Message>, Consumer<Message>> handlers
    ) {
        return new Listener(name, input, messenger, connection, handlers);
    }

}
