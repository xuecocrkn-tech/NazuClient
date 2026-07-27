package shame.nazuna.mixin;
 
 import net.minecraft.EntityRenderState;
 import net.minecraft.Text;
 import net.minecraft.MatrixStack;
 import net.minecraft.VertexConsumerProvider;
 import net.minecraft.EntityRenderer;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.impl.render.EntityESP;
 
 @Mixin({EntityRenderer.class})
 public abstract class EntityRendererMixin<S extends EntityRenderState>
 {
   @Inject(method = {"method_3926"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$renderLabelIfPresent(S state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
     if (ModuleClass.INSTANCE == null)
       return; 
     EntityESP esp = ModuleClass.entityESP;
     if (esp == null)
       return; 
     if (esp.shouldHideVanillaTags())
       ci.cancel(); 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\EntityRendererMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */