package it.unibo.sd.beccacino.controller.lobby;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.*;
import it.unibo.sd.beccacino.Lobby;
import it.unibo.sd.beccacino.Request;
import it.unibo.sd.beccacino.Response;
import it.unibo.sd.beccacino.ResponseCode;
import it.unibo.sd.beccacino.rabbitmq.RabbitMQManager;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LobbiesStub {

    private final RabbitMQManager rabbitMQManager;
    private final LobbyManager lobbyManager;
    private final String todoQueue = "todoQueueLobbies";
    private final String resultsQueue = "resultsQueueLobbies";
    private Channel channel;
    private Connection connection;
    private Lobby lastOperation;
    private ResponseCode lastResponseCode;

    public LobbiesStub() {
        System.out.println("Connessione....");
        this.rabbitMQManager = new RabbitMQManager();
        this.lobbyManager = new LobbyManagerImpl(this);
        try {
            System.out.println("Connessione quasi ....");
            this.connection = this.rabbitMQManager.createConnection();
            System.out.println("Connessione quasi quasi ....");
            this.channel = connection.createChannel();
            System.out.println("Connessione iniziata....");
            this.run();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void run() throws IOException {

        System.out.println("Code....");

        this.rabbitMQManager.getQueueBuilder()
                .getInstanceOfQueueBuilder()
                .setNameQueue(resultsQueue)
                .setExchangeName(resultsQueue)
                .setChannel(channel)
                .createQueue();

        System.out.println("Code quasi....");

        this.rabbitMQManager.getQueueBuilder()
                .getInstanceOfQueueBuilder()
                .setNameQueue(todoQueue)
                .setExchangeName(todoQueue)
                .setChannel(channel)
                .createQueue();

        System.out.println("Code iniziate....");

        channel.basicConsume(todoQueue, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws InvalidProtocolBufferException {

                System.out.println("Code ricevono.... : " + Request.parseFrom(body));

                Request request = Request.parseFrom(body);

                lobbyManager.handleRequest(request);
            }
        });


    }

    public void sendLobbyResponse(Lobby lobbyUpdated, ResponseCode responseCode) {
        this.lastOperation = lobbyUpdated;
        this.lastResponseCode = responseCode;
        Response response;

        System.out.println("Lobby Stub received request from manager: "+ lobbyUpdated + " with code "+ responseCode);

        if (lobbyUpdated != null) {
            response = Response.newBuilder()
                    .setLobby(lobbyUpdated)
                    .setResponseCode(responseCode.getCode())
                    .setResponseMessage(responseCode.getMessage())
                    .build();
        } else {
            response = Response.newBuilder()
                    .setResponseCode(responseCode.getCode())
                    .setResponseMessage(responseCode.getMessage())
                    .build();
        }

        System.out.println("And the final response is"+response);

        try {
            channel.basicPublish(resultsQueue, "", null, response.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Lobby getLastOperation() {
        return lastOperation;
    }

    public ResponseCode getLastResponseCode() {
        return lastResponseCode;
    }

    private void shutdownStub() {
        try {
            channel.close();
            connection.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();

        }
    }

}
