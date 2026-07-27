package shame.nazuna.api.utils.combat;
 import java.util.Objects;
 import net.minecraft.Entity;
 import net.minecraft.LivingEntity;
 import net.minecraft.ProjectileUtil;
 import net.minecraft.Box;
 import net.minecraft.HitResult;
 import net.minecraft.Vec3d;
 import net.minecraft.MathHelper;
 import net.minecraft.RaycastContext;
 import net.minecraft.BlockHitResult;
 import net.minecraft.EntityHitResult;
 import net.minecraft.ClientPlayerEntity;
 import org.joml.Vector3f;
 import shame.nazuna.api.QClient;
 
 public final class RayTraceUtil implements QClient {
   private RayTraceUtil() {
     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
 
 
   
   public static HitResult rayTrace(double rayTraceDistance, float yaw, float pitch, Entity entity) {
     Vec3d startVec = mc.field_1724.method_33571();
     Vec3d directionVec = getVectorForRotation(pitch, yaw);
     
     Vec3d endVec = startVec.method_1031(directionVec.field_1352 * rayTraceDistance, directionVec.field_1351 * rayTraceDistance, directionVec.field_1350 * rayTraceDistance);
 
 
 
 
     
     return (HitResult)mc.field_1687.method_17742(new RaycastContext(startVec, endVec, RaycastContext.class_3960.field_17559, RaycastContext.class_242.field_1348, entity));
   }
 
 
 
 
 
 
   
   public static BlockHitResult raycast(Vec3d start, Vec3d end, RaycastContext.class_3960 shapeType) {
     return raycast(start, end, shapeType, (Entity)mc.field_1724);
   }
   
   public static BlockHitResult raycast(Vec3d start, Vec3d end, RaycastContext.class_3960 shapeType, Entity entity) {
     return mc.field_1687.method_17742(new RaycastContext(start, end, shapeType, RaycastContext.class_242.field_1348, entity));
   }
   
   public static boolean rayTrace(Vec3d clientVec, double range, Box box) {
     Vec3d cameraVec = ((ClientPlayerEntity)Objects.<ClientPlayerEntity>requireNonNull(mc.field_1724)).method_33571();
     return (box.method_1006(cameraVec) || box.method_992(cameraVec, cameraVec.method_1019(clientVec.method_1021(range))).isPresent());
   }
   
   public static boolean isViewEntity(LivingEntity target, float yaw, float pitch, float distance, boolean ignoreWalls) {
     Entity entity = mc.method_1560();
     
     if (entity == null || mc.field_1687 == null)
     {
       return false;
     }
     double reachDistanceSquared = (distance * distance);
     
     Vec3d startVec = entity.method_33571();
     Vector3f directionVec = calculateViewVector(yaw, pitch);
     directionVec.mul(distance, distance, distance);
     Vec3d endVec = startVec.method_1031(directionVec.x, directionVec.y, directionVec.z);
     Box aabb = target.method_5829();
     
     EntityHitResult result = ProjectileUtil.method_18075(entity, startVec, endVec, aabb, entityIn -> 
 
 
 
         
         (!entityIn.method_7325() && entityIn.method_5805() && entityIn == target), reachDistanceSquared);
 
 
     
     return (result != null);
   }
   
   public static Vector3f calculateViewVector(float yaw, float pitch) {
     float pitchRad = pitch * 0.017453292F;
     float yawRad = -yaw * 0.017453292F;
     float cosYaw = MathHelper.method_15362(yawRad);
     float sinYaw = MathHelper.method_15374(yawRad);
     float cosPitch = MathHelper.method_15362(pitchRad);
     float sinPitch = MathHelper.method_15374(pitchRad);
     
     return new Vector3f(sinYaw * cosPitch, -sinPitch, cosYaw * cosPitch);
   }
   
   public static Vec3d getVectorForRotation(float pitch, float yaw) {
     float yawRadians = -yaw * 0.017453292F - 3.1415927F;
     float pitchRadians = -pitch * 0.017453292F;
     
     float cosYaw = MathHelper.method_15362(yawRadians);
     float sinYaw = MathHelper.method_15374(yawRadians);
     float cosPitch = -MathHelper.method_15362(pitchRadians);
     float sinPitch = MathHelper.method_15374(pitchRadians);
     
     return new Vec3d((sinYaw * cosPitch), sinPitch, (cosYaw * cosPitch));
   }
   
   public static boolean rayTraceSingleEntity(float yaw, float pitch, double distance, Entity entity) {
     Vec3d eyeVec = mc.field_1724.method_33571();
     Vec3d lookVec = mc.field_1724.method_5631(pitch, yaw);
     Vec3d extendedVec = eyeVec.method_1019(lookVec.method_1021(distance));
     
     Box AABB = entity.method_5829();
     
     return (AABB.method_1006(eyeVec) || AABB.method_992(eyeVec, extendedVec).isPresent());
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\combat\RayTraceUtil.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */