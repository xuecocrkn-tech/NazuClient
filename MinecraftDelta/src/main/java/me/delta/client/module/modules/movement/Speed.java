package me.delta.client.module.modules.movement;

import me.delta.client.module.Category;
import me.delta.client.module.Module;

public class Speed extends Module {
    private final me.delta.client.settings.ModeSetting mode = createMode("Mode", "Speed mode", "Strafe", "Strafe", "YPort", "NCPHop", "Ground");
    private final me.delta.client.settings.NumberSetting speed = createNumber("Speed", "Movement speed multiplier", 1.5, 0.5, 5.0, 0.1, "x");

    private int tickCounter = 0;
    private boolean jumpStage = false;

    public Speed() {
        super("Speed", "Increases movement speed", Category.MOVEMENT);
    }

    @Override
    protected void onDisable() {
        tickCounter = 0;
        jumpStage = false;
    }

    public void onTick() {
        if (mc.player == null || mc.player.isUsingItem()) return;
        if (mc.player.horizontalCollision) return;

        switch (mode.getValue()) {
            case "Strafe" -> doStrafe();
            case "YPort" -> doYPort();
            case "NCPHop" -> doNCPHop();
            case "Ground" -> doGround();
        }
    }

    private void doStrafe() {
        if (!mc.player.isOnGround()) return;
        if (mc.player.forwardSpeed == 0 && mc.player.sidewaysSpeed == 0) return;

        mc.player.setVelocity(
                mc.player.getVelocity().x * speed.getValue(),
                mc.player.getVelocity().y,
                mc.player.getVelocity().z * speed.getValue()
        );
    }

    private void doYPort() {
        tickCounter++;
        if (!mc.player.isOnGround()) return;

        if (tickCounter >= 4) {
            mc.player.jump();
            tickCounter = 0;
        }
    }

    private void doNCPHop() {
        if (mc.player.forwardSpeed == 0 && mc.player.sidewaysSpeed == 0) return;

        if (mc.player.isOnGround()) {
            mc.player.jump();
            mc.player.setVelocity(
                    mc.player.getVelocity().x * speed.getValue(),
                    mc.player.getVelocity().y,
                    mc.player.getVelocity().z * speed.getValue()
            );
        }
    }

    private void doGround() {
        if (!mc.player.isOnGround()) return;
        mc.player.setVelocity(
                mc.player.getVelocity().x * speed.getValue(),
                mc.player.getVelocity().y,
                mc.player.getVelocity().z * speed.getValue()
        );
    }
}
