package it.unibo.sd.beccacino.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.*;
import it.unibo.sd.beccacino.Player;
import it.unibo.sd.beccacino.rabbitmq.RabbitMQManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

public class ProtobufTest {
    Server server;
    Client client;

    @BeforeEach
    void beforeEach() {
        RabbitMQManager rabbitMQManager = new RabbitMQManager();
        server = new Server(rabbitMQManager);
        client = new Client(rabbitMQManager);
    }

    @Test
    void simpleTrasmissionTest() {
        assertDoesNotThrow(()-> {
            client.run();
            server.run();
        });
    }

    private class Client {
        private final RabbitMQManager rabbitMQManager;
        public Client(RabbitMQManager rabbitMQManager) {
            this.rabbitMQManager = rabbitMQManager;
        }


        public void run() throws IOException, TimeoutException {

            Connection connection = this.rabbitMQManager.createConnection();

            Channel channel = connection.createChannel();

            String todoQueue = "todoQueue";
            String resultsQueue = "resultsQueue";

            this.rabbitMQManager.getQueueBuilder()
                    .getInstanceOfQueueBuilder()
                    .setNameQueue(todoQueue)
                    .setExchangeName(todoQueue)
                    .setChannel(channel)
                    .createQueue();

            this.rabbitMQManager.getQueueBuilder()
                    .getInstanceOfQueueBuilder()
                    .setNameQueue(resultsQueue)
                    .setChannel(channel)
                    .createQueue();

            System.out.println("Client Intialized Queue!");

            Player player = Player.newBuilder().setId("1").setNickname("prova").build();

            System.out.println(player);

            System.out.println("Client published player!");

            channel.basicPublish(todoQueue, "", null, player.toByteArray());

            channel.basicConsume(resultsQueue, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws InvalidProtocolBufferException {

                    System.out.println("Client received player file!");

                    Player player1 = Player.parseFrom(body);

                    System.out.println(player1);

                    try {
                        channel.close();
                        connection.close();
                    } catch (IOException | TimeoutException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Client closing!");

                }
            });

        }
    }

    private class Server {
        private final RabbitMQManager rabbitMQManager;

        public Server(RabbitMQManager rabbitMQManager) {
            this.rabbitMQManager = rabbitMQManager;
        }


        public void run() throws IOException, TimeoutException {

            Connection connection = this.rabbitMQManager.createConnection();

            Channel channel = connection.createChannel();

            String todoQueue = "todoQueue";
            String resultsQueue = "resultsQueue";

            this.rabbitMQManager.getQueueBuilder()
                    .getInstanceOfQueueBuilder()
                    .setNameQueue(resultsQueue)
                    .setExchangeName(resultsQueue)
                    .setChannel(channel)
                    .createQueue();

            this.rabbitMQManager.getQueueBuilder()
                    .getInstanceOfQueueBuilder()
                    .setNameQueue(todoQueue)
                    .setChannel(channel)
                    .createQueue();

            System.out.println("Server Intialized Queue!");

            channel.basicConsume(todoQueue, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws InvalidProtocolBufferException {

                    System.out.println("Server received player file!");

                    Player player1 = Player.parseFrom(body);

                    System.out.println(player1);

                    try {
                        channel.basicPublish(resultsQueue, "", null, player1.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Server send player file!");

                    try {
                        channel.close();
                        connection.close();
                    } catch (IOException | TimeoutException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Server closing!");

                }
            });

        }
    }
}
