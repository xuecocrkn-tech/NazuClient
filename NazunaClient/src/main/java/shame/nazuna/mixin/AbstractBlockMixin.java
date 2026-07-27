package shame.nazuna.mixin;
 
 import net.minecraft.class_1922;
 import net.minecraft.class_2338;
 import net.minecraft.class_259;
 import net.minecraft.class_265;
 import net.minecraft.class_2680;
 import net.minecraft.class_3726;
 import net.minecraft.class_4970;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
 import shame.nazuna.api.events.implement.EventBlockCollide;
 
 @Mixin({class_4970.class})
 public class AbstractBlockMixin
 {
   @Inject(method = {"method_9530"}, at = {@At("HEAD")}, cancellable = true)
   public void getOutlineShape(class_2680 state, class_1922 world, class_2338 pos, class_3726 context, CallbackInfoReturnable<class_265> cir) {
     EventBlockCollide eventBlockCollide = new EventBlockCollide(pos);
     eventBlockCollide.call();
     if (eventBlockCollide.isCancelled()) cir.setReturnValue(class_259.method_1073()); 
   }
   
   @Inject(method = {"method_9549"}, at = {@At("HEAD")}, cancellable = true)
   public void getCollisionShape(class_2680 state, class_1922 world, class_2338 pos, class_3726 context, CallbackInfoReturnable<class_265> cir) {
     EventBlockCollide eventBlockCollide = new EventBlockCollide(pos);
     eventBlockCollide.call();
     if (eventBlockCollide.isCancelled()) cir.setReturnValue(class_259.method_1073()); 
   }
   
   @Inject(method = {"method_9584"}, at = {@At("HEAD")}, cancellable = true)
   public void getRaycastShape(class_2680 state, class_1922 world, class_2338 pos, CallbackInfoReturnable<class_265> cir) {
     EventBlockCollide eventBlockCollide = new EventBlockCollide(pos);
     eventBlockCollide.call();
     if (eventBlockCollide.isCancelled()) cir.setReturnValue(class_259.method_1073()); 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\AbstractBlockMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */