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


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\SlotAccessor.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */