package it.unibo.sd.beccacino.controller.lobby;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.*;
import it.unibo.sd.beccacino.Lobby;
import it.unibo.sd.beccacino.Request;
import it.unibo.sd.beccacino.rabbitmq.RabbitMQManager;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LobbiesStub {

    private final RabbitMQManager rabbitMQManager;
    private final LobbyManager lobbyManager;

    public Lobby getLastOperation() {
        return lastOperation;
    }

    public int getLastResponseCode() {
        return lastResponseCode;
    }

    private Lobby lastOperation;
    private int lastResponseCode;

    public LobbiesStub() {
        this.rabbitMQManager = new RabbitMQManager();
        this.lobbyManager = new LobbyManagerImpl(this);
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
                .createQueueForSend();

        this.rabbitMQManager.getQueueBuilder()
                .getInstanceOfQueueBuilder()
                .setNameQueue(resultsQueue)
                .setChannel(channel)
                .createQueueForReceive();

        System.out.println("LobbiesStub Intialized Queue!");

        channel.basicConsume(resultsQueue, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws InvalidProtocolBufferException {

                System.out.println("LobbiesStub received file!");

                Request request = Request.parseFrom(body);

                System.out.println(request);

                lobbyManager.handleRequest(request);

                /*try {
                    channel.close();
                    connection.close();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }*/

                System.out.println("LobbiesStub send file to manager!");

            }
        });

    }

    public void sendLobbyResponse(Lobby lobbyUpdated, int responseCode) {
        //TODO control if the lobby!=null and response code is 200
        this.lastOperation = lobbyUpdated;
        this.lastResponseCode = responseCode;
    }

}
