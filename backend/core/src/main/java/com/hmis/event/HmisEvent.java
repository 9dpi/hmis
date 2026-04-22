package com.hmis.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class cho tất cả domain events trong HMIS.
 * Publish qua EventPublisher → Kafka topic.
 */
@Getter
public abstract class HmisEvent extends ApplicationEvent {

    private final String  eventType;
    private final UUID    tenantId;
    private final UUID    aggregateId;
    private final LocalDateTime occurredAt;

    protected HmisEvent(Object source,
                        String eventType,
                        UUID tenantId,
                        UUID aggregateId) {
        super(source);
        this.eventType   = eventType;
        this.tenantId    = tenantId;
        this.aggregateId = aggregateId;
        this.occurredAt  = LocalDateTime.now();
    }
}
