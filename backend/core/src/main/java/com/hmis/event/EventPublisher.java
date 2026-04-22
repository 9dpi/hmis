package com.hmis.event;

import com.hmis.event.payload.EventPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Event Bus: publish domain events lên Kafka (chỉ hoạt động khi Kafka được cấu hình).
 * Ở chế độ local (H2), bean này vẫn tồn tại nhưng không làm gì.
 */
@Component
@Slf4j
public class EventPublisher {

    @Autowired(required = false)
    private KafkaTemplate<String, EventPayload> kafkaTemplate;

    private static final String TOPIC_PREFIX = "hmis";

    public void publish(String eventType, UUID tenantId, UUID aggregateId, Object data) {
        if (kafkaTemplate == null) {
            log.debug("[EventPublisher] Kafka unavailable – skipped event: {} for tenant {}",
                    eventType, tenantId);
            return;
        }

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
                        log.error("Failed to publish event {}: {}", eventType, ex.getMessage());
                    }
                });
    }
}
