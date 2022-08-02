package it.unibo.sd.beccacino.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Connection;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

class RabbitMQManagerTest {

    private final RabbitMQManager rabbitMQManager = new RabbitMQManager();

    @Test
    void testCreateConnection() throws IOException, TimeoutException {
        Connection connection = rabbitMQManager.createConnection();
        assertNotNull(connection.createChannel());
    }

    @Test
    void testCreateQueueForReceive() throws IOException, TimeoutException {
        Connection connection = rabbitMQManager.createConnection();
        RabbitMQQueueBuilder builder = rabbitMQManager.getQueueBuilder();
        assertDoesNotThrow(() -> builder.getInstanceOfQueueBuilder().setNameQueue("test1")
                                                                    .setExchangeName("test1")
                                                                    .setChannel(connection.createChannel())
                                                                    .setArguments(null)
                                                                    .setAutoDelete(true)
                                                                    .setDurable(true)
                                                                    .setExclusive(true)
                                                                    .createQueue());

        assertEquals("test67", builder.getInstanceOfQueueBuilder().setNameQueue("test67")
                                                                .setExchangeName("test67")
                                                                .setChannel(connection.createChannel())
                                                                .createQueue());

        assertThrows(NullPointerException.class, () -> builder.getInstanceOfQueueBuilder().createQueue());

        assertThrows(IOException.class, () -> builder.getInstanceOfQueueBuilder().setNameQueue("test1").setChannel(connection.createChannel()).createQueue());

    }

    @Test
    void testCreateQueueForSend() throws IOException, TimeoutException {
        Connection connection = rabbitMQManager.createConnection();
        RabbitMQQueueBuilder builder = rabbitMQManager.getQueueBuilder();
        assertDoesNotThrow(() -> builder.getInstanceOfQueueBuilder().setNameQueue("test3")
                                                                   .setChannel(connection.createChannel())
                                                                   .setExchangeName("test3")
                                                                   .setExchangeType(BuiltinExchangeType.FANOUT)
                                                                   .setRoutingKey("")
                                                                   .setArguments(null)
                                                                   .setAutoDelete(true)
                                                                   .setDurable(true)
                                                                   .setExclusive(true)
                                                                   .createQueue());

        assertEquals("test4", builder.getInstanceOfQueueBuilder().setNameQueue("test4")
                                                                    .setExchangeName("test4")
                                                                    .setChannel(connection.createChannel())
                                                                    .createQueue());

        assertThrows(IOException.class, () -> {
            builder.getInstanceOfQueueBuilder().setNameQueue("test5")
                    .setChannel(connection.createChannel())
                    .createQueue();
        });


    }
}