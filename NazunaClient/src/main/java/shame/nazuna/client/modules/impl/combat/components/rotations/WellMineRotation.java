package shame.nazuna.client.modules.impl.combat.components.rotations;
 
 import net.minecraft.LivingEntity;
 import net.minecraft.Box;
 import net.minecraft.Vec3d;
 import net.minecraft.MathHelper;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.storages.implement.RotationStorage;
 import shame.nazuna.api.utils.rotate.Rotation;
 import shame.nazuna.client.modules.impl.combat.Aura;
 import shame.nazuna.client.modules.impl.combat.components.RotationsSystem;
 
 public class WellMineRotation
   extends RotationsSystem
   implements QClient {
   private LivingEntity currentTarget;
   private float lastYaw = 0.0F;
   private float lastPitch = 0.0F;
   
   private float acceleration = 0.0F;
   
   private boolean isBack = false;
   private double randomOffsetX = 0.0D;
   private double randomOffsetY = 0.0D;
   private double randomOffsetZ = 0.0D;
   
   public void reset() {
     this.currentTarget = null;
     this.acceleration = 0.0F;
     this.isBack = false;
     this.randomOffsetX = 0.0D;
     this.randomOffsetY = 0.0D;
     this.randomOffsetZ = 0.0D;
     
     if (mc.field_1724 != null) {
       this.lastYaw = mc.field_1724.method_36454();
       this.lastPitch = mc.field_1724.method_36455();
     } else {
       this.lastYaw = 0.0F;
       this.lastPitch = 0.0F;
     } 
   }
   
   private float getGCDValue() {
     float sensitivity = (float)(((Double)mc.field_1690.method_42495().method_41753()).doubleValue() * 0.6000000238418579D + 0.20000000298023224D);
     return sensitivity * sensitivity * sensitivity * 1.2F;
   }
 
   
   private void updateRandomOffset(LivingEntity target) {
     Box box = target.method_5829();
     double boxWidth = box.field_1320 - box.field_1323;
     double boxHeight = box.field_1325 - box.field_1322;
     double boxDepth = box.field_1324 - box.field_1321;
     
     this.randomOffsetX = (Math.random() - 0.5D) * boxWidth * 0.15D;
     this.randomOffsetY = (Math.random() - 0.5D) * boxHeight * 0.15D;
     this.randomOffsetZ = (Math.random() - 0.5D) * boxDepth * 0.15D;
   }
 
 
   
   public void updateRotations(LivingEntity target) {
     if (mc.field_1724 == null || target == null) {
       return;
     }
     
     if (this.currentTarget != target) {
       this.currentTarget = target;
       this.acceleration = 0.0F;
       this.isBack = false;
       this.lastYaw = mc.field_1724.method_36454();
       this.lastPitch = mc.field_1724.method_36455();
       updateRandomOffset(target);
     } 
     
     Box box = getPredictedBox(target);
     Vec3d eyePos = mc.field_1724.method_33571();
     Vec3d centerPoint = box.method_1005().method_1031(this.randomOffsetX, this.randomOffsetY, this.randomOffsetZ);
     Vec3d toTarget = centerPoint.method_1020(eyePos);
     float centerYaw = (float)MathHelper.method_15338(Math.toDegrees(Math.atan2(toTarget.field_1350, toTarget.field_1352)) - 90.0D);
     float centerPitch = (float)-Math.toDegrees(Math.atan2(toTarget.field_1351, Math.hypot(toTarget.field_1352, toTarget.field_1350)));
     boolean bothGliding = (mc.field_1724.method_6128() && target.method_6128());
     Vec3d lookVec = mc.field_1724.method_5828(1.0F);
     Vec3d endVec = eyePos.method_1019(lookVec.method_1021(bothGliding ? 1488.0D : 999.0D));
     Box shrunkBox = box.method_1014(bothGliding ? 0.0D : -0.5D);
     boolean inBox = shrunkBox.method_992(eyePos, endVec).isPresent();
     
     if (bothGliding) {
       if (this.isBack) {
         if (this.acceleration >= -0.02F) {
           this.acceleration -= (Math.abs(MathHelper.method_15393(centerYaw - this.lastYaw)) > 80.0F) ? 0.15F : 0.02F;
         }
         if (this.acceleration <= -0.02F) {
           this.isBack = false;
           updateRandomOffset(target);
         } 
       } else {
         this.acceleration += 0.0105F;
         if (this.acceleration >= 0.305F || inBox) {
           this.isBack = true;
         }
       } 
     } else if (this.isBack) {
       if (this.acceleration >= -0.15F) {
         float slowdownSpeed = (Math.abs(MathHelper.method_15393(centerYaw - this.lastYaw)) > 80.0F) ? 0.1F : 0.01F;
         this.acceleration -= slowdownSpeed *= 0.9F + (float)Math.random() * 0.2F;
       } 
       if (this.acceleration <= -0.15F) {
         this.isBack = false;
         updateRandomOffset(target);
       } 
     } else {
       float accelSpeed = 0.0082F + (float)Math.random() * 0.002F - 0.001F;
       this.acceleration += accelSpeed;
       float threshold = 0.184F + (float)Math.random() * 0.03F - 0.015F;
       if (this.acceleration >= threshold || inBox) {
         this.isBack = true;
       }
     } 
     
     float deltaYaw = MathHelper.method_15393(centerYaw - this.lastYaw);
     float deltaPitch = centerPitch - this.lastPitch;
     float smooth = Math.max(this.acceleration, 0.0F);
     float humanYawOffset = (float)(Math.sin(System.currentTimeMillis() * 0.001D) * 0.04D);
     float humanPitchOffset = (float)(Math.cos(System.currentTimeMillis() * 0.0015D) * 0.025D);
     if (Math.abs(deltaYaw) > 1.0F || Math.abs(deltaPitch) > 1.0F) {
       humanYawOffset += ((float)Math.random() - 0.5F) * 0.035F;
       humanPitchOffset += ((float)Math.random() - 0.5F) * 0.02F;
     } 
     
     float newYaw = this.lastYaw + deltaYaw * MathHelper.method_15363(smooth * 1.12F, 0.0F, 1.0F) + humanYawOffset;
     float newPitch = this.lastPitch + deltaPitch * MathHelper.method_15363(smooth / 1.88F, 0.0F, 1.0F) + humanPitchOffset;
     float gcd = getGCDValue();
     newYaw -= (newYaw - this.lastYaw) % gcd;
     newPitch -= (newPitch - this.lastPitch) % gcd;
     if (newPitch > 89.0F) {
       newPitch = 89.0F;
     }
     if (newPitch < -89.0F) {
       newPitch = -89.0F;
     }
     
     this.lastYaw = newYaw;
     this.lastPitch = newPitch;
     RotationStorage.update(new Rotation(newYaw, newPitch), 360.0F, 45.0F, 45.0F, 45.0F, 0, 1, Aura.clientLook.isState());
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\components\rotations\WellMineRotation.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */