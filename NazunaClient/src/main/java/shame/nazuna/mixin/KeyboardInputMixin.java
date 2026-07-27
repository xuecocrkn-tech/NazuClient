package shame.nazuna.mixin;
 
 import net.minecraft.PlayerInput;
 import net.minecraft.MinecraftClient;
 import net.minecraft.KeyboardInput;
 import net.minecraft.Input;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.events.EventInvoker;
 import shame.nazuna.api.events.implement.EventMoveInput;
 
 @Mixin({KeyboardInput.class})
 public abstract class KeyboardInputMixin extends Input {
   private static final MinecraftClient mc = MinecraftClient.method_1551();
   
   @Inject(method = {"method_3129"}, at = {@At("TAIL")})
   private void onTickTail(CallbackInfo ci) {
     if (!EventInvoker.hasListeners(EventMoveInput.class)) {
       return;
     }
 
 
 
 
     
     EventMoveInput eventInput = new EventMoveInput(this.field_3905, this.field_3907, this.field_54155.comp_3163(), this.field_54155.comp_3164());
     
     eventInput.call();
     
     float forward = eventInput.getForward();
     float strafe = eventInput.getStrafe();
     
     this
 
 
 
 
 
       
       .field_54155 = new PlayerInput((forward > 0.0F), (forward < 0.0F), (strafe > 0.0F), (strafe < 0.0F), eventInput.isJump(), eventInput.isSneak(), this.field_54155.comp_3165());
     
     this.field_3905 = forward;
     this.field_3907 = strafe;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\KeyboardInputMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */