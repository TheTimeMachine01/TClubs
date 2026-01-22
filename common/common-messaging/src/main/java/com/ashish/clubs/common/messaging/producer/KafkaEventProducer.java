package com.ashish.clubs.common.messaging.producer;

import com.ashish.clubs.common.messaging.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Kafka Event Producer for publishing domain events across the system.
 * Wraps KafkaTemplate with reactive support.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventProducer {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    /**
     * Publish an event to a Kafka topic.
     * Returns a Mono that completes when the event is sent.
     *
     * @param topic The Kafka topic name
     * @param event The domain event to publish
     * @return Mono<Void> that completes when the event is sent
     */
    public Mono<Void> publishEvent(String topic, DomainEvent event) {
        return Mono.fromFuture(() ->
            kafkaTemplate.send(topic, event.getEventId(), event)
                    .thenApply(result -> {
                        log.info("Event published to topic {}: eventId={}, eventType={}",
                                topic, event.getEventId(), event.getEventType());
                        return (Void) null;
                    })
                    .exceptionally(ex -> {
                        log.error("Failed to publish event to topic {}: eventId={}",
                                topic, event.getEventId(), ex);
                        throw new RuntimeException("Event publishing failed", ex);
                    })
        ).onErrorResume(ex -> {
            log.error("Error publishing event to topic {}: eventId={}",
                    topic, event.getEventId(), ex);
            return Mono.error(ex);
        });
    }

    /**
     * Publish an event with a specific key.
     *
     * @param topic The Kafka topic name
     * @param key The message key for partitioning
     * @param event The domain event to publish
     * @return Mono<Void> that completes when the event is sent
     */
    public Mono<Void> publishEventWithKey(String topic, String key, DomainEvent event) {
        return Mono.fromFuture(() ->
            kafkaTemplate.send(topic, key, event)
                    .thenApply(result -> {
                        log.info("Event published to topic {} with key {}: eventId={}, eventType={}",
                                topic, key, event.getEventId(), event.getEventType());
                        return (Void) null;
                    })
                    .exceptionally(ex -> {
                        log.error("Failed to publish event to topic {} with key {}: eventId={}",
                                topic, key, event.getEventId(), ex);
                        throw new RuntimeException("Event publishing failed", ex);
                    })
        ).onErrorResume(ex -> {
            log.error("Error publishing event to topic {} with key {}: eventId={}",
                    topic, key, event.getEventId(), ex);
            return Mono.error(ex);
        });
    }
}


