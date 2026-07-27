package me.delta.client.module.modules.player;

import me.delta.client.module.Category;
import me.delta.client.module.Module;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Scaffold extends Module {
    private final me.delta.client.settings.ModeSetting placeMode = createMode("Place Mode", "Block placement mode", "Normal", "Normal", "Expand", "Sprint");
    private final me.delta.client.settings.BooleanSetting autoSwitch = createBoolean("Auto Switch", "Auto-switch to blocks", true);
    private final me.delta.client.settings.BooleanSetting tower = createBoolean("Tower", "Hold shift to tower up", true);

    private int lastSlot = -1;
    private boolean switched = false;

    public Scaffold() {
        super("Scaffold", "Automatically places blocks beneath you", Category.PLAYER);
    }

    @Override
    protected void onDisable() {
        if (switched && lastSlot >= 0 && mc.player != null) {
            mc.player.getInventory().selectedSlot = lastSlot;
        }
        switched = false;
        lastSlot = -1;
    }

    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        // Tower mode
        if (tower.getValue() && mc.options.sneakKey.isPressed()) {
            mc.player.setVelocity(mc.player.getVelocity().x, 0.42, mc.player.getVelocity().z);
            return;
        }

        BlockPos pos = findTargetPos();
        if (pos == null) return;

        int blockSlot = findBlockSlot();
        if (blockSlot == -1) return;

        // Save and switch slot
        if (autoSwitch.getValue()) {
            lastSlot = mc.player.getInventory().selectedSlot;
            mc.player.getInventory().selectedSlot = blockSlot;
            switched = true;
        }

        // Place the block
        BlockPos placePos = pos;
        Direction facing = Direction.UP;
        Vec3d hitPos = Vec3d.ofCenter(placePos);

        // Find adjacent block to click on
        BlockPos neighborPos = findNeighborPos(placePos);
        if (neighborPos != null) {
            Direction dir = Direction.fromVector(
                    placePos.getX() - neighborPos.getX(),
                    placePos.getY() - neighborPos.getY(),
                    placePos.getZ() - neighborPos.getZ()
            );
            if (dir != null) {
                BlockHitResult hit = new BlockHitResult(
                        Vec3d.ofCenter(neighborPos).add(
                                dir.getOffsetX() * 0.5,
                                dir.getOffsetY() * 0.5,
                                dir.getOffsetZ() * 0.5
                        ),
                        dir,
                        neighborPos,
                        false
                );
                try {
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
                    mc.player.swingHand(Hand.MAIN_HAND);
                } catch (Exception ignored) {}
            }
        }
    }

    private BlockPos findTargetPos() {
        Vec3d pos = mc.player.getPos();
        BlockPos below = BlockPos.ofFloored(pos.x, pos.y - 1, pos.z);

        // Check if there's already a block below
        if (!mc.world.getBlockState(below).isReplaceable()) return null;

        return below;
    }

    private BlockPos findNeighborPos(BlockPos target) {
        Direction[] dirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.DOWN};

        for (Direction dir : dirs) {
            BlockPos neighbor = target.offset(dir);
            BlockState state = mc.world.getBlockState(neighbor);
            if (!state.isReplaceable() && state.isFullCube(mc.world, neighbor)) {
                return neighbor;
            }
        }
        return null;
    }

    private int findBlockSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem) {
                return i;
            }
        }
        // Check offhand
        if (mc.player.getOffHandStack().getItem() instanceof BlockItem) {
            return mc.player.getInventory().selectedSlot; // Return current, since we use offhand
        }
        return -1;
    }
}
