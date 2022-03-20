package it.unibo.sd.beccacino.controller.game;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.*;
import it.unibo.sd.beccacino.*;
import it.unibo.sd.beccacino.rabbitmq.RabbitMQManager;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class GameStub {

    private final RabbitMQManager rabbitMQManager;
    private final GameRequestHandler gameRequestHandler;
    private Channel channel;
    private Connection connection;

    private Game lastOperation;
    private ResponseCode lastResponseCode;

    String todoQueue = "todoQueueGames";
    String resultsQueue = "resultsQueueGames";

    public GameStub() {
        this.rabbitMQManager = new RabbitMQManager();
        this.gameRequestHandler = new GameRequestHandlerImpl(this);
        try {
            this.connection = this.rabbitMQManager.createConnection();
            this.channel = connection.createChannel();
            this.run();
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

        System.out.println("GameStub Intialized Queue!");

        channel.basicConsume(resultsQueue, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws InvalidProtocolBufferException {

                System.out.println("GameStub received file!");

                GameRequest gameRequest = GameRequest.parseFrom(body);

                System.out.println(gameRequest);

                gameRequestHandler.handleRequest(gameRequest);

                System.out.println("GameStub send file to manager!");

            }
        });

    }

    public void sendGameResponse(Game gameUpdated, ResponseCode responseCode) {
        this.lastOperation = gameUpdated;
        this.lastResponseCode = responseCode;
        GameResponse gameResponse;

        if (gameUpdated != null) {
            gameResponse = GameResponse.newBuilder()
                    .setGame(gameUpdated)
                    .setResponseCode(responseCode.getCode())
                    .setResponseMessage(responseCode.getMessage())
                    .build();
        } else {
            gameResponse = GameResponse.newBuilder()
                    .setResponseCode(responseCode.getCode())
                    .setResponseMessage(responseCode.getMessage())
                    .build();
        }

        try {
            channel.basicPublish(todoQueue, "", null, gameResponse.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Game getLastOperation() {
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
