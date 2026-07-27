package shame.nazuna.api.utils.combat;
 
 import java.util.Map;
 import java.util.UUID;
 import java.util.concurrent.ConcurrentHashMap;
 import net.minecraft.class_1309;
 import net.minecraft.class_243;
 import net.minecraft.class_3532;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 
 
 public class PredictUtils
   implements QClient
 {
   private static final Map<UUID, PositionData> positionCache = new ConcurrentHashMap<>();
   public static class PositionData {
     private double serverX; private double serverY; private double serverZ; private double prevServerX; private double prevServerY; private double prevServerZ;
     public long getLastUpdate() { return this.lastUpdate; }
     
     public class_243 getResolvedPos() {
       return new class_243(this.serverX, this.serverY, this.serverZ);
     }
     
     public class_243 getResolvedForward() {
       return new class_243(this.serverX - this.prevServerX, this.serverY - this.prevServerY, this.serverZ - this.prevServerZ);
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
   
   public static void updateEntity(class_1309 entity) {
     PositionData data = positionCache.computeIfAbsent(entity.method_5667(), k -> new PositionData());
     data.update(entity.method_23317(), entity.method_23318(), entity.method_23321());
   }
   
   public static PositionData getData(class_1309 entity) {
     return positionCache.get(entity.method_5667());
   }
   
   public static class_243 predict(class_1309 entity, int ticks, float extraForward, boolean isMeFlying) {
     PositionData data = getData(entity);
     class_243 pos = new class_243(entity.method_23317(), entity.method_23318() + (entity.method_5751() / 2.0F), entity.method_23321());
     
     if (data == null) {
       return predictElytraPhysics(entity, pos, ticks);
     }
     
     class_243 forward = data.getResolvedForward();
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
       
       class_243 linearPredict = pos.method_1019(forward.method_18805(predictMultiplier, predictMultiplier, predictMultiplier));
       class_243 physicsPredict = predictElytraPhysics(entity, pos, ticks);
       
       double weight = class_3532.method_15350(speed / 50.0D, 0.3D, 0.9D);
       
       return new class_243(
           class_3532.method_16436(weight, physicsPredict.field_1352, linearPredict.field_1352), 
           class_3532.method_16436(weight, physicsPredict.field_1351, linearPredict.field_1351), 
           class_3532.method_16436(weight, physicsPredict.field_1350, linearPredict.field_1350));
     } 
 
     
     if (speed > 1.0D) {
       return pos.method_1019(forward.method_18805(ticks, ticks, ticks));
     }
     
     return pos;
   }
 
   
   public static class_243 predict(class_1309 entity, class_243 pos, int ticks) {
     PositionData data = getData(entity);
     
     if (data != null && entity.method_6128()) {
       class_243 forward = data.getResolvedForward();
       double horizontalSpeed = Math.hypot(forward.field_1352, forward.field_1350) * 20.0D;
       double verticalSpeed = Math.abs(forward.field_1351) * 20.0D;
       
       if (horizontalSpeed <= 5.0D && verticalSpeed <= 5.0D) {
         return pos;
       }
       
       return pos.method_1019(forward.method_18805(ticks, ticks, ticks));
     } 
     
     return predictElytraPhysics(entity, pos, ticks);
   }
   
   public static class_243 predictElytraPhysics(class_1309 entity, class_243 pos, int ticks) {
     class_243 velocity = entity.method_18798();
     
     if (!entity.method_6128()) {
       return pos.method_1019(velocity.method_18805(ticks, ticks, ticks));
     }
     
     double horizontalDelta = Math.hypot(entity.field_6014 - entity.method_23317(), entity.field_5969 - entity.method_23321()) * 20.0D;
     double verticalDelta = Math.abs(entity.method_23318() - entity.field_6036) * 20.0D;
     
     if (horizontalDelta <= 5.0D && verticalDelta <= 5.0D) {
       return pos;
     }
     
     for (int i = 0; i < ticks; i++) {
       class_243 rotation = entity.method_5720();
       float pitchRad = (float)Math.toRadians(entity.method_36455());
       double horizontalSpeed = Math.sqrt(velocity.field_1352 * velocity.field_1352 + velocity.field_1350 * velocity.field_1350);
       double velocityLength = velocity.method_1033();
       float cos = class_3532.method_15362(pitchRad);
       cos = (float)((cos * cos) * Math.min(1.0D, rotation.method_1033() / 0.4D));
       
       velocity = velocity.method_1031(0.0D, -0.08D * (-1.0D + cos * 0.75D), 0.0D);
       
       if (velocity.field_1351 < 0.0D && horizontalSpeed > 0.0D) {
         double d5 = velocity.field_1351 * -0.1D * cos;
         velocity = velocity.method_1031(rotation.field_1352 * d5 / horizontalSpeed, d5, rotation.field_1350 * d5 / horizontalSpeed);
       } 
       
       if (pitchRad < 0.0F && horizontalSpeed > 0.0D) {
         double lift = velocityLength * -class_3532.method_15374(pitchRad) * 0.04D;
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
   
   public static class_243 bypasselytrahacking(class_1309 target) {
     class_243 interpolatedRotation = class_243.method_1030(target.method_53829(), target.method_53831());
     class_243 rotationVector = target.method_5720();
     class_243 relativePos = target.method_19538().method_1031(0.0D, (target.method_17682() * 0.6F), 0.0D).method_1020(mc.field_1724.method_33571());
     class_243 blendedDirection = interpolatedRotation.method_1029().method_35590(rotationVector, interpolatedRotation.method_1033());
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