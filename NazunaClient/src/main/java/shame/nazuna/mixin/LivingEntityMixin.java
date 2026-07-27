package shame.nazuna.mixin;
 
 import net.minecraft.class_1309;
 import net.minecraft.class_310;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.impl.render.SwingAnimations;
 
 @Mixin({class_1309.class})
 public abstract class LivingEntityMixin
 {
   @Inject(method = {"method_6028"}, at = {@At("HEAD")}, cancellable = true)
   private void onGetHandSwingDuration(CallbackInfoReturnable<Integer> cir) {
     if (this != (class_310.method_1551()).field_1724) {
       return;
     }
     
     if (ModuleClass.INSTANCE == null) {
       return;
     }
     
     SwingAnimations tweaks = ModuleClass.swingAnimations;
     if (tweaks != null && tweaks.isEnable() && tweaks.smoothEnabled.isState())
       cir.setReturnValue(Integer.valueOf((int)tweaks.slowAnimationSpeed.get())); 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\LivingEntityMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */