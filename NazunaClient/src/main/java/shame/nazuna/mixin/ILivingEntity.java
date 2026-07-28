package shame.nazuna.mixin;

import net.minecraft.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({LivingEntity.class})
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

