package org.arya.banking.auth.kafka;

import org.apache.avro.specific.SpecificRecord;
import org.arya.banking.common.config.KafkaConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
public class KafkaListenerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SpecificRecord> kafkaListenerContainerFactory(
            KafkaConfiguration kafkaConfiguration) {
        return kafkaConfiguration.kafkaListerFactory("auth-service-group");
    }
}
