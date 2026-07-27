package me.delta.client.module.modules.player;

import me.delta.client.module.Category;
import me.delta.client.module.Module;

public class NoSlow extends Module {
    private final me.delta.client.settings.ModeSetting mode = createMode("Mode", "NoSlow mode", "Vanilla", "Vanilla", "NCP", "Strict");

    public NoSlow() {
        super("NoSlow", "Prevents slowdown from items and blocks", Category.PLAYER);
    }

    public boolean isActive() {
        return isEnabled();
    }

    public boolean shouldCancelSlowness() {
        return isEnabled();
    }
}
