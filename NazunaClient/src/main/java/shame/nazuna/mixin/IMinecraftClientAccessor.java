package shame.nazuna.mixin;

import net.minecraft.class_310;
import net.minecraft.class_320;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_310.class})
public interface IMinecraftClientAccessor {
  @Mutable
  @Accessor("field_1726")
  void setSession(class_320 paramclass_320);
  
  @Mutable
  @Accessor("field_1752")
  void setItemUseCooldown(int paramInt);
}


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\IMinecraftClientAccessor.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */