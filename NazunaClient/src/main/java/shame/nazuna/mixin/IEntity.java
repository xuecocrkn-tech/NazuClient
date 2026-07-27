package shame.nazuna.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_1297.class})
public interface IEntity {
  @Invoker("method_17835")
  class_243 invokeAdjustMovementForCollisions(class_243 paramclass_243);
}


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\IEntity.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */