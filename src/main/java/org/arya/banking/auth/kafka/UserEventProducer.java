package org.arya.banking.auth.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arya.banking.common.avro.LoginFailedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static org.arya.banking.common.kafka.constants.KafkaConstants.AUTH_FAILED_TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, LoginFailedEvent> kafkaTemplate;

    public void sendLoginFailedEvent(LoginFailedEvent event) {
        kafkaTemplate.send(AUTH_FAILED_TOPIC, event.getUserId().toString(), event);
        log.info("User update event for userId: [{}] sent", event.getUserId());
    }
}
