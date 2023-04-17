package rabbitmq.transaction.producer.service.impl;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import rabbitmq.transaction.producer.service.RabbitMQProducerService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Log
@Service
public class RabbitMQProducerServiceImpl implements RabbitMQProducerService {

    private final static String QUEUE_NAME = "transact_queue";

    private final static String FINAL_MESSAGE = "Message_block_end";

    private final Connection connection;

    public RabbitMQProducerServiceImpl() throws IOException, TimeoutException {
        final ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connection = connectionFactory.newConnection();
    }

    @Override
    public void send(final List<String> messages) throws IOException {
        final Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, /*durable*/false, /*exclusive*/false, /*autoDelete*/false, /*arguments*/null);

        channel.txSelect();
        for (final String message : messages) {
            channel.basicPublish(/*exchange*/"", /*routingKey*/QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            log.info(String.format("Message sent: %s", message));
        }

        channel.basicPublish(/*exchange*/"", /*routingKey*/QUEUE_NAME, null, FINAL_MESSAGE.getBytes(StandardCharsets.UTF_8));
        log.info(String.format("Message sent: %s", FINAL_MESSAGE));

        log.info("Producer Committing Transaction...");
        channel.txCommit();
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
