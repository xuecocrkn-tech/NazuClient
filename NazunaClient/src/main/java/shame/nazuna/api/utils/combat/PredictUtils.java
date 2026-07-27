package shame.nazuna.api.utils.combat;
 
 import java.util.Map;
 import java.util.UUID;
 import java.util.concurrent.ConcurrentHashMap;
 import net.minecraft.LivingEntity;
 import net.minecraft.Vec3d;
 import net.minecraft.MathHelper;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 
 
 public class PredictUtils
   implements QClient
 {
   private static final Map<UUID, PositionData> positionCache = new ConcurrentHashMap<>();
   public static class PositionData {
     private double serverX; private double serverY; private double serverZ; private double prevServerX; private double prevServerY; private double prevServerZ;
     public long getLastUpdate() { return this.lastUpdate; }
     
     public Vec3d getResolvedPos() {
       return new Vec3d(this.serverX, this.serverY, this.serverZ);
     }
     
     public Vec3d getResolvedForward() {
       return new Vec3d(this.serverX - this.prevServerX, this.serverY - this.prevServerY, this.serverZ - this.prevServerZ);
     }
 
 
 
 
     
     public void update(double x, double y, double z) {
       this.backUpX = this.prevServerX;
       this.backUpY = this.prevServerY;
       this.backUpZ = this.prevServerZ;
       
       this.prevServerX = this.serverX;
       this.prevServerY = this.serverY;
       this.prevServerZ = this.serverZ;
       this.serverX = x;
       this.serverY = y;
       this.serverZ = z;
       
       this.prevSpeed = this.lastSpeed;
       this.lastSpeed = getResolvedForward().method_1033() * 20.0D;
       this.lastUpdate = System.currentTimeMillis();
     }
     
     public boolean isSpeedChanged() {
       return (this.lastSpeed >= 20.0D || (this.lastSpeed != this.prevSpeed && this.lastSpeed == 0.0D));
     }
   }
   
   public static void updateEntity(LivingEntity entity) {
     PositionData data = positionCache.computeIfAbsent(entity.method_5667(), k -> new PositionData());
     data.update(entity.method_23317(), entity.method_23318(), entity.method_23321());
   }
   
   public static PositionData getData(LivingEntity entity) {
     return positionCache.get(entity.method_5667());
   }
   
   public static Vec3d predict(LivingEntity entity, int ticks, float extraForward, boolean isMeFlying) {
     PositionData data = getData(entity);
     Vec3d pos = new Vec3d(entity.method_23317(), entity.method_23318() + (entity.method_5751() / 2.0F), entity.method_23321());
     
     if (data == null) {
       return predictElytraPhysics(entity, pos, ticks);
     }
     
     Vec3d forward = data.getResolvedForward();
     double speed = data.getLastSpeed();
     boolean isHighSpeed = data.isSpeedChanged();
     
     if (entity.method_6128()) {
       double horizontalSpeed = Math.hypot(forward.field_1352, forward.field_1350) * 20.0D;
       double verticalSpeed = Math.abs(forward.field_1351) * 20.0D;
       
       if (horizontalSpeed <= 5.0D && verticalSpeed <= 5.0D) {
         return pos;
       }
       
       boolean shouldPredict = (isMeFlying && entity.method_6128() && isHighSpeed);
       float predictMultiplier = shouldPredict ? ((ticks + 2) + extraForward) : ticks;
       
       Vec3d linearPredict = pos.method_1019(forward.method_18805(predictMultiplier, predictMultiplier, predictMultiplier));
       Vec3d physicsPredict = predictElytraPhysics(entity, pos, ticks);
       
       double weight = MathHelper.method_15350(speed / 50.0D, 0.3D, 0.9D);
       
       return new Vec3d(
           MathHelper.method_16436(weight, physicsPredict.field_1352, linearPredict.field_1352), 
           MathHelper.method_16436(weight, physicsPredict.field_1351, linearPredict.field_1351), 
           MathHelper.method_16436(weight, physicsPredict.field_1350, linearPredict.field_1350));
     } 
 
     
     if (speed > 1.0D) {
       return pos.method_1019(forward.method_18805(ticks, ticks, ticks));
     }
     
     return pos;
   }
 
   
   public static Vec3d predict(LivingEntity entity, Vec3d pos, int ticks) {
     PositionData data = getData(entity);
     
     if (data != null && entity.method_6128()) {
       Vec3d forward = data.getResolvedForward();
       double horizontalSpeed = Math.hypot(forward.field_1352, forward.field_1350) * 20.0D;
       double verticalSpeed = Math.abs(forward.field_1351) * 20.0D;
       
       if (horizontalSpeed <= 5.0D && verticalSpeed <= 5.0D) {
         return pos;
       }
       
       return pos.method_1019(forward.method_18805(ticks, ticks, ticks));
     } 
     
     return predictElytraPhysics(entity, pos, ticks);
   }
   
   public static Vec3d predictElytraPhysics(LivingEntity entity, Vec3d pos, int ticks) {
     Vec3d velocity = entity.method_18798();
     
     if (!entity.method_6128()) {
       return pos.method_1019(velocity.method_18805(ticks, ticks, ticks));
     }
     
     double horizontalDelta = Math.hypot(entity.field_6014 - entity.method_23317(), entity.field_5969 - entity.method_23321()) * 20.0D;
     double verticalDelta = Math.abs(entity.method_23318() - entity.field_6036) * 20.0D;
     
     if (horizontalDelta <= 5.0D && verticalDelta <= 5.0D) {
       return pos;
     }
     
     for (int i = 0; i < ticks; i++) {
       Vec3d rotation = entity.method_5720();
       float pitchRad = (float)Math.toRadians(entity.method_36455());
       double horizontalSpeed = Math.sqrt(velocity.field_1352 * velocity.field_1352 + velocity.field_1350 * velocity.field_1350);
       double velocityLength = velocity.method_1033();
       float cos = MathHelper.method_15362(pitchRad);
       cos = (float)((cos * cos) * Math.min(1.0D, rotation.method_1033() / 0.4D));
       
       velocity = velocity.method_1031(0.0D, -0.08D * (-1.0D + cos * 0.75D), 0.0D);
       
       if (velocity.field_1351 < 0.0D && horizontalSpeed > 0.0D) {
         double d5 = velocity.field_1351 * -0.1D * cos;
         velocity = velocity.method_1031(rotation.field_1352 * d5 / horizontalSpeed, d5, rotation.field_1350 * d5 / horizontalSpeed);
       } 
       
       if (pitchRad < 0.0F && horizontalSpeed > 0.0D) {
         double lift = velocityLength * -MathHelper.method_15374(pitchRad) * 0.04D;
         velocity = velocity.method_1031(-rotation.field_1352 * lift / horizontalSpeed, lift * 3.2D, -rotation.field_1350 * lift / horizontalSpeed);
       } 
       
       if (horizontalSpeed > 0.0D) {
         velocity = velocity.method_1031((rotation.field_1352 / horizontalSpeed * velocityLength - velocity.field_1352) * 0.1D, 0.0D, (rotation.field_1350 / horizontalSpeed * velocityLength - velocity.field_1350) * 0.1D);
       }
 
 
 
 
       
       velocity = velocity.method_18805(0.99D, 0.98D, 0.99D);
       pos = pos.method_1019(velocity);
     } 
     
     return pos;
   }
   
   public static Vec3d bypasselytrahacking(LivingEntity target) {
     Vec3d interpolatedRotation = Vec3d.method_1030(target.method_53829(), target.method_53831());
     Vec3d rotationVector = target.method_5720();
     Vec3d relativePos = target.method_19538().method_1031(0.0D, (target.method_17682() * 0.6F), 0.0D).method_1020(mc.field_1724.method_33571());
     Vec3d blendedDirection = interpolatedRotation.method_1029().method_35590(rotationVector, interpolatedRotation.method_1033());
     return relativePos.method_1019(blendedDirection.method_1029().method_1021(ModuleClass.elytraTarget.forward.getValue().floatValue()));
   }
   
   public static void cleanup() {
     long now = System.currentTimeMillis();
     positionCache.entrySet().removeIf(e -> (now - ((PositionData)e.getValue()).getLastUpdate() > 10000L));
   }
   
   public static void clear() {
     positionCache.clear();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\combat\PredictUtils.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */