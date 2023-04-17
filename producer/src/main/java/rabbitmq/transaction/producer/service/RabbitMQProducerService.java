package rabbitmq.transaction.producer.service;

import java.io.IOException;
import java.util.List;

public interface RabbitMQProducerService extends AutoCloseable {

    void send(List<String> messages) throws IOException;
}
