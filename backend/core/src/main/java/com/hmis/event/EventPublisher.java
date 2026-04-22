package com.hmis.event;

import com.hmis.event.payload.EventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Event Bus: publish domain events lên Kafka.
 * Topic naming: hmis.<tenantId>.<eventType>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final KafkaTemplate<String, EventPayload> kafkaTemplate;

    private static final String TOPIC_PREFIX = "hmis";

    public void publish(String eventType, UUID tenantId, UUID aggregateId, Object data) {
        String topic = String.format("%s.%s", TOPIC_PREFIX, eventType.toLowerCase());
        EventPayload payload = EventPayload.builder()
                .eventType(eventType)
                .tenantId(tenantId.toString())
                .aggregateId(aggregateId.toString())
                .data(data)
                .build();

        kafkaTemplate.send(topic, aggregateId.toString(), payload)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Published event {} to topic {}", eventType, topic);
                    } else {
                        log.error("Failed to publish event {} to {}: {}", eventType, topic, ex.getMessage());
                    }
                });
    }
}
