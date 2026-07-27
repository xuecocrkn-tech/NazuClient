package shame.nazuna.mixin;
 
 import net.minecraft.ItemStack;
 import net.minecraft.Items;
 import net.minecraft.GameRenderer;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.impl.render.Removals;
 
 @Mixin({GameRenderer.class})
 public class GameRendererMixin
 {
   @Inject(method = {"method_3189"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$hideTotemAnimation(ItemStack stack, CallbackInfo ci) {
     if (ModuleClass.INSTANCE == null || stack == null || !stack.method_31574(Items.field_8288)) {
       return;
     }
     
     Removals removals = ModuleClass.removals;
     if (removals != null && removals.isTotemAnimationDisabled())
       ci.cancel(); 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\GameRendererMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */