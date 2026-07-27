package shame.nazuna.mixin;
 
 import net.minecraft.class_638;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.Shadow;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.impl.render.WorldTweaks;
 
 @Mixin({class_638.class_5271.class})
 public class ClientWorldPropertiesMixin {
   @Shadow
   private long field_24439;
   
   @Inject(method = {"method_165"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$setTimeOfDay(long timeOfDay, CallbackInfo ci) {
     if (ModuleClass.INSTANCE == null)
       return; 
     WorldTweaks tweaks = ModuleClass.worldTweaks;
     if (tweaks == null || !tweaks.isTimeEnabled())
       return; 
     this.field_24439 = tweaks.getForcedTime();
     ci.cancel();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\ClientWorldPropertiesMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */