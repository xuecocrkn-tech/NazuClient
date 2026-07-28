package shame.nazuna.mixin;
 
 import net.minecraft.TextVisitFactory;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.ModifyArg;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.impl.misc.NameProtect;
 
 
 
 
 
 
 
 
 
 @Mixin({TextVisitFactory.class})
 public class TextVisitFactoryMixin
 {
   @ModifyArg(method = {"method_27472"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/TextVisitFactory;method_27473(Ljava/lang/String;ILnet/minecraft/Style;Lnet/minecraft/Style;Lnet/minecraft/CharacterVisitor;)Z", ordinal = 0), index = 0)
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

