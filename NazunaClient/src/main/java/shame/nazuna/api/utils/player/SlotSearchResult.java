package shame.nazuna.api.utils.player;

import net.minecraft.ItemStack;
import net.minecraft.Items;
import shame.nazuna.api.QClient;
import org.jetbrains.annotations.NotNull;

public final class SlotSearchResult implements QClient {
    private final int slot;
    private final boolean found;
    private final ItemStack stack;

    private static final SlotSearchResult NOT_FOUND_RESULT = new SlotSearchResult(-1, false, ItemStack.EMPTY);

    public SlotSearchResult(int slot, boolean found, ItemStack stack) {
        this.slot = slot;
        this.found = found;
        this.stack = stack;
    }

    public int slot() { return slot; }
    public boolean found() { return found; }
    public ItemStack stack() { return stack; }

    public static SlotSearchResult notFound() {
        return NOT_FOUND_RESULT;
    }

    @NotNull
    public static SlotSearchResult inOffhand(ItemStack stack) {
        return new SlotSearchResult(999, true, stack);
    }

    public boolean isHolding() {
        if (mc.field_1724 == null) return false;
        return (isOffhand() || (mc.field_1724.method_31548()).field_7545 == this.slot);
    }

    public boolean isOffhand() {
        return (this.slot == 999);
    }

    public boolean isInHotBar() {
        return (this.slot >= 0 && this.slot < 9);
    }

    public void switchTo() {
        if (this.found && isInHotBar()) {
            HotbarUtil.switchTo(this.slot);
        }
    }

    public void switchToSilent() {
        if (this.found && isInHotBar())
            HotbarUtil.switchToSilent(this.slot);
    }
}
