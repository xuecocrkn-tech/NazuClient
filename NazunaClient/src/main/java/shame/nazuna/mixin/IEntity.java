package shame.nazuna.mixin;

import net.minecraft.Entity;
import net.minecraft.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({Entity.class})
public interface IEntity {
  @Invoker("method_17835")
  Vec3d invokeAdjustMovementForCollisions(Vec3d paramclass_243);
}

