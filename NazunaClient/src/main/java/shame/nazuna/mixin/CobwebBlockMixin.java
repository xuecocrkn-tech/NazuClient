package shame.nazuna.mixin;
 
 import net.minecraft.class_1297;
 import net.minecraft.class_1937;
 import net.minecraft.class_2338;
 import net.minecraft.class_2560;
 import net.minecraft.class_2680;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 
 @Mixin({class_2560.class})
 public class CobwebBlockMixin {
   @Inject(method = {"method_9548"}, at = {@At("HEAD")}, cancellable = true)
   public void onEntityCollision(class_2680 state, class_1937 world, class_2338 pos, class_1297 entity, CallbackInfo ci) {
     if (ModuleClass.noWeb.isEnable()) if (ModuleClass.noWeb.web.is("Коллизия")) ci.cancel();  
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\CobwebBlockMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */