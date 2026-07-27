package me.delta.client.core;

public interface Cancellable {
    boolean isCancelled();
    void setCancelled(boolean cancelled);
}
