package me.delta.client.module.modules.movement;

import me.delta.client.module.Category;
import me.delta.client.module.Module;

public class Flight extends Module {
    private final me.delta.client.settings.ModeSetting mode = createMode("Mode", "Flight mode", "Velocity", "Vanilla", "Velocity", "Packet", "Creative");
    private final me.delta.client.settings.NumberSetting speed = createNumber("Speed", "Flight speed", 1.5, 0.1, 5.0, 0.1, "x");

    public Flight() {
        super("Flight", "Allows flying without creative mode", Category.MOVEMENT);
    }

    @Override
    protected void onDisable() {
        if (mc.player != null && !mc.player.getAbilities().creativeMode) {
            mc.player.getAbilities().flying = false;
            mc.player.getAbilities().allowFlying = false;
        }
    }

    public void onTick() {
        if (mc.player == null) return;

        switch (mode.getValue()) {
            case "Vanilla" -> {
                mc.player.getAbilities().allowFlying = true;
                mc.player.getAbilities().flying = true;
                mc.player.getAbilities().setFlySpeed((float) (0.05 * speed.getValue()));
            }
            case "Velocity" -> {
                double yaw = Math.toRadians(mc.player.getYaw());
                double forward = mc.player.forwardSpeed;
                double sideways = mc.player.sidewaysSpeed;

                double velX = (-Math.sin(yaw) * forward + Math.cos(yaw) * sideways) * speed.getValue();
                double velZ = (Math.cos(yaw) * forward + Math.sin(yaw) * sideways) * speed.getValue();

                mc.player.setVelocity(velX, 0.0, velZ);

                if (mc.options.jumpKey.isPressed()) {
                    mc.player.setVelocity(mc.player.getVelocity().add(0, speed.getValue(), 0));
                }
                if (mc.options.sneakKey.isPressed()) {
                    mc.player.setVelocity(mc.player.getVelocity().add(0, -speed.getValue(), 0));
                }
                mc.player.setOnGround(true);
            }
            case "Packet" -> {
                mc.player.getAbilities().allowFlying = true;
                mc.player.getAbilities().flying = true;
                mc.player.getAbilities().setFlySpeed((float) (0.05 * speed.getValue()));
            }
            case "Creative" -> {
                mc.player.getAbilities().allowFlying = true;
                mc.player.getAbilities().flying = true;
                mc.player.getAbilities().setFlySpeed((float) (0.05 * speed.getValue()));
            }
        }
    }
}
