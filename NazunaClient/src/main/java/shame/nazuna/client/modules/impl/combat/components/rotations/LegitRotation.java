package shame.nazuna.client.modules.impl.combat.components.rotations;
 
 import java.util.Optional;
 import java.util.concurrent.ThreadLocalRandom;
 import net.minecraft.LivingEntity;
 import net.minecraft.Box;
 import net.minecraft.Vec2f;
 import net.minecraft.Vec3d;
 import net.minecraft.MathHelper;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.storages.implement.RotationStorage;
 import shame.nazuna.api.utils.rotate.Rotation;
 import shame.nazuna.api.utils.rotate.RotationUtils;
 import shame.nazuna.client.modules.impl.combat.Aura;
 import shame.nazuna.client.modules.impl.combat.components.RotationsSystem;
 
 public class LegitRotation
   extends RotationsSystem
   implements QClient
 {
   public void updateRotations(LivingEntity target) {
     Vec3d eyePos = mc.field_1724.method_5836(1.0F);
     Vec3d lookVec = mc.field_1724.method_5828(1.0F);
     Vec3d reachVec = eyePos.method_1019(lookVec.method_1021(999.0D));
     
     Box box = getPredictedBox(target);
     
     double shrinkXZ = target.method_6128() ? -0.5D : 0.10000000149011612D;
     double shrinkY = target.method_6128() ? -0.5D : 0.10000000149011612D;
 
 
 
 
 
 
     
     box = new Box(box.field_1323 + box.method_17939() * shrinkXZ / 2.0D, box.field_1322, box.field_1321 + box.method_17941() * shrinkXZ / 2.0D, box.field_1320 - box.method_17939() * shrinkXZ / 2.0D, box.field_1325 - box.method_17940() * shrinkY, box.field_1324 - box.method_17941() * shrinkXZ / 2.0D);
 
     
     Optional<Vec3d> hit = box.method_992(eyePos, reachVec);
     boolean inside = box.method_1006(eyePos);
     
     if (hit.isPresent() || inside) {
       Aura.adjYaw = MathHelper.method_15363(Aura.adjYaw - ThreadLocalRandom.current().nextFloat(0.005F, 0.02F), 0.0F, 1.0F);
       Aura.adjPitch = MathHelper.method_15363(Aura.adjPitch - ThreadLocalRandom.current().nextFloat(0.005F, 0.02F), 0.0F, 1.0F);
     }
     else if (mc.field_1724.method_6128()) {
       Aura.adjYaw = MathHelper.method_15363(Aura.adjYaw + ThreadLocalRandom.current().nextFloat(5.0E-4F, 0.005F), 0.0F, 1.0F);
       Aura.adjPitch = MathHelper.method_15363(Aura.adjPitch + ThreadLocalRandom.current().nextFloat(9.0E-4F, 0.009F), 0.0F, 1.0F);
     }
     else if (target.method_20232()) {
       Aura.adjYaw = MathHelper.method_15363(Aura.adjYaw + ThreadLocalRandom.current().nextFloat(9.0E-5F, 0.009F), 0.0F, 1.0F);
       Aura.adjPitch = MathHelper.method_15363(Aura.adjPitch + ThreadLocalRandom.current().nextFloat(9.0E-5F, 9.0E-4F), 0.0F, 1.0F);
     } else {
       Aura.adjYaw = MathHelper.method_15363(Aura.adjYaw + ThreadLocalRandom.current().nextFloat(9.0E-5F, 0.009F), 0.0F, 1.0F);
       Aura.adjPitch = MathHelper.method_15363(Aura.adjPitch + ThreadLocalRandom.current().nextFloat(9.0E-4F, 0.009F), 0.0F, 1.0F);
     } 
 
 
 
     
     Vec2f targetRot = RotationUtils.getRotations(getPredictedPoint(target, target.method_30951(1.0F)));
     
     float currentYaw = mc.field_1724.method_36454();
     float currentPitch = mc.field_1724.method_36455();
     
     float diffYaw = MathHelper.method_15393(targetRot.field_1343 - currentYaw);
     float diffPitch = MathHelper.method_15393(targetRot.field_1342 - currentPitch);
     
     float newYaw = currentYaw + diffYaw * Aura.adjYaw;
     float newPitch = currentPitch + diffPitch * Aura.adjPitch;
     
     Aura.otvodkaYaw = 0.0F;
     Aura.otvodkaPitch = 0.0F;
     RotationStorage.update(new Rotation(newYaw, newPitch), 360.0F, 360.0F, 40.0F, 35.0F, 1, 1, Aura.clientLook
 
 
         
         .isState());
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\components\rotations\LegitRotation.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */