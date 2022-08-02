package it.unibo.sd.beccacino.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Map;

public interface RabbitMQQueueBuilder {

    RabbitMQQueueBuilder getInstanceOfQueueBuilder();

    RabbitMQQueueBuilder setNameQueue(String nameQueue);

    RabbitMQQueueBuilder setChannel(Channel channel);

    RabbitMQQueueBuilder setDurable(boolean durable);

    RabbitMQQueueBuilder setExclusive(boolean exclusive);

    RabbitMQQueueBuilder setAutoDelete(boolean autoDelete);

    RabbitMQQueueBuilder setRoutingKey(String routingKey);

    RabbitMQQueueBuilder setExchangeType(BuiltinExchangeType exchangeType);

    RabbitMQQueueBuilder setExchangeName(String exchangeName);

    RabbitMQQueueBuilder setArguments(Map<String, Object> arguments);

    String createQueue() throws IOException;
}
