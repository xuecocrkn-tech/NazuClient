package me.delta.client.core;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
    private final Map<Class<?>, List<ListenerEntry>> listeners = new HashMap<>();

    public <T> void register(Class<T> eventClass, EventListener<T> listener, int priority) {
        listeners.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>())
                .add(new ListenerEntry(listener, priority));
        listeners.get(eventClass).sort(Comparator.comparingInt(e -> e.priority));
    }

    public <T> void register(Class<T> eventClass, EventListener<T> listener) {
        register(eventClass, listener, 0);
    }

    public <T> void unregister(Class<T> eventClass, EventListener<T> listener) {
        List<ListenerEntry> entries = listeners.get(eventClass);
        if (entries != null) {
            entries.removeIf(e -> e.listener == listener);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T post(T event) {
        List<ListenerEntry> entries = listeners.get(event.getClass());
        if (entries != null) {
            for (ListenerEntry entry : entries) {
                if (event instanceof Cancellable && ((Cancellable) event).isCancelled()) {
                    break;
                }
                ((EventListener<T>) entry.listener).onEvent(event);
            }
        }
        return event;
    }

    public void clear() {
        listeners.clear();
    }

    private record ListenerEntry(EventListener<?> listener, int priority) {}
}
