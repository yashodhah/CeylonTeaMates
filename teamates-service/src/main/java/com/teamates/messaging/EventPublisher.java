package com.teamates.messaging;


public interface EventPublisher<T> {
    void publishEvent(T event);
}
