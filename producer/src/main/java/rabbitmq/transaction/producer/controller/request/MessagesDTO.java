package rabbitmq.transaction.producer.controller.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MessagesDTO {

    private List<String> messages;
}
