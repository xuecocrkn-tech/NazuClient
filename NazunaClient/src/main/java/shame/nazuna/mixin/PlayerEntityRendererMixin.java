package shame.nazuna.mixin;
 
 import net.minecraft.class_10055;
 import net.minecraft.class_1007;
 import net.minecraft.class_3883;
 import net.minecraft.class_3887;
 import net.minecraft.class_5617;
 import net.minecraft.class_591;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.client.modules.impl.render.SatelliteFeatureRenderer;
 
 @Mixin({class_1007.class})
 public abstract class PlayerEntityRendererMixin
 {
   @Inject(method = {"<init>"}, at = {@At("TAIL")})
   private void astra$addShoulderPetFeature(class_5617.class_5618 context, boolean slim, CallbackInfo ci) {
     class_3883<class_10055, class_591> rendererContext = (class_3883<class_10055, class_591>)this;
 
     
     ((LivingEntityRendererAccessor)this).astra$addFeature((class_3887<?, ?>)new SatelliteFeatureRenderer(rendererContext, context));
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\PlayerEntityRendererMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */