package me.delta.client.module.modules.combat;

import me.delta.client.module.Category;
import me.delta.client.module.Module;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotem extends Module {
    private final me.delta.client.settings.NumberSetting healthThreshold = createNumber("Health", "Health threshold to switch to totem", 10.0, 1.0, 20.0, 1.0, "❤");
    private final me.delta.client.settings.BooleanSetting fallBack = createBoolean("FallBack", "Switch to crystal/pearl if no totem", true);
    private final me.delta.client.settings.ModeSetting preferMode = createMode("Prefer", "What to prefer in offhand", "Totem", "Totem", "Crystal", "Gapple");

    private int tickCounter = 0;
    private static final int DELAY = 3;

    public AutoTotem() {
        super("AutoTotem", "Automatically places totems in offhand", Category.COMBAT);
    }

    @Override
    protected void onEnable() {
        tickCounter = 0;
    }

    public void onTick() {
        if (mc.player == null || mc.interactionManager == null) return;

        tickCounter++;
        if (tickCounter < DELAY) return;
        tickCounter = 0;

        ItemStack offhand = mc.player.getOffHandStack();

        // Check current offhand
        if (preferMode.getValue().equals("Totem") && offhand.isOf(Items.TOTEM_OF_UNDYING)) return;
        if (preferMode.getValue().equals("Crystal") && offhand.isOf(Items.END_CRYSTAL)) return;
        if (preferMode.getValue().equals("Gapple") && offhand.isOf(Items.ENCHANTED_GOLDEN_APPLE)) return;

        double health = mc.player.getHealth() + mc.player.getAbsorptionAmount();

        // If we're low, force totem
        boolean lowHealth = health <= healthThreshold.getValue() || mc.player.hasStatusEffect(StatusEffects.WITHER);

        String targetItem;
        if (lowHealth || preferMode.getValue().equals("Totem")) {
            targetItem = "Totem";
        } else {
            targetItem = preferMode.getValue();
        }

        int slot = findItemInHotbar(targetItem);
        if (slot == -1 && fallBack.getValue() && !targetItem.equals("Totem")) {
            // Try fallback
            slot = findItemInHotbar("Totem");
        }
        if (slot == -1) return;

        // Move to offhand
        mc.interactionManager.clickSlot(
                mc.player.currentScreenHandler.syncId,
                slot < 9 ? slot + 36 : slot,
                40,
                SlotActionType.SWAP,
                mc.player
        );
    }

    private int findItemInHotbar(String itemType) {
        return switch (itemType) {
            case "Totem" -> findItem(Items.TOTEM_OF_UNDYING);
            case "Crystal" -> findItem(Items.END_CRYSTAL);
            case "Gapple" -> findItem(Items.ENCHANTED_GOLDEN_APPLE);
            default -> -1;
        };
    }

    private int findItem(net.minecraft.item.Item item) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isOf(item)) return i;
        }
        return -1;
    }
}
