package shame.nazuna.mixin;

import net.minecraft.class_10042;
import net.minecraft.class_3887;
import net.minecraft.class_4587;
import net.minecraft.class_922;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_922.class})
public interface LivingEntityRendererAccessor {
  @Invoker("method_4046")
  boolean astra$addFeature(class_3887<?, ?> paramclass_3887);
  
  @Invoker("method_4058")
  void astra$setupTransforms(class_10042 paramclass_10042, class_4587 paramclass_4587, float paramFloat1, float paramFloat2);
  
  @Invoker("method_4042")
  void astra$scale(class_10042 paramclass_10042, class_4587 paramclass_4587);
}


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\LivingEntityRendererAccessor.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */