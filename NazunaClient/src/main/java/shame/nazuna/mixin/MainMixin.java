package shame.nazuna.mixin;
 
 import net.minecraft.client.main.Main;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.astra;
 
 @Mixin({Main.class})
 public class MainMixin
 {
   @Inject(method = {"main"}, at = {@At("HEAD")})
   private static void onMain(String[] args, CallbackInfo ci) {
     if (NazunaClient.INSTANCE.isServer) {
       try {
         NazunaClient.INSTANCE.closeMinecraft();
       } catch (Exception e) {
         e.printStackTrace();
       } 
       NazunaClient.INSTANCE.isServer = false;
     } 
   }
 }

