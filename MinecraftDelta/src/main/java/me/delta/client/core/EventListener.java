package me.delta.client.core;

@FunctionalInterface
public interface EventListener<T> {
    void onEvent(T event);
}
