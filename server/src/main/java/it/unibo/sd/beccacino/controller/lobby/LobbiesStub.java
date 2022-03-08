package it.unibo.sd.beccacino.controller.lobby;

import com.rabbitmq.client.*;
import it.unibo.sd.beccacino.Agent;

import java.io.IOException;

public class LobbyListener extends Agent {
    private Boolean state;
    private Channel channel;

    public LobbyListener(String name) {
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

            }
        });

        while (this.state) {}

        channel.close();
        connection.close();
    }
}
