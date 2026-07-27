package shame.nazuna.client.modules.impl.combat.components;
 
 import net.minecraft.LivingEntity;
 import net.minecraft.Box;
 import net.minecraft.Vec2f;
 import net.minecraft.Vec3d;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.combat.PredictUtils;
 import shame.nazuna.client.modules.impl.combat.components.gcd.GCDUtil;
 
 public abstract class RotationsSystem
   implements QClient {
   public Vec2f rotate = Vec2f.field_1340;
   
   public abstract void updateRotations(LivingEntity paramclass_1309);
   
   public static Vec2f correctRotation(float yaw, float pitch) {
     if ((yaw == -90.0F && pitch == 90.0F) || yaw == -180.0F) return new Vec2f(mc.field_1724.method_36454(), mc.field_1724.method_36455());
     
     float gcd = GCDUtil.getGCD();
     yaw -= yaw % gcd;
     pitch -= pitch % gcd;
     
     return new Vec2f(yaw, pitch);
   }
   
   protected boolean shouldUseElytraPredict(LivingEntity target) {
     return (mc.field_1724 != null && target != null && mc.field_1724
       
       .method_6128() && target
       .method_6128() && ModuleClass.elytraTarget != null && ModuleClass.elytraTarget
       
       .isEnable());
   }
   
   protected int getElytraPredictTicks() {
     if (ModuleClass.elytraTarget == null) {
       return 0;
     }
     return Math.max(0, ModuleClass.elytraTarget.forward.getValue().intValue());
   }
   
   protected Vec3d getPredictedPoint(LivingEntity target, Vec3d point) {
     if (!shouldUseElytraPredict(target)) {
       return point;
     }
     
     return PredictUtils.bypasselytrahacking(target);
   }
   
   protected Box getPredictedBox(LivingEntity target) {
     Box box = target.method_5829();
     if (!shouldUseElytraPredict(target)) {
       return box;
     }
     Vec3d currentCenter = box.method_1005();
     Vec3d predictedCenter = getPredictedPoint(target, currentCenter);
     return box.method_997(predictedCenter.method_1020(currentCenter));
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\components\RotationsSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */