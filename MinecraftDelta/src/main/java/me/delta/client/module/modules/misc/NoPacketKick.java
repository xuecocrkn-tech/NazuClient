package me.delta.client.module.modules.misc;

import me.delta.client.module.Category;
import me.delta.client.module.Module;

public class NoPacketKick extends Module {
    private final me.delta.client.settings.ModeSetting mode = createMode("Mode", "Anti-kick mode", "Packet", "Packet", "Position", "Motion");

    public NoPacketKick() {
        super("NoPacketKick", "Prevents being kicked for packet timeout", Category.MISC);
    }

    public boolean shouldSpoofPacket() {
        return isEnabled();
    }
}
