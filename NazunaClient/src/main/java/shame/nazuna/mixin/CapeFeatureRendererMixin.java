package shame.nazuna.mixin;
 
 import net.minecraft.class_10055;
 import net.minecraft.class_1297;
 import net.minecraft.class_1657;
 import net.minecraft.class_4587;
 import net.minecraft.class_4597;
 import net.minecraft.class_972;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.impl.render.Chams;
 
 
 
 
 @Mixin({class_972.class})
 public class CapeFeatureRendererMixin
   implements QClient
 {
   @Inject(method = {"method_4177"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$hideCape(class_4587 matrices, class_4597 vertexConsumers, int light, class_10055 playerState, float limbAngle, float limbDistance, CallbackInfo ci) {
     if (ModuleClass.INSTANCE == null || mc.field_1687 == null) {
       return;
     }
     
     Chams chams = ModuleClass.chams;
     if (chams == null || !chams.isEnable()) {
       return;
     }
     
     class_1297 entity = mc.field_1687.method_8469(playerState.field_53528);
     if (entity instanceof class_1657) { class_1657 player = (class_1657)entity; if (chams.shouldHideItemsAndCape(player))
         ci.cancel();  }
   
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\CapeFeatureRendererMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */