package me.delta.client.module.modules.misc;

import me.delta.client.module.Category;
import me.delta.client.module.Module;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.util.math.BlockPos;

public class AutoTool extends Module {
    private final me.delta.client.settings.BooleanSetting switchBack = createBoolean("Switch Back", "Switch back to previous slot", true);
    private int previousSlot = -1;

    public AutoTool() {
        super("AutoTool", "Automatically switches to the best tool", Category.MISC);
    }

    @Override
    protected void onDisable() {
        previousSlot = -1;
    }

    public void onBlockBreakStart(BlockPos pos) {
        if (mc.player == null || mc.world == null) return;
        if (!isEnabled()) return;

        BlockState state = mc.world.getBlockState(pos);
        if (state.isAir()) return;

        previousSlot = mc.player.getInventory().selectedSlot;
        int bestSlot = findBestToolSlot(state);

        if (bestSlot != -1 && bestSlot != mc.player.getInventory().selectedSlot) {
            mc.player.getInventory().selectedSlot = bestSlot;
        }
    }

    private int findBestToolSlot(BlockState state) {
        int bestSlot = -1;
        float bestSpeed = 0;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            float speed = stack.getMiningSpeedMultiplier(state);

            if (stack.getItem() instanceof MiningToolItem) {
                speed += 1.0f;
            }

            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        return bestSlot;
    }
}
