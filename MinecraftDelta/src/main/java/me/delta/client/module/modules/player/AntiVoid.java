package me.delta.client.module.modules.player;

import me.delta.client.module.Category;
import me.delta.client.module.Module;

public class AntiVoid extends Module {
    private final me.delta.client.settings.ModeSetting mode = createMode("Mode", "AntiVoid action", "Motion", "Motion", "Jump", "Flag");
    private final me.delta.client.settings.NumberSetting triggerHeight = createNumber("Trigger Y", "Y level to trigger", 0.0, -64.0, 64.0, 1.0);

    public AntiVoid() {
        super("AntiVoid", "Prevents falling into the void", Category.PLAYER);
    }

    public void onTick() {
        if (mc.player == null) return;
        if (mc.player.getY() > triggerHeight.getValue()) return;
        if (mc.player.isOnGround()) return;

        switch (mode.getValue()) {
            case "Motion" -> {
                if (mc.player.getVelocity().y < 0) {
                    mc.player.setVelocity(
                            mc.player.getVelocity().x,
                            0.42,
                            mc.player.getVelocity().z
                    );
                    mc.player.setOnGround(true);
                }
            }
            case "Jump" -> {
                mc.player.jump();
            }
            case "Flag" -> {
                mc.player.setPosition(
                        mc.player.getX(),
                        triggerHeight.getValue() + 1,
                        mc.player.getZ()
                );
                mc.player.setOnGround(true);
            }
        }
    }
}
