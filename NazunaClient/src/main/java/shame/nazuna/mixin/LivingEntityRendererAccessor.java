package shame.nazuna.mixin;

import net.minecraft.LivingEntityRenderState;
import net.minecraft.FeatureRenderer;
import net.minecraft.MatrixStack;
import net.minecraft.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({LivingEntityRenderer.class})
public interface LivingEntityRendererAccessor {
  @Invoker("method_4046")
  boolean astra$addFeature(FeatureRenderer<?, ?> paramclass_3887);
  
  @Invoker("method_4058")
  void astra$setupTransforms(LivingEntityRenderState paramclass_10042, MatrixStack paramclass_4587, float paramFloat1, float paramFloat2);
  
  @Invoker("method_4042")
  void astra$scale(LivingEntityRenderState paramclass_10042, MatrixStack paramclass_4587);
}

