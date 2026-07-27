package me.delta.client.module.modules.movement;

import me.delta.client.module.Category;
import me.delta.client.module.Module;

public class NoFall extends Module {
    private final me.delta.client.settings.ModeSetting mode = createMode("Mode", "NoFall mode", "Packet", "Packet", "Bucket", "AirPlace");

    public NoFall() {
        super("NoFall", "Prevents fall damage", Category.MOVEMENT);
    }

    public boolean shouldCancelFallDamage() {
        return isEnabled();
    }

    public String getCurrentMode() {
        return mode.getValue();
    }
}
