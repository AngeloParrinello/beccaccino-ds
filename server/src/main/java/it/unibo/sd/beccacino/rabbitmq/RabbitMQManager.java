package it.unibo.sd.beccacino.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQManager {

    private final RabbitMQQueueBuilder queueBuilder;

    public RabbitMQManager() {
        this.queueBuilder = new RabbitMQQueueBuilderImpl();
    }

    public Connection createConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("user");
        factory.setPassword("user");
        factory.setVirtualHost("/");
        // not "localhost" because i'm in the container
        // factory.setHost("localhost");
        factory.setHost(System.getenv("RABBIT_HOST"));
        factory.setPort(5672);
        factory.setAutomaticRecoveryEnabled(true);
        while(true) {
            try {
                return factory.newConnection();
            } catch (java.net.ConnectException e) {
                System.out.println("Retrying connection to RabbitMQ");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public RabbitMQQueueBuilder getQueueBuilder() {
        return queueBuilder.getInstanceOfQueueBuilder();
    }
}
