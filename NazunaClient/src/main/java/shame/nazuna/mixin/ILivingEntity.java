package shame.nazuna.mixin;

import net.minecraft.class_1309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_1309.class})
public interface ILivingEntity {
  @Accessor("field_6273")
  int getLastAttackedTicks();
  
  @Accessor("field_6228")
  void setJumpingCooldown(int paramInt);
  
  @Accessor("field_6284")
  double getResolveYaw();
  
  @Accessor("field_6221")
  double getResolvePitch();
}


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\ILivingEntity.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */