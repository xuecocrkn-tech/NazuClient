package shame.nazuna.api.utils.rotate;
 import net.minecraft.Entity;
 import net.minecraft.Direction;
 import net.minecraft.Box;
 import net.minecraft.HitResult;
 import net.minecraft.Vec2f;
 import net.minecraft.Vec3d;
 import net.minecraft.MathHelper;
 import net.minecraft.RaycastContext;
 import shame.nazuna.client.modules.impl.combat.components.gcd.GCDUtil;
 
 public final class RotationUtils implements QClient {
   private RotationUtils() {
     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   } public static HitResult rayTrace(double dst, float yaw, float pitch) {
     Vec3d vec3d = mc.field_1724.method_5836(1.0F);
     Vec3d vec3d2 = getRotationVector(pitch, yaw);
     Vec3d vec3d3 = vec3d.method_1031(vec3d2.field_1352 * dst, vec3d2.field_1351 * dst, vec3d2.field_1350 * dst);
     return (HitResult)mc.field_1687.method_17742(new RaycastContext(vec3d, vec3d3, RaycastContext.class_3960.field_17559, RaycastContext.class_242.field_1348, (Entity)mc.field_1724));
   }
   
   static Vec3d getBestVector(Entity entity) {
     Vec3d eyePos = mc.field_1724.method_33571();
     Box box = entity.method_5829();
     double step = 0.1D;
     Vec3d bestVec = null;
     double closestDistance = Double.MAX_VALUE;
     double x;
     for (x = box.field_1323; x <= box.field_1320; x += step) {
       double y; for (y = box.field_1322; y <= box.field_1325; y += step) {
         double z; for (z = box.field_1321; z <= box.field_1324; z += step) {
           Vec3d sample = new Vec3d(x, y, z);
           double dist = eyePos.method_1022(sample);
           if (dist < closestDistance) {
             closestDistance = dist;
             bestVec = sample;
           } 
         } 
       } 
     } 
     return bestVec;
   }
   
   public static Rotation fromVec3d(Vec3d vector) {
     return new Rotation((float)MathHelper.method_15338(Math.toDegrees(Math.atan2(vector.field_1350, vector.field_1352)) - 90.0D), (float)MathHelper.method_15338(Math.toDegrees(-Math.atan2(vector.field_1351, Math.hypot(vector.field_1352, vector.field_1350)))));
   }
   @NotNull
   public static Vec3d getRotationVector(float yaw, float pitch) {
     return new Vec3d((MathHelper.method_15374(-pitch * 0.017453292F) * MathHelper.method_15362(yaw * 0.017453292F)), -MathHelper.method_15374(yaw * 0.017453292F), (MathHelper.method_15362(-pitch * 0.017453292F) * MathHelper.method_15362(yaw * 0.017453292F)));
   }
   
   public static Vec2f getRotations(Entity entity) {
     return getRotations(entity.method_23317(), entity.method_23318(), entity.method_23321());
   }
   
   public static Vec2f getRotations(Vec3d vec3d) {
     return getRotations(vec3d.field_1352, vec3d.field_1351, vec3d.field_1350);
   }
 
   
   public static Vec2f getRotations(double x, double y, double z) {
     double deltaX = x - mc.field_1724.method_23317();
     double deltaY = y - mc.field_1724.method_23320();
     double deltaZ = z - mc.field_1724.method_23321();
     double distance = MathHelper.method_15355((float)(deltaX * deltaX + deltaZ * deltaZ));
     
     float yaw = (float)(MathHelper.method_15349(deltaZ, deltaX) * 57.29577951308232D - 90.0D);
     float pitch = (float)(-MathHelper.method_15349(deltaY, distance) * 57.29577951308232D);
     return new Vec2f(yaw, pitch);
   }
   
   public static float[] getRotations(Direction direction) {
     switch (direction) { default: throw new MatchException(null, null);
       case field_11033: (new float[2])[0] = mc.field_1724.method_36454(); (new float[2])[1] = 90.0F;
       case field_11036: (new float[2])[0] = mc.field_1724.method_36454(); (new float[2])[1] = -90.0F;
       case field_11043: (new float[2])[0] = 180.0F; (new float[2])[1] = mc.field_1724.method_36455();
       case field_11035: (new float[2])[0] = 0.0F; (new float[2])[1] = mc.field_1724.method_36455();
       case field_11039: (new float[2])[0] = 90.0F; (new float[2])[1] = mc.field_1724.method_36455();
       case field_11034: break; }  return new float[] { -90.0F, mc.field_1724.method_36455() };
   }
 
   
   public static float[] correctRotation(float[] rotations) {
     rotations[0] = rotations[0] - rotations[0] % GCDUtil.getGCDValue();
     rotations[1] = rotations[1] - rotations[1] % GCDUtil.getGCDValue();
     return new float[] { rotations[0], rotations[1] };
   }
   
   public static float getFixRotate(float rot) {
     return getDeltaMouse(rot) * GCDUtil.getGCDValue();
   }
   
   public static float getDeltaMouse(float delta) {
     return Math.round(delta / GCDUtil.getGCDValue());
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\rotate\RotationUtils.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */