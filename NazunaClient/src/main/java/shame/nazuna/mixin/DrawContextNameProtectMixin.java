package shame.nazuna.mixin;
 
 import net.minecraft.class_332;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.ModifyVariable;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.impl.misc.NameProtect;
 
 
 
 
 
 
 @Mixin({class_332.class})
 public class DrawContextNameProtectMixin
 {
   @ModifyVariable(method = {"method_25303"}, at = @At("HEAD"), argsOnly = true, ordinal = 0)
   private String astra$patchStringShadow(String text) {
     return patch(text);
   }
 
 
 
 
 
   
   @ModifyVariable(method = {"method_51433"}, at = @At("HEAD"), argsOnly = true, ordinal = 0)
   private String astra$patchString(String text) {
     return patch(text);
   }
   
   private String patch(String text) {
     if (ModuleClass.INSTANCE == null) {
       return text;
     }
     
     NameProtect nameProtect = ModuleClass.nameProtect;
     if (nameProtect == null || !nameProtect.isEnable()) {
       return text;
     }
     
     return nameProtect.patchIncomingText(text);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\DrawContextNameProtectMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */