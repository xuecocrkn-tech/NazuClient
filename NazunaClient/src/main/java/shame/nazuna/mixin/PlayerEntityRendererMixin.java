package shame.nazuna.mixin;
 
 import net.minecraft.PlayerEntityRenderState;
 import net.minecraft.PlayerEntityRenderer;
 import net.minecraft.FeatureRendererContext;
 import net.minecraft.FeatureRenderer;
 import net.minecraft.EntityRendererFactory;
 import net.minecraft.PlayerEntityModel;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.client.modules.impl.render.SatelliteFeatureRenderer;
 
 @Mixin({PlayerEntityRenderer.class})
 public abstract class PlayerEntityRendererMixin
 {
   @Inject(method = {"<init>"}, at = {@At("TAIL")})
   private void astra$addShoulderPetFeature(EntityRendererFactory.class_5618 context, boolean slim, CallbackInfo ci) {
     FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> rendererContext = (FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel>)this;
 
     
     ((LivingEntityRendererAccessor)this).astra$addFeature((FeatureRenderer<?, ?>)new SatelliteFeatureRenderer(rendererContext, context));
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\PlayerEntityRendererMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */