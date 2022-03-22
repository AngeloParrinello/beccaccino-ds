package it.unibo.sd.beccacino.controller.game;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.*;
import it.unibo.sd.beccacino.DBManager;
import it.unibo.sd.beccacino.Player;
import it.unibo.sd.beccacino.Request;
import it.unibo.sd.beccacino.Response;
import it.unibo.sd.beccacino.controller.lobby.LobbiesStub;
import it.unibo.sd.beccacino.rabbitmq.RabbitMQManager;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class RealStartGameRequestWithClient {

    public static void main(String[] args) {
        LobbiesStub lobbiesStub = new LobbiesStub();
        FakeClient client;
        client = new FakeClient(new RabbitMQManager());
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
                    .createQueueForSend();

            this.rabbitMQManager.getQueueBuilder()
                    .getInstanceOfQueueBuilder()
                    .setNameQueue(resultsQueue)
                    .setChannel(channel)
                    .createQueueForReceive();

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
