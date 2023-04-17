package rabbitmq.transaction.producer.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rabbitmq.transaction.producer.controller.request.MessagesDTO;
import rabbitmq.transaction.producer.controller.RabbitMQProducerController;
import rabbitmq.transaction.producer.service.RabbitMQProducerService;

import java.io.IOException;

@RestController
public class RabbitMQProducerControllerImpl implements RabbitMQProducerController {

    private final RabbitMQProducerService rabbitMQProducerService;

    @Autowired
    public RabbitMQProducerControllerImpl(final RabbitMQProducerService rabbitMQProducerService) {
        this.rabbitMQProducerService = rabbitMQProducerService;
    }

    @Override
    @PostMapping("/messages")
    public ResponseEntity sendMessages(@RequestBody final MessagesDTO messages) throws IOException {
        rabbitMQProducerService.send(messages.getMessages());
        return new ResponseEntity(HttpStatus.OK);
    }
}
