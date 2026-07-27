package shame.nazuna.mixin;
 
 import net.minecraft.PlayerEntityRenderState;
 import net.minecraft.Entity;
 import net.minecraft.PlayerEntity;
 import net.minecraft.MatrixStack;
 import net.minecraft.VertexConsumerProvider;
 import net.minecraft.CapeFeatureRenderer;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.impl.render.Chams;
 
 
 
 
 @Mixin({CapeFeatureRenderer.class})
 public class CapeFeatureRendererMixin
   implements QClient
 {
   @Inject(method = {"method_4177"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$hideCape(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState playerState, float limbAngle, float limbDistance, CallbackInfo ci) {
     if (ModuleClass.INSTANCE == null || mc.field_1687 == null) {
       return;
     }
     
     Chams chams = ModuleClass.chams;
     if (chams == null || !chams.isEnable()) {
       return;
     }
     
     Entity entity = mc.field_1687.method_8469(playerState.field_53528);
     if (entity instanceof PlayerEntity) { PlayerEntity player = (PlayerEntity)entity; if (chams.shouldHideItemsAndCape(player))
         ci.cancel();  }
   
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\CapeFeatureRendererMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */