package shame.nazuna.mixin;
 
 import net.minecraft.class_10017;
 import net.minecraft.class_4538;
 import net.minecraft.class_4587;
 import net.minecraft.class_4597;
 import net.minecraft.class_898;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.impl.render.Removals;
 
 @Mixin({class_898.class})
 public class EntityRenderDispatcherMixin
 {
   @Inject(method = {"method_23166"}, at = {@At("HEAD")}, cancellable = true)
   private static void astra$renderShadow(class_4587 matrices, class_4597 vertexConsumers, class_10017 renderState, float opacity, float tickDelta, class_4538 world, float radius, CallbackInfo ci) {
     if (ModuleClass.INSTANCE == null)
       return; 
     Removals removals = ModuleClass.removals;
     if (removals != null && removals.isEnabled("Тени"))
       ci.cancel(); 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\EntityRenderDispatcherMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */