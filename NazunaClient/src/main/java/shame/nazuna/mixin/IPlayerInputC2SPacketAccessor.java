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


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\IPlayerInputC2SPacketAccessor.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */