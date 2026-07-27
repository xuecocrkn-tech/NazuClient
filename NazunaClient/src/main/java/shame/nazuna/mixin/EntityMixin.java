package shame.nazuna.mixin;
 
 import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
 import net.minecraft.Entity;
 import net.minecraft.PlayerEntity;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.impl.player.NoPush;
 import shame.nazuna.client.modules.impl.render.SeeInvisibles;
 import shame.nazuna.client.modules.impl.render.ShaderEsp;
 
 @Mixin({Entity.class})
 public abstract class EntityMixin
   implements QClient {
   @ModifyExpressionValue(method = {"method_5784"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/Entity;method_65038()Z")})
   private boolean fixFallDistanceCalculation(boolean original) {
     if (this == mc.field_1724) {
       return false;
     }
     return original;
   }
   
   @Inject(method = {"method_5697"}, at = {@At("HEAD")}, cancellable = true)
   public void pushAwayFrom(CallbackInfo ci) {
     if (this != mc.field_1724 || ModuleClass.INSTANCE == null)
       return;  NoPush noPush = ModuleClass.noPush;
     if (noPush != null && noPush.isEnable() && noPush.getCollisionList().is("Игроки")) {
       ci.cancel();
     }
   }
   
   @Inject(method = {"method_5675"}, at = {@At("RETURN")}, cancellable = true)
   public void isPushedByFluids(CallbackInfoReturnable<Boolean> ci) {
     if (this != mc.field_1724 || ModuleClass.INSTANCE == null)
       return;  NoPush noPush = ModuleClass.noPush;
     if (noPush != null && noPush.isEnable() && noPush.getCollisionList().is("Вода")) {
       ci.setReturnValue(Boolean.valueOf(false));
     }
   }
   
   @Inject(method = {"method_22861"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$getTeamColorValue(CallbackInfoReturnable<Integer> cir) {
     if (ModuleClass.INSTANCE == null)
       return; 
     ShaderEsp shaderEsp = ModuleClass.shaderEsp;
     if (shaderEsp != null && shaderEsp.shouldOutline((Entity)this)) {
       cir.setReturnValue(Integer.valueOf(shaderEsp.getOutlineColor()));
     }
   }
   
   @Inject(method = {"method_5756"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$allowSeeInvisibles(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
     EntityMixin entityMixin = this; if (entityMixin instanceof PlayerEntity) { PlayerEntity target = (PlayerEntity)entityMixin; if (ModuleClass.INSTANCE != null) {
 
 
         
         SeeInvisibles seeInvisibles = ModuleClass.seeInvisibles;
         if (seeInvisibles != null && seeInvisibles.shouldRenderInvisible(target))
           cir.setReturnValue(Boolean.valueOf(false)); 
         return;
       }  }
   
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\EntityMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */