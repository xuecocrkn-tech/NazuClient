package shame.nazuna.mixin;
 
 import net.minecraft.class_5223;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.ModifyArg;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.impl.misc.NameProtect;
 
 
 
 
 
 
 
 
 
 @Mixin({class_5223.class})
 public class TextVisitFactoryMixin
 {
   @ModifyArg(method = {"method_27472"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/class_5223;method_27473(Ljava/lang/String;ILnet/minecraft/class_2583;Lnet/minecraft/class_2583;Lnet/minecraft/class_5224;)Z", ordinal = 0), index = 0)
   private static String astra$patchVisitedText(String text) {
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


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\TextVisitFactoryMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */