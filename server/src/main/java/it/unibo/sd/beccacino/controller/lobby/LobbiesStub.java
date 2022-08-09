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

                System.out.println("Code ricevono.... : " + Request.parseFrom(body));

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

        System.out.println("Lobby Stub received request from manager: "+ lobbyUpdated + " with code "+ responseCode);

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

    private void createQueueFor(String id){
        try {
            this.channel.queueDeclare(resultsQueue+id, false, false, false, null);
            this.channel.exchangeDeclare(resultsQueue, BuiltinExchangeType.FANOUT);
            this.channel.queueBind(resultsQueue+id, resultsQueue, "");
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
