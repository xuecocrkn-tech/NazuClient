package shame.nazuna.mixin;

import net.minecraft.PlayerInput;
import net.minecraft.PlayerInputC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({PlayerInputC2SPacket.class})
public interface IPlayerInputC2SPacketAccessor {
  @Mutable
  @Accessor("comp_3139")
  void setInput(PlayerInput paramclass_10185);
}

