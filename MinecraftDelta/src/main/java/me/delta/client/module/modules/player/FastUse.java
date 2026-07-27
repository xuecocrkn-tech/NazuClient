package me.delta.client.module.modules.player;

import me.delta.client.module.Category;
import me.delta.client.module.Module;

public class FastUse extends Module {
    private final me.delta.client.settings.NumberSetting speed = createNumber("Speed", "Usage speed multiplier", 3.0, 1.0, 5.0, 1.0, "x");

    public FastUse() {
        super("FastUse", "Use items faster than normal", Category.PLAYER);
    }

    public int getModifiedItemUseCooldown(int originalCooldown) {
        return isEnabled() ? (int) (originalCooldown / speed.getValue()) : originalCooldown;
    }
}
