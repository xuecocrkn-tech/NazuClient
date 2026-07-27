package me.delta.client.module.modules.combat;

import me.delta.client.module.Category;
import me.delta.client.module.Module;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Criticals extends Module {
    private final me.delta.client.settings.ModeSetting mode = createMode("Mode", "Criticals mode", "Packet", "Packet", "MiniJump", "Velocity");

    public Criticals() {
        super("Criticals", "Always deal critical hits", Category.COMBAT);
    }

    public void onAttack() {
        if (mc.player == null || !mc.player.isOnGround()) return;
        if (mc.player.isInLava() || mc.player.isTouchingWater()) return;
        if (mc.player.isGliding()) return;

        switch (mode.getValue()) {
            case "Packet" -> {
                double posX = mc.player.getX();
                double posY = mc.player.getY();
                double posZ = mc.player.getZ();

                // 1.21.4 PositionAndOnGround: (x, y, z, onGround, changeLook)
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        posX, posY + 0.0625, posZ, false, false
                ));
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        posX, posY + 0.0, posZ, false, false
                ));
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        posX, posY + 1.1E-5, posZ, false, false
                ));
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        posX, posY + 0.0, posZ, false, false
                ));
            }
            case "MiniJump" -> {
                mc.player.addVelocity(0, 0.11, 0);
                mc.player.setOnGround(false);
            }
            case "Velocity" -> {
                mc.player.setVelocity(mc.player.getVelocity().add(0, 0.1, 0));
                mc.player.setOnGround(false);
            }
        }
    }
}
