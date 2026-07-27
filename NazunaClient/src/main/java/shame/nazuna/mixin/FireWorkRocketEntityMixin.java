package shame.nazuna.mixin;
 
 import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
 import net.minecraft.EntityType;
 import net.minecraft.LivingEntity;
 import net.minecraft.FireworkRocketEntity;
 import net.minecraft.ProjectileEntity;
 import net.minecraft.World;
 import net.minecraft.Vec2f;
 import net.minecraft.Vec3d;
 import net.minecraft.MinecraftClient;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.Shadow;
 import org.spongepowered.asm.mixin.Unique;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.Redirect;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.events.implement.EventFireWork;
 import shame.nazuna.api.utils.player.BoostUtils;
 import shame.nazuna.client.modules.impl.movement.ElytraBoost;
 
 @Mixin({FireworkRocketEntity.class})
 public abstract class FireWorkRocketEntityMixin
   extends ProjectileEntity
 {
   @Unique
   private Vec3d rotation;
   @Shadow
   private LivingEntity field_7616;
   
   public FireWorkRocketEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
     super(entityType, world);
   }
   
   @Inject(method = {"method_5773"}, at = {@At("HEAD")})
   public void tick(CallbackInfo ci) {
     (new EventFireWork((FireworkRocketEntity)this)).call();
   }
 
 
 
 
 
 
   
   @ModifyExpressionValue(method = {"method_5773"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/LivingEntity;method_5720()Lnet/minecraft/Vec3d;")})
   public Vec3d captureRotation(Vec3d original) {
     this.rotation = original;
     return this.rotation;
   }
 
 
 
 
 
 
 
   
   @Redirect(method = {"method_5773"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/Vec3d;method_1031(DDD)Lnet/minecraft/Vec3d;", ordinal = 0))
   public Vec3d modifyBoost(Vec3d velocity, double x, double y, double z) {
     MinecraftClient mc = MinecraftClient.method_1551();
     ElytraBoost elytraBoost = ElytraBoost.INSTANCE;
     
     if (mc.field_1724 == null || !mc.field_1724.method_6128()) {
       return defaultBoost(velocity);
     }
     
     if (elytraBoost == null || !elytraBoost.isEnable()) {
       return defaultBoost(velocity);
     }
     
     return handleElytraBoost(mc, elytraBoost, velocity);
   }
   
   @Unique
   private Vec3d handleElytraBoost(MinecraftClient mc, ElytraBoost elytraBoost, Vec3d velocity) {
     String modeName = elytraBoost.getMode().getCurrent();
 
     
     switch (modeName)
     { case "LonyGrief":
         boost = BoostUtils.getBoost((LivingEntity)mc.field_1724);
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
         
         return velocity.method_1031(this.rotation.field_1352 * 0.1D + (this.rotation.field_1352 * boost.field_1352 - velocity.field_1352) * 0.5D, this.rotation.field_1351 * 0.1D + (this.rotation.field_1351 * boost.field_1351 - velocity.field_1351) * 0.5D, this.rotation.field_1350 * 0.1D + (this.rotation.field_1350 * boost.field_1350 - velocity.field_1350) * 0.5D);case "SlimeWorld": boost = BoostUtils.getBoostslime((LivingEntity)mc.field_1724); return velocity.method_1031(this.rotation.field_1352 * 0.1D + (this.rotation.field_1352 * boost.field_1352 - velocity.field_1352) * 0.5D, this.rotation.field_1351 * 0.1D + (this.rotation.field_1351 * boost.field_1351 - velocity.field_1351) * 0.5D, this.rotation.field_1350 * 0.1D + (this.rotation.field_1350 * boost.field_1350 - velocity.field_1350) * 0.5D);case "BravoHVH": boost = BoostUtils.getBoostbravo((LivingEntity)mc.field_1724); return velocity.method_1031(this.rotation.field_1352 * 0.1D + (this.rotation.field_1352 * boost.field_1352 - velocity.field_1352) * 0.5D, this.rotation.field_1351 * 0.1D + (this.rotation.field_1351 * boost.field_1351 - velocity.field_1351) * 0.5D, this.rotation.field_1350 * 0.1D + (this.rotation.field_1350 * boost.field_1350 - velocity.field_1350) * 0.5D);case "ReallyWorld": boost = BoostUtils.getBoostrw((LivingEntity)mc.field_1724); return velocity.method_1031(this.rotation.field_1352 * 0.1D + (this.rotation.field_1352 * boost.field_1352 - velocity.field_1352) * 0.5D, this.rotation.field_1351 * 0.1D + (this.rotation.field_1351 * boost.field_1351 - velocity.field_1351) * 0.5D, this.rotation.field_1350 * 0.1D + (this.rotation.field_1350 * boost.field_1350 - velocity.field_1350) * 0.5D); }  Vec2f customBoost = elytraBoost.getBoostV2(); Vec3d boost = new Vec3d(customBoost.field_1343, customBoost.field_1342, customBoost.field_1343); return velocity.method_1031(this.rotation.field_1352 * 0.1D + (this.rotation.field_1352 * boost.field_1352 - velocity.field_1352) * 0.5D, this.rotation.field_1351 * 0.1D + (this.rotation.field_1351 * boost.field_1351 - velocity.field_1351) * 0.5D, this.rotation.field_1350 * 0.1D + (this.rotation.field_1350 * boost.field_1350 - velocity.field_1350) * 0.5D);
   }
 
 
 
 
   
   @Unique
   private Vec3d defaultBoost(Vec3d velocity) {
     return velocity.method_1031(this.rotation.field_1352 * 0.1D + (this.rotation.field_1352 * 1.5D - velocity.field_1352) * 0.5D, this.rotation.field_1351 * 0.1D + (this.rotation.field_1351 * 1.5D - velocity.field_1351) * 0.5D, this.rotation.field_1350 * 0.1D + (this.rotation.field_1350 * 1.5D - velocity.field_1350) * 0.5D);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\FireWorkRocketEntityMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */