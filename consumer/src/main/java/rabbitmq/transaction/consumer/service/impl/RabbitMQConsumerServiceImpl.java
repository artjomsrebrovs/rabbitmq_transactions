package rabbitmq.transaction.consumer.service.impl;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import rabbitmq.transaction.consumer.service.RabbitMQConsumerService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Log
@Service
public class RabbitMQConsumerServiceImpl implements RabbitMQConsumerService {

    private final static String QUEUE_NAME = "transact_queue";

    private final static String FINAL_MESSAGE = "Message_block_end";

    private final Connection connection;

    private final Channel channel;

    public RabbitMQConsumerServiceImpl() throws IOException, TimeoutException {
        final ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connection = connectionFactory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, /*durable*/false, /*exclusive*/false, /*autoDelete*/false, /*arguments*/null);
        setup();
    }

    private void setup() throws IOException {
        final DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            channel.txSelect();

            final String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            log.info(String.format("Message received: %s", message));
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

            if (message.equals(FINAL_MESSAGE)) {
                log.info("Consumer Committing Transaction...");
                channel.txCommit();
                //channel.txRollback();
                //channel.basicCancel("");
            }
        };
        channel.basicConsume(QUEUE_NAME, /*autoAck*/false, deliverCallback, consumerTag -> {
        });

    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
