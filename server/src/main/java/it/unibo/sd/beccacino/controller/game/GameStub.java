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
    String todoQueue = "todoQueueGames";
    String resultsQueue = "resultsQueueGames";
    private Channel channel;
    private Connection connection;
    private Game lastOperation;
    private ResponseCode lastResponseCode;

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
                .createQueue();

        channel.basicConsume(todoQueue, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws InvalidProtocolBufferException {

                GameRequest gameRequest = GameRequest.parseFrom(body);

                System.out.println("GameRequest arrivata: " + gameRequest);

                gameRequestHandler.handleRequest(gameRequest);

            }
        });

    }

    public void sendGameResponse(Game gameUpdated, ResponseCode responseCode) {
        if (responseCode == ResponseCode.START_OK) {
            this.setupQueues(gameUpdated);
        }

        this.lastOperation = gameUpdated;
        this.lastResponseCode = responseCode;

        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA: "+gameUpdated.getPlayersList());

        gameUpdated.getPlayersList().forEach(player -> {
            final int index = gameUpdated.getPlayersList().indexOf(player);

            Game game = Game.newBuilder()
                    .setPublicData(gameUpdated.getPublicData())
                    .addPrivateData(gameUpdated.getPrivateData(index))
                    .addAllPlayers(gameUpdated.getPlayersList())
                    .setRound(gameUpdated.getRound())
                    .build();

            GameResponse gameResponse;
            gameResponse = GameResponse.newBuilder()
                    .setGame(game)
                    .setResponseCode(responseCode.getCode())
                    .setResponseMessage(responseCode.getMessage())
                    .build();

            try {
                System.out.println("Invio");
                channel.basicPublish(resultsQueue + player.getId(),
                        "", null, gameResponse.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void sendGameErrorResponse(ResponseCode responseCode, Player requestingPlayer, String gameId) {
        this.lastOperation = null;
        this.lastResponseCode = responseCode;

        GameResponse gameResponse;
        gameResponse = GameResponse.newBuilder()
                .setResponseCode(responseCode.getCode())
                .setResponseMessage(responseCode.getMessage())
                .build();

        try {
            channel.basicPublish(resultsQueue + requestingPlayer.getId(), "", null, gameResponse.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupQueues(Game game) {
        game.getPlayersList().forEach(g -> System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBB: "+g.getNickname()));
        game.getPlayersList().forEach(player -> {
            String resultQueueName = resultsQueue + player.getId();
            try {
                this.rabbitMQManager.getQueueBuilder()
                        .getInstanceOfQueueBuilder()
                        .setNameQueue(resultQueueName)
                        .setExchangeName(resultQueueName)
                        .setChannel(channel)
                        .createQueue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
