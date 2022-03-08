package it.unibo.sd.beccacino.controller;

import com.rabbitmq.client.*;
import it.unibo.sd.beccacino.Agent;

import java.io.IOException;

public class ServerListener extends Agent {
    private Boolean state;
    private Channel channel;

    public ServerListener(String name) {
        super(name);
    }

    @Override
    public void run(String myName, Connection connection) throws Exception {
        this.channel = connection.createChannel();
        this.channel.exchangeDeclare("public", BuiltinExchangeType.DIRECT);
        var declaration = channel.queueDeclare();
        String queueName = declaration.getQueue();
        this.channel.queueBind(queueName, "public", "from.*");

        channel.basicConsume(declaration.getQueue(), new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                final LobbyManager lobbyManager = new LobbyManagerImpl();
                switch (consumerTag) {
                    case "createLobby":
                        lobbyManager.createLobby();
                    case "joinLobby":
                        lobbyManager.joinLobby(8758);
                    case "deleteLobby":
                        lobbyManager.deleteLobby(8758);
                    default:
                        throw new IllegalStateException("Server: received an unknown message type.");
                }
            }
        });

        while (this.state) {}

        channel.close();
        connection.close();
    }
}
