package shame.nazuna.client.modules.impl.combat.components.rotations;
 
 import net.minecraft.LivingEntity;
 import net.minecraft.Vec2f;
 import net.minecraft.Vec3d;
 import net.minecraft.MathHelper;
 import shame.nazuna.api.QClient;
 import shame.nazuna.client.modules.impl.combat.components.RotationsSystem;
 import shame.nazuna.client.modules.impl.combat.components.gcd.GCDUtil;
 
 public class PredictRots
   extends RotationsSystem
   implements QClient
 {
   public Vec2f rotating(Vec2f rotation, LivingEntity target) {
     Vec3d vec = calcPointed(target);
     float rawYaw = (float)MathHelper.method_15338(Math.toDegrees(Math.atan2(vec.field_1350, vec.field_1352)) - 90.0D);
     float rawPitch = (float)MathHelper.method_15338(Math.toDegrees(-Math.atan2(vec.field_1351, Math.hypot(vec.field_1352, vec.field_1350))));
     float yawDelta = MathHelper.method_15393(rawYaw - rotation.field_1343);
     float pitchDelta = MathHelper.method_15393(rawPitch - rotation.field_1342);
     if (Math.abs(yawDelta) > 180.0F) {
       yawDelta -= Math.signum(yawDelta) * 360.0F;
     }
     
     float additionYaw = MathHelper.method_15363(yawDelta, -180.0F, 180.0F);
     float additionPitch = MathHelper.method_15363(pitchDelta, -90.0F, 90.0F);
     float yaw = rotation.field_1343 + additionYaw;
     float pitch = rotation.field_1342 + additionPitch;
     
     float yawFinal = GCDUtil.getFixedRotation(yaw);
     float pitchFinal = GCDUtil.getFixedRotation(pitch);
     
     return new Vec2f(yawFinal, pitchFinal);
   }
 
   
   private Vec3d calcPointed(LivingEntity target) {
     if (target != null) {
       Vec3d vecPosition = getPredictedPoint(target, target.method_5829().method_1005());
       
       return new Vec3d(vecPosition.method_10216() - mc.field_1724.method_23317(), vecPosition.method_10214() - mc.field_1724.method_23318(), vecPosition.method_10215() - mc.field_1724.method_23321());
     } 
     return Vec3d.field_1353;
   }
   
   public void updateRotations(LivingEntity entity) {}
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\components\rotations\PredictRots.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */