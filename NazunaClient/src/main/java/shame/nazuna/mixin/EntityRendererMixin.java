package shame.nazuna.mixin;
 
 import net.minecraft.class_10017;
 import net.minecraft.class_2561;
 import net.minecraft.class_4587;
 import net.minecraft.class_4597;
 import net.minecraft.class_897;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.impl.render.EntityESP;
 
 @Mixin({class_897.class})
 public abstract class EntityRendererMixin<S extends class_10017>
 {
   @Inject(method = {"method_3926"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$renderLabelIfPresent(S state, class_2561 text, class_4587 matrices, class_4597 vertexConsumers, int light, CallbackInfo ci) {
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