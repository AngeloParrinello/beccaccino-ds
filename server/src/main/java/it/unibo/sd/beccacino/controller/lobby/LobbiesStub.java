package it.unibo.sd.beccacino.controller.lobby;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.*;
import it.unibo.sd.beccacino.*;
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

    public LobbiesStub() {
        this.rabbitMQManager = new RabbitMQManager();
        this.lobbyManager = new LobbyManagerImpl(this);
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
                    //TODO rimuovi la coda
                } else {
                    createQueueFor(request.getRequestingPlayer().getId());
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

    public Lobby getLastOperation() {
        return lastOperation;
    }

    public ResponseCode getLastResponseCode() {
        return lastResponseCode;
    }

    private void createQueueFor(String playerID){
        try {
            this.channel.queueDeclare(resultsQueue + playerID, false, false, false, null);
            this.channel.exchangeDeclare(resultsQueue, BuiltinExchangeType.FANOUT);
            this.channel.queueBind(resultsQueue + playerID, resultsQueue, "");
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
