package shame.nazuna.mixin;
 import com.mojang.authlib.GameProfile;
 import net.minecraft.class_1313;
 import net.minecraft.class_1657;
 import net.minecraft.class_1937;
 import net.minecraft.class_2338;
 import net.minecraft.class_243;
 import net.minecraft.class_2596;
 import net.minecraft.class_304;
 import net.minecraft.class_746;
 import org.jetbrains.annotations.NotNull;
 import org.spongepowered.asm.mixin.Final;
 import org.spongepowered.asm.mixin.Shadow;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.Redirect;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.events.EventInvoker;
 import shame.nazuna.api.events.implement.EventCloseInv;
 import shame.nazuna.api.events.implement.EventMove;
 import shame.nazuna.api.events.implement.EventSlowWalking;
 import shame.nazuna.api.events.implement.EventSprint;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.events.implement.EventUpdatePost;
 import shame.nazuna.api.storages.implement.RotationStorage;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.player.ViaProtocolUtils;
 import shame.nazuna.client.modules.impl.combat.Aura;
 
 @Mixin({class_746.class})
 public abstract class ClientPlayerEntityMixin extends class_1657 implements QClient {
   public ClientPlayerEntityMixin(class_1937 world, class_2338 pos, float yaw, GameProfile gameProfile) {
     super(world, pos, yaw, gameProfile);
   }
   
   @Shadow
   @Final
   public class_634 field_3944;
   
   @Shadow
   public abstract void method_3137();
   
   @Inject(method = {"method_5773"}, at = {@At(value = "HEAD", target = "Lnet/minecraft/class_742;method_5773()V")})
   private void onTick(CallbackInfo ci) {
     if (EventInvoker.hasListeners(EventUpdate.class)) {
       (new EventUpdate()).call();
     }
   }
   
   @Inject(method = {"method_5773"}, at = {@At(value = "TAIL", target = "Lnet/minecraft/class_742;method_5773()V")})
   private void onTickPost(CallbackInfo ci) {
     if (EventInvoker.hasListeners(EventUpdatePost.class)) {
       (new EventUpdatePost()).call();
     }
     
     if (shouldSyncRotation()) {
       this.field_6241 = method_36454();
       this.field_6259 = method_36454();
       this.field_6283 = method_36454();
       this.field_6220 = method_36454();
     } 
   }
   
   @Unique
   private boolean shouldSyncRotation() {
     return (ModuleClass.aura.isEnable() && Aura.clientLook
       .isState() && RotationStorage.instance
       .isRotating());
   }
 
 
 
 
   
   @Redirect(method = {"method_6007"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/class_304;method_1434()Z", ordinal = 1), require = 0)
   private boolean onSprintKeyPressed(class_304 instance) {
     if (ViaProtocolUtils.isTargetProtocolBelowOneNineteen() && (this.field_5976 || this.field_34927)) {
       return false;
     }
     
     EventSprint event = new EventSprint();
     event.call();
     if (event.isCancelled()) {
       return false;
     }
     return instance.method_1434();
   }
 
 
 
 
 
 
 
   
   @Redirect(method = {"method_6007"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/class_746;method_6115()Z"), require = 0)
   private boolean onSlowDownRedirect(class_746 player) {
     if (player.method_6115()) {
       EventSlowWalking event = new EventSlowWalking();
       event.call();
       return (player.method_6115() && player.method_5854() == null && !event.isCancelled());
     } 
     return (player.method_6115() && player.method_5854() == null);
   }
   
   @Inject(method = {"method_30673"}, at = {@At("HEAD")}, cancellable = true)
   public void pushOutOfBlocks(double x, double z, CallbackInfo ci) {
     if (ModuleClass.noPush.isEnable() && ModuleClass.noPush.getCollisionList().is("Блоки")) {
       ci.cancel();
     }
   }
   
   @Inject(method = {"method_5784"}, at = {@At("HEAD")}, cancellable = true)
   private void onMoveHook(class_1313 movementType, class_243 movement, @NotNull CallbackInfo ci) {
     EventMove event = new EventMove(movement);
     event.call();
     
     if (!event.isCancelled() && event.getMovePos().equals(movement)) {
       return;
     }
     
     if (event.isCancelled()) {
       ci.cancel();
       
       return;
     } 
     double d = method_23317();
     double e = method_23321();
     method_5784(movementType, event.getMovePos());
     float f = (float)Math.sqrt(Math.pow(method_23317() - d, 2.0D) + Math.pow(method_23321() - e, 2.0D));
     method_48565(f);
     ci.cancel();
   }
   
   @Inject(method = {"method_7346"}, at = {@At("HEAD")}, cancellable = true)
   private void onCloseHandledScreen(CallbackInfo ci) {
     int syncId = this.field_7512.field_7763;
     EventCloseInv event = new EventCloseInv(syncId);
     event.call();
     if (!event.isCancelled()) {
       this.field_3944.method_52787((class_2596)new class_2815(syncId));
     }
     method_3137();
     ci.cancel();
   }
   
   @Inject(method = {"method_7290"}, at = {@At("HEAD")}, cancellable = true)
   private void onDropSelectedItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
     if (ModuleClass.lockSlot != null && ModuleClass.lockSlot.isCurrentSlotLockedForDrop())
       cir.setReturnValue(Boolean.valueOf(false)); 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\ClientPlayerEntityMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */