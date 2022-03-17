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
    private Channel channel;
    private Connection connection;

    private Lobby lastOperation;
    private ResponseCode lastResponseCode;

    String todoQueue = "todoQueueLobbies";
    String resultsQueue = "resultsQueueLobbies";

    public LobbiesStub() {
        this.rabbitMQManager = new RabbitMQManager();
        this.lobbyManager = new LobbyManagerImpl(this);
        try {
            this.connection = this.rabbitMQManager.createConnection();
            this.channel = connection.createChannel();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void run() throws IOException {

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

        System.out.println("LobbiesStub Intialized Queue!");

        channel.basicConsume(resultsQueue, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws InvalidProtocolBufferException {

                System.out.println("LobbiesStub received file!");

                Request request = Request.parseFrom(body);

                System.out.println(request);

                lobbyManager.handleRequest(request);

                System.out.println("LobbiesStub send file to manager!");

            }
        });

    }

    public void sendLobbyResponse(Lobby lobbyUpdated, ResponseCode responseCode) {
        this.lastOperation = lobbyUpdated;
        this.lastResponseCode = responseCode;
        if(lobbyUpdated != null) {
            Response response = Response.newBuilder()
                    .setLobby(lobbyUpdated)
                    .setResponseCode(responseCode.getCode())
                    .build();
        }
        // TODO remove comments
        /*try {
            channel.basicPublish(todoQueue, "", null, response.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
