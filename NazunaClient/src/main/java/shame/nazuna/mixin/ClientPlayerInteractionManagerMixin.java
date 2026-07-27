package shame.nazuna.mixin;
 
 import net.minecraft.Entity;
 import net.minecraft.PlayerEntity;
 import net.minecraft.ClientPlayerInteractionManager;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.events.Event;
 import shame.nazuna.api.events.EventInvoker;
 import shame.nazuna.api.events.implement.EventAttackEntity;
 
 @Mixin({ClientPlayerInteractionManager.class})
 public abstract class ClientPlayerInteractionManagerMixin {
   @Inject(method = {"method_2918"}, at = {@At("HEAD")}, cancellable = true)
   public void attackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
     try {
       if (player != null && target != null) {
         EventAttackEntity event = new EventAttackEntity(player, target);
         EventInvoker.invoke((Event)event);
         if (event.isCancelled()) ci.cancel(); 
       } 
     } catch (Exception exception) {}
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\ClientPlayerInteractionManagerMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */