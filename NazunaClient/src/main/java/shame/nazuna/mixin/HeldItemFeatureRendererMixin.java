package shame.nazuna.mixin;
 
 import net.minecraft.EntityRenderState;
 import net.minecraft.PlayerEntityRenderState;
 import net.minecraft.Entity;
 import net.minecraft.PlayerEntity;
 import net.minecraft.MatrixStack;
 import net.minecraft.VertexConsumerProvider;
 import net.minecraft.HeldItemFeatureRenderer;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.impl.render.Chams;
 
 
 
 
 @Mixin({HeldItemFeatureRenderer.class})
 public class HeldItemFeatureRendererMixin
   implements QClient
 {
   @Inject(method = {"method_4199"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$hideHeldItems(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityRenderState state, float limbAngle, float limbDistance, CallbackInfo ci) {
     if (state instanceof PlayerEntityRenderState) { PlayerEntityRenderState playerState = (PlayerEntityRenderState)state; if (ModuleClass.INSTANCE != null && mc.field_1687 != null) {
 
 
         
         Chams chams = ModuleClass.chams;
         if (chams == null || !chams.isEnable()) {
           return;
         }
         
         Entity entity = mc.field_1687.method_8469(playerState.field_53528);
         if (entity instanceof PlayerEntity) { PlayerEntity player = (PlayerEntity)entity; if (chams.shouldHideItemsAndCape(player))
             ci.cancel();  }
         
         return;
       }  }
   
   }
 }

