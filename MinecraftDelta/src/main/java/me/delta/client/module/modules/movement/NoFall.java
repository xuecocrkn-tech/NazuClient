package me.delta.client.module.modules.movement;

import me.delta.client.module.Category;
import me.delta.client.module.Module;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoFall extends Module {
    private final me.delta.client.settings.ModeSetting mode = createMode("Mode", "NoFall mode", "Packet", "Packet", "Bucket", "AirPlace");

    public NoFall() {
        super("NoFall", "Prevents fall damage", Category.MOVEMENT);
    }

    public void onTick() {
        if (mc.player == null) return;

        double fallDistance = mc.player.fallDistance;

        if (fallDistance > 3.0f && "Packet".equals(mode.getValue())) {
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                    mc.player.getX(),
                    mc.player.getY(),
                    mc.player.getZ(),
                    true,   // onGround = true
                    false   // changeLook = false
            ));
            mc.player.fallDistance = 0;
        }
    }

    public String getCurrentMode() {
        return mode.getValue();
    }
}
