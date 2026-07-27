package shame.nazuna.api.utils.combat;
 import java.util.Objects;
 import net.minecraft.class_1297;
 import net.minecraft.class_1309;
 import net.minecraft.class_1675;
 import net.minecraft.class_238;
 import net.minecraft.class_239;
 import net.minecraft.class_243;
 import net.minecraft.class_3532;
 import net.minecraft.class_3959;
 import net.minecraft.class_3965;
 import net.minecraft.class_3966;
 import net.minecraft.class_746;
 import org.joml.Vector3f;
 import shame.nazuna.api.QClient;
 
 public final class RayTraceUtil implements QClient {
   private RayTraceUtil() {
     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
 
 
   
   public static class_239 rayTrace(double rayTraceDistance, float yaw, float pitch, class_1297 entity) {
     class_243 startVec = mc.field_1724.method_33571();
     class_243 directionVec = getVectorForRotation(pitch, yaw);
     
     class_243 endVec = startVec.method_1031(directionVec.field_1352 * rayTraceDistance, directionVec.field_1351 * rayTraceDistance, directionVec.field_1350 * rayTraceDistance);
 
 
 
 
     
     return (class_239)mc.field_1687.method_17742(new class_3959(startVec, endVec, class_3959.class_3960.field_17559, class_3959.class_242.field_1348, entity));
   }
 
 
 
 
 
 
   
   public static class_3965 raycast(class_243 start, class_243 end, class_3959.class_3960 shapeType) {
     return raycast(start, end, shapeType, (class_1297)mc.field_1724);
   }
   
   public static class_3965 raycast(class_243 start, class_243 end, class_3959.class_3960 shapeType, class_1297 entity) {
     return mc.field_1687.method_17742(new class_3959(start, end, shapeType, class_3959.class_242.field_1348, entity));
   }
   
   public static boolean rayTrace(class_243 clientVec, double range, class_238 box) {
     class_243 cameraVec = ((class_746)Objects.<class_746>requireNonNull(mc.field_1724)).method_33571();
     return (box.method_1006(cameraVec) || box.method_992(cameraVec, cameraVec.method_1019(clientVec.method_1021(range))).isPresent());
   }
   
   public static boolean isViewEntity(class_1309 target, float yaw, float pitch, float distance, boolean ignoreWalls) {
     class_1297 entity = mc.method_1560();
     
     if (entity == null || mc.field_1687 == null)
     {
       return false;
     }
     double reachDistanceSquared = (distance * distance);
     
     class_243 startVec = entity.method_33571();
     Vector3f directionVec = calculateViewVector(yaw, pitch);
     directionVec.mul(distance, distance, distance);
     class_243 endVec = startVec.method_1031(directionVec.x, directionVec.y, directionVec.z);
     class_238 aabb = target.method_5829();
     
     class_3966 result = class_1675.method_18075(entity, startVec, endVec, aabb, entityIn -> 
 
 
 
         
         (!entityIn.method_7325() && entityIn.method_5805() && entityIn == target), reachDistanceSquared);
 
 
     
     return (result != null);
   }
   
   public static Vector3f calculateViewVector(float yaw, float pitch) {
     float pitchRad = pitch * 0.017453292F;
     float yawRad = -yaw * 0.017453292F;
     float cosYaw = class_3532.method_15362(yawRad);
     float sinYaw = class_3532.method_15374(yawRad);
     float cosPitch = class_3532.method_15362(pitchRad);
     float sinPitch = class_3532.method_15374(pitchRad);
     
     return new Vector3f(sinYaw * cosPitch, -sinPitch, cosYaw * cosPitch);
   }
   
   public static class_243 getVectorForRotation(float pitch, float yaw) {
     float yawRadians = -yaw * 0.017453292F - 3.1415927F;
     float pitchRadians = -pitch * 0.017453292F;
     
     float cosYaw = class_3532.method_15362(yawRadians);
     float sinYaw = class_3532.method_15374(yawRadians);
     float cosPitch = -class_3532.method_15362(pitchRadians);
     float sinPitch = class_3532.method_15374(pitchRadians);
     
     return new class_243((sinYaw * cosPitch), sinPitch, (cosYaw * cosPitch));
   }
   
   public static boolean rayTraceSingleEntity(float yaw, float pitch, double distance, class_1297 entity) {
     class_243 eyeVec = mc.field_1724.method_33571();
     class_243 lookVec = mc.field_1724.method_5631(pitch, yaw);
     class_243 extendedVec = eyeVec.method_1019(lookVec.method_1021(distance));
     
     class_238 AABB = entity.method_5829();
     
     return (AABB.method_1006(eyeVec) || AABB.method_992(eyeVec, extendedVec).isPresent());
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\combat\RayTraceUtil.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */