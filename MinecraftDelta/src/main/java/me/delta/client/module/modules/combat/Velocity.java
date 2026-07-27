package me.delta.client.module.modules.combat;

import me.delta.client.module.Category;
import me.delta.client.module.Module;

public class Velocity extends Module {
    private final me.delta.client.settings.NumberSetting horizontal = createNumber("Horizontal", "Horizontal velocity %", 0.0, 0.0, 100.0, 5.0, "%");
    private final me.delta.client.settings.NumberSetting vertical = createNumber("Vertical", "Vertical velocity %", 0.0, 0.0, 100.0, 5.0, "%");

    public Velocity() {
        super("Velocity", "Modify knockback received from hits", Category.COMBAT);
    }

    /**
     * Called from mixin to modify velocity.
     * Returns 0 to cancel velocity entirely.
     */
    public double getHorizontalMultiplier() {
        return horizontal.getValue() / 100.0;
    }

    public double getVerticalMultiplier() {
        return vertical.getValue() / 100.0;
    }

    public boolean shouldCancel() {
        return horizontal.getValue() == 0 && vertical.getValue() == 0;
    }
}
