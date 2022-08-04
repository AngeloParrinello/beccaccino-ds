package it.unibo.sd.beccacino.controller.lobby;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.*;
import it.unibo.sd.beccacino.Lobby;
import org.slf4j.LoggerFactory;
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

    private final String todoQueue = "todoQueueLobbies";
    private final String resultsQueue = "resultsQueueLobbies";

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
        System.out.println("Finish");
    }

    public void run() throws IOException {

        this.rabbitMQManager.getQueueBuilder()
                .getInstanceOfQueueBuilder()
                .setNameQueue(resultsQueue)
                .setExchangeName(resultsQueue)
                .setChannel(channel)
                .createQueue();

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

                System.out.println("Server receive a request: "+ request);

                lobbyManager.handleRequest(request);
            }
        });


    }

    public void sendLobbyResponse(Lobby lobbyUpdated, ResponseCode responseCode) {
        this.lastOperation = lobbyUpdated;
        this.lastResponseCode = responseCode;
        Response response;

        if(lobbyUpdated != null) {
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
