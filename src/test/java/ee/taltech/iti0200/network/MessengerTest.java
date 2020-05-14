package ee.taltech.iti0200.network;

import ee.taltech.iti0200.network.message.Message;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MessengerTest {

    @Test
    void readInboxDumpsMessagesIntoNewList() {
        Message message1 = mock(Message.class);
        Message message2 = mock(Message.class);

        ConcurrentLinkedQueue<Message> inbox = new ConcurrentLinkedQueue<>();
        inbox.add(message1);
        inbox.add(message2);

        LinkedList<Message> messages = new Messenger(inbox, null, null).readInbox();

        assertThat(inbox).isEmpty();
        assertThat(messages).containsExactly(message1, message2);
    }

    @Test
    void writeInboxWritesToInbox() {
        Message message = mock(Message.class);
        ConcurrentLinkedQueue<Message> inbox = new ConcurrentLinkedQueue<>();

        new Messenger(inbox, null, null).writeInbox(message);

        assertThat(inbox).containsExactly(message);
    }

    @Test
    void readOutboxReturnsMessagesInOrder() {
        Message message1 = mock(Message.class);
        Message message2 = mock(Message.class);

        ConcurrentLinkedQueue<Message> outbox = new ConcurrentLinkedQueue<>();
        outbox.add(message1);
        outbox.add(message2);

        Messenger messenger = new Messenger(null, outbox, null);

        assertThat(messenger.readOutbox()).isEqualTo(message1);
        assertThat(messenger.readOutbox()).isEqualTo(message2);
        assertThat(messenger.readOutbox()).isNull();
    }

    @Test
    void writeOutbox() {
        Message message1 = mock(Message.class);
        Message message2 = mock(Message.class);

        ConcurrentLinkedQueue<Message> outbox = new ConcurrentLinkedQueue<>();

        new Messenger(null, outbox, null).writeOutbox(asList(message1, message2));

        assertThat(outbox).containsExactly(message1, message2);
    }

}
