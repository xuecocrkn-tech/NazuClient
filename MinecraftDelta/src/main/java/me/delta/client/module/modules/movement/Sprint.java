package me.delta.client.module.modules.movement;

import me.delta.client.module.Category;
import me.delta.client.module.Module;

public class Sprint extends Module {
    private final me.delta.client.settings.ModeSetting mode = createMode("Mode", "Sprint mode", "Legit", "Legit", "Rage", "Omni");

    public Sprint() {
        super("Sprint", "Automatically sprints when possible", Category.MOVEMENT);
    }

    @Override
    protected void onDisable() {
        if (mc.player != null) mc.player.setSprinting(false);
    }

    public void onTick() {
        if (mc.player == null) return;

        switch (mode.getValue()) {
            case "Legit" -> {
                if (mc.player.forwardSpeed > 0 && !mc.player.horizontalCollision) {
                    mc.player.setSprinting(true);
                }
            }
            case "Rage" -> {
                if (mc.player.forwardSpeed > 0 || mc.player.sidewaysSpeed != 0) {
                    mc.player.setSprinting(true);
                }
            }
            case "Omni" -> {
                mc.player.setSprinting(true);
            }
        }
    }
}
