package me.delta.client.module.modules.movement;

import me.delta.client.module.Category;
import me.delta.client.module.Module;

public class Step extends Module {
    private final me.delta.client.settings.NumberSetting height = createNumber("Height", "Step height in blocks", 2.0, 1.0, 5.0, 0.5, " blocks");

    public Step() {
        super("Step", "Steps up blocks without jumping", Category.MOVEMENT);
    }

    public float getStepHeight() {
        return isEnabled() ? height.getValue().floatValue() : 0.6f;
    }
}
