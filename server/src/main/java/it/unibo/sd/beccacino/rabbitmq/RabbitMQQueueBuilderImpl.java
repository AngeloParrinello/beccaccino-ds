package it.unibo.sd.beccacino.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Map;

public class RabbitMQQueueBuilderImpl implements RabbitMQQueueBuilder {

    private String nameQueue;
    private Channel channel;
    private boolean durable = false;
    private boolean exclusive = false;
    private boolean autoDelete = false;
    private Map<String, Object> arguments = null;
    private String exchangeName = "";
    private BuiltinExchangeType exchangeType = BuiltinExchangeType.DIRECT;
    private String routingKey = "";

    @Override
    public RabbitMQQueueBuilder getInstanceOfQueueBuilder() {
        return new RabbitMQQueueBuilderImpl();
    }

    @Override
    public RabbitMQQueueBuilder setNameQueue(String nameQueue) {
        this.nameQueue = nameQueue;
        return this;
    }

    @Override
    public RabbitMQQueueBuilder setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }

    @Override
    public RabbitMQQueueBuilder setDurable(boolean durable) {
        this.durable = durable;
        return this;
    }

    @Override
    public RabbitMQQueueBuilder setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
        return this;
    }

    @Override
    public RabbitMQQueueBuilder setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
        return this;
    }

    @Override
    public RabbitMQQueueBuilder setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
        return this;
    }

    @Override
    public RabbitMQQueueBuilder setExchangeType(BuiltinExchangeType exchangeType) {
        this.exchangeType = exchangeType;
        return this;
    }

    @Override
    public RabbitMQQueueBuilder setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
        return this;
    }

    @Override
    public RabbitMQQueueBuilder setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
        return this;
    }

    @Override
    public String createQueue() throws IOException {
        this.channel.queueDeclare(this.nameQueue, this.durable, this.exclusive, this.autoDelete, this.arguments);
        this.channel.exchangeDeclare(this.exchangeName, this.exchangeType);
        this.channel.queueBind(this.nameQueue, this.exchangeName, this.routingKey);
        return this.nameQueue;
    }
}
