package com.hmis.event.payload;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Payload chuẩn cho tất cả Kafka messages.
 */
@Data
@Builder
public class EventPayload {

    private String    eventType;
    private String    tenantId;
    private String    aggregateId;
    private Object    data;

    @Builder.Default
    private String    timestamp = LocalDateTime.now().toString();
}
