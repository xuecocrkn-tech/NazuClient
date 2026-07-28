package shame.nazuna.mixin;
 
 import net.minecraft.Keyboard;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.events.implement.EventChunkReload;
 import shame.nazuna.api.utils.input.KeyBoardUtils;
 
 @Mixin({Keyboard.class})
 public class KeyboardMixin
   implements QClient {
   @Inject(method = {"method_1466"}, at = {@At("HEAD")})
   public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
     if (mc.field_1755 == null) KeyBoardUtils.call(key, action); 
   }
   
   @Inject(method = {"method_1468"}, at = {@At("RETURN")})
   private void processF3(int key, CallbackInfoReturnable<Boolean> cir) {
     if (key == 65 && ((Boolean)cir.getReturnValue()).booleanValue())
       (new EventChunkReload()).call(); 
   }
 }

