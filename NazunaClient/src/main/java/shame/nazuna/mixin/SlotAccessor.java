package shame.nazuna.mixin;

import net.minecraft.Inventory;
import net.minecraft.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Slot.class})
public interface SlotAccessor {
  @Accessor("field_7871")
  Inventory astra$getInventory();
  
  @Accessor("field_7875")
  int astra$getIndex();
}

