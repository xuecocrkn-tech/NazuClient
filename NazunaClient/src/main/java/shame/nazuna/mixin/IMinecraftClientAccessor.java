package shame.nazuna.mixin;

import net.minecraft.MinecraftClient;
import net.minecraft.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({MinecraftClient.class})
public interface IMinecraftClientAccessor {
  @Mutable
  @Accessor("field_1726")
  void setSession(Session paramclass_320);
  
  @Mutable
  @Accessor("field_1752")
  void setItemUseCooldown(int paramInt);
}

