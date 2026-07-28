package shame.nazuna.mixin;
 
 import net.minecraft.LivingEntity;
 import net.minecraft.MinecraftClient;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.impl.render.SwingAnimations;
 
 @Mixin({LivingEntity.class})
 public abstract class LivingEntityMixin
 {
   @Inject(method = {"method_6028"}, at = {@At("HEAD")}, cancellable = true)
   private void onGetHandSwingDuration(CallbackInfoReturnable<Integer> cir) {
     if (this != (MinecraftClient.method_1551()).field_1724) {
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

