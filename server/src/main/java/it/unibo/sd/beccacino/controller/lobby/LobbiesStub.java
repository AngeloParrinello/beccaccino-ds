package it.unibo.sd.beccacino.controller.lobby;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.*;
import it.unibo.sd.beccacino.*;
import it.unibo.sd.beccacino.controller.game.GameStub;
import it.unibo.sd.beccacino.rabbitmq.RabbitMQManager;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LobbiesStub {

    private final RabbitMQManager rabbitMQManager;
    private final LobbyManager lobbyManager;
    private final String resultsQueue = "resultsQueueLobbies";
    private Channel channel;
    private Connection connection;
    private Lobby lastOperation;
    private ResponseCode lastResponseCode;

    public LobbiesStub(GameStub gameStub) {
        this.rabbitMQManager = new RabbitMQManager();
        this.lobbyManager = new LobbyManagerImpl(this, gameStub);
        try {
            this.connection = this.rabbitMQManager.createConnection();
            this.channel = connection.createChannel();
            this.run();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void run() throws IOException {
        String todoQueue = "todoQueueLobbies";
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

                Request request = Request.parseFrom(body);

                lobbyManager.handleRequest(request);

                if(request.getLobbyMessage().equals("leave")){
                    try {
                        channel.queueDeclarePassive(resultsQueue + request.getRequestingPlayer().getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    createQueueFor(request);
                }
            }
        });
    }

    public void sendLobbyResponse(Lobby lobbyUpdated, ResponseCode responseCode, Player requestingPlayer) {
        this.lastOperation = lobbyUpdated;
        this.lastResponseCode = responseCode;
        Response response;

        if (lobbyUpdated != null) {
            response = Response.newBuilder()
                    .setLobby(lobbyUpdated)
                    .setResponseCode(responseCode.getCode())
                    .setResponseMessage(responseCode.getMessage())
                    .setRequestingPlayer(requestingPlayer)
                    .build();
        } else {
            response = Response.newBuilder()
                    .setResponseCode(responseCode.getCode())
                    .setResponseMessage(responseCode.getMessage())
                    .setRequestingPlayer(requestingPlayer)
                    .build();
        }

        try {
            channel.basicPublish(resultsQueue, "", null, response.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendGameStartLobbyResponse(Lobby lobby, Player requestingPlayer, String gameID) {
        Response response = Response.newBuilder()
                    .setLobby(lobby)
                    .setResponseCode(300)
                    .setResponseMessage(gameID)
                    .setRequestingPlayer(requestingPlayer)
                    .build();

        System.out.println("La response del game start è" + response);

        try {
            channel.basicPublish(resultsQueue, "", null, response.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendGameReconnectLobbyResponse(Lobby lobby, Player requestingPlayer, String gameID) {
        Response response = Response.newBuilder()
                .setLobby(lobby)
                .setResponseCode(303)
                .setResponseMessage(gameID)
                .setRequestingPlayer(requestingPlayer)
                .build();

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

    public void createQueueFor(Request request){
        final String queueName = resultsQueue + request.getRequestingPlayer().getId();
        try {
            this.channel.queueDeclare(queueName, false, false, false, null);
            this.channel.exchangeDeclare(resultsQueue, BuiltinExchangeType.FANOUT);
            this.channel.queueBind(queueName, resultsQueue, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
