package shame.nazuna.mixin;

import net.minecraft.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({PlayerMoveC2SPacket.class})
public interface IPlayerMoveC2SPacketAccessor {
  @Accessor("field_12889")
  void setX(double paramDouble);
  
  @Accessor("field_12886")
  void setY(double paramDouble);
  
  @Accessor("field_12884")
  void setZ(double paramDouble);
  
  @Mutable
  @Accessor("field_52335")
  void setHorizontalCollision(boolean paramBoolean);
}


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\IPlayerMoveC2SPacketAccessor.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */