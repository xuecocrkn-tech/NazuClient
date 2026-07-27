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
     if (astra.INSTANCE.isServer) {
       try {
         astra.INSTANCE.closeMinecraft();
       } catch (Exception e) {
         e.printStackTrace();
       } 
       astra.INSTANCE.isServer = false;
     } 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\MainMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */