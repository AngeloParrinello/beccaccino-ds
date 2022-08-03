package it.unibo.sd.beccacino;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.*;
import it.unibo.sd.beccacino.controller.lobby.LobbiesStub;
import it.unibo.sd.beccacino.rabbitmq.RabbitMQManager;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RealStartGameRequestWithClient {

    public static void main(String[] args) {
       new LobbiesStub();
        FakeClient client = new FakeClient(new RabbitMQManager());
        try {
            client.simpleLobbyPublish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class FakeClient {
        private final RabbitMQManager rabbitMQManager;
        private Connection connection;
        private Channel channel;

        private final String todoQueue = "todoQueueLobbies";
        private final String resultsQueue = "resultsQueueLobbies";

        public FakeClient(RabbitMQManager rabbitMQManager) {
            this.rabbitMQManager = rabbitMQManager;
            try {
                connection = this.rabbitMQManager.createConnection();
                channel = connection.createChannel();
                this.run();
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }

        private void run() throws IOException, TimeoutException {

            this.rabbitMQManager.getQueueBuilder()
                    .getInstanceOfQueueBuilder()
                    .setNameQueue(todoQueue)
                    .setExchangeName(todoQueue)
                    .setChannel(channel)
                    .createQueue();

            this.rabbitMQManager.getQueueBuilder()
                    .getInstanceOfQueueBuilder()
                    .setNameQueue(resultsQueue)
                    .setExchangeName(resultsQueue)
                    .setChannel(channel)
                    .createQueue();

            System.out.println("Client Intialized Queue!");
        }

        public void simpleLobbyPublish() throws IOException {

            Request lobbyRequest = Request.newBuilder()
                    .setLobbyMessage("create")
                    .setRequestingPlayer(Player.newBuilder().setId("1").setNickname("PlayerProva").build())
                    .build();

            System.out.println(lobbyRequest);

            channel.basicPublish(todoQueue, "", null, lobbyRequest.toByteArray());

            System.out.println("Client published Lobby create message!");

            channel.basicConsume(resultsQueue, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws InvalidProtocolBufferException {

                    System.out.println("Client received Lobby response message!");

                    Response lobbyResponse = Response.parseFrom(body);

                    System.out.println("Lobby response message: " + lobbyResponse);

                    System.out.println("Lobby created: " + lobbyResponse.getLobby());

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

}
