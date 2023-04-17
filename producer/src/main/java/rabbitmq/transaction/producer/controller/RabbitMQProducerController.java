package rabbitmq.transaction.producer.controller;

import org.springframework.http.ResponseEntity;
import rabbitmq.transaction.producer.controller.request.MessagesDTO;

import java.io.IOException;

public interface RabbitMQProducerController {

    ResponseEntity sendMessages(MessagesDTO messages) throws IOException;
}
