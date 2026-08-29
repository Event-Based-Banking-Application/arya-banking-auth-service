package org.arya.banking.auth.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arya.banking.auth.service.KeyCloakService;
import org.arya.banking.common.avro.OutboxKafkaEvent;
import org.arya.banking.common.avro.UserCreateEvent;
import org.arya.banking.common.utils.GsonParser;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static org.arya.banking.common.constants.kafka.KafkaConstants.USER_UPDATE_TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserUpdateEventListener {

    private final KeyCloakService keyCloakService;

    @KafkaListener(id = "user-update-event", topics = USER_UPDATE_TOPIC)
    public void onUserUpdateEvent(OutboxKafkaEvent event) {
        UserCreateEvent userCreateEvent = GsonParser.fromJson(event.getPayload().toString(), UserCreateEvent.class);
        log.info("User Update Event received: {}", userCreateEvent.getMetadata().getEventId());
        keyCloakService.onUserUpdateEvent(userCreateEvent);
    }

}
