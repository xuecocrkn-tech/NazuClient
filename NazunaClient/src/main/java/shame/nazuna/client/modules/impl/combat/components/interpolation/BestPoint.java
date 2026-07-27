package shame.nazuna.client.modules.impl.combat.components.interpolation;
 import net.minecraft.class_1297;
 import net.minecraft.class_238;
 import net.minecraft.class_239;
 import net.minecraft.class_243;
 import net.minecraft.class_3959;
 import net.minecraft.class_3965;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.utils.combat.RayTraceUtil;
 import shame.nazuna.api.utils.math.MathUtils;
 import shame.nazuna.api.utils.rotate.Rotation;
 
 public final class BestPoint implements QClient {
   private BestPoint() {
     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   } private static class_243 rotationPoint = class_243.field_1353;
   private static class_243 rotationMotion = class_243.field_1353;
   
   public static class_243 getRotationPoint() {
     return rotationPoint;
   }
   
   public static class_243 getNearestPoint(class_1297 entity) {
     class_238 box = entity.method_5829();
     double step = 0.1D;
     class_243 bestVec = null;
     double closestDistance = Double.MAX_VALUE;
     double x;
     for (x = box.field_1323; x <= box.field_1320; x += step) {
       double y; for (y = box.field_1322; y <= box.field_1325; y += step) {
         double z; for (z = box.field_1321; z <= box.field_1324; z += step) {
           class_243 sample = new class_243(x, y, z);
           double dist = mc.field_1724.method_33571().method_1022(sample);
           if (dist < closestDistance) {
             closestDistance = dist;
             bestVec = sample;
           } 
         } 
       } 
     } 
     return bestVec;
   }
   public static class_243 getPoint(class_1297 target) {
     class_238 box = target.method_5829();
     
     double width = box.field_1320 - box.field_1323;
     double height = box.field_1325 - box.field_1322;
     double depth = box.field_1324 - box.field_1321;
     
     double baseX = box.field_1323 + width / 2.0D;
     double baseY = box.field_1322 + height * 0.7D;
     double baseZ = box.field_1321 + depth / 2.0D;
     
     double time = System.currentTimeMillis() / 50.0D;
     
     int id = target.method_5628();
 
     
     double offsetX = Math.sin(time + id) * width * 0.45D;
     
     double offsetY = Math.cos(time * 0.8D + id) * height * 0.1D;
     
     double offsetZ = Math.cos(time * 1.2D + id) * depth * 0.45D;
     
     return new class_243(baseX + offsetX, baseY + offsetY, baseZ + offsetZ);
   }
   public static class_243 getPoint2(class_1297 target) {
     class_238 box = target.method_5829();
     
     double width = box.field_1320 - box.field_1323;
     double height = box.field_1325 - box.field_1322;
     double depth = box.field_1324 - box.field_1321;
     
     double baseX = box.field_1323 + width / 2.0D;
     double baseY = box.field_1322 + height * 0.65D;
     double baseZ = box.field_1321 + depth / 2.0D;
     
     double time = System.currentTimeMillis() / 65.0D;
     
     int id = target.method_5628();
 
     
     double offsetX = Math.sin(time + id) * width * 0.7D;
     
     double offsetY = Math.cos(time * 0.8D + id) * height * 0.4D;
     
     double offsetZ = Math.cos(time * 1.2D + id) * depth * 0.7D;
     
     return new class_243(baseX + offsetX, baseY + offsetY, baseZ + offsetZ);
   }
   
   public static class_243 getNearestVisiblePoint(class_1297 target, class_243 preferredPoint, double range) {
     if (preferredPoint == null || mc.field_1724 == null || mc.field_1687 == null) {
       return preferredPoint;
     }
     
     if (isPointVisible(target, preferredPoint, range)) {
       return preferredPoint;
     }
     
     class_238 box = target.method_5829();
     double step = 0.12D;
     class_243 bestPoint = null;
     double bestDistance = Double.MAX_VALUE;
     double x;
     for (x = box.field_1323; x <= box.field_1320; x += step) {
       double y; for (y = box.field_1322; y <= box.field_1325; y += step) {
         double z; for (z = box.field_1321; z <= box.field_1324; z += step) {
           class_243 sample = new class_243(x, y, z);
           if (isPointVisible(target, sample, range)) {
 
 
             
             double distanceToCurrent = sample.method_1025(preferredPoint);
             if (distanceToCurrent < bestDistance) {
               bestDistance = distanceToCurrent;
               bestPoint = sample;
             } 
           } 
         } 
       } 
     } 
     return (bestPoint != null) ? bestPoint : preferredPoint;
   }
   
   private static boolean isPointVisible(class_1297 target, class_243 point, double range) {
     class_243 eyePos = mc.field_1724.method_33571();
     double distance = eyePos.method_1022(point);
     if (distance > range) {
       return false;
     }
     
     class_243 direction = point.method_1020(eyePos).method_1029();
     if (!RayTraceUtil.rayTrace(direction, distance + 0.2D, target.method_5829())) {
       return false;
     }
     
     class_3965 blockHit = RayTraceUtil.raycast(eyePos, point, class_3959.class_3960.field_17558, (class_1297)mc.field_1724);
     return (blockHit.method_17783() == class_239.class_240.field_1333 || eyePos.method_1025(blockHit.method_17784()) >= eyePos.method_1025(point) - 1.0E-4D);
   }
   
   public static class_243 getMultipoint(class_1297 target, double distance) {
     float minMotionXZ = 0.005F;
     float maxMotionXZ = 0.015F;
     
     float minMotionY = 0.0015F;
     float maxMotionY = 0.015F;
     
     double lenghtX = target.method_5829().method_17939();
     double lenghtY = target.method_5829().method_17940();
     double lenghtZ = target.method_5829().method_17941();
     
     if (rotationMotion.equals(class_243.field_1353)) {
       rotationMotion = new class_243(MathUtils.randomBest(-0.019999999552965164D, 0.019999999552965164D), MathUtils.randomBest(-0.019999999552965164D, 0.019999999552965164D), MathUtils.randomBest(-0.019999999552965164D, 0.019999999552965164D));
     }
     if (rotationPoint.equals(class_243.field_1353)) {
       rotationPoint = new class_243(0.0D, lenghtY * 0.5D, 0.0D);
     }
     rotationPoint = rotationPoint.method_1019(rotationMotion);
     
     double safeX = (lenghtX - 0.1D) / 2.0D;
     double safeZ = (lenghtZ - 0.1D) / 2.0D;
     
     if (rotationPoint.field_1352 >= safeX) {
       rotationMotion = new class_243(-MathUtils.randomBest(minMotionXZ, maxMotionXZ), rotationMotion.method_10214(), rotationMotion.method_10215());
     } else if (rotationPoint.field_1352 <= -safeX) {
       rotationMotion = new class_243(MathUtils.randomBest(minMotionXZ, maxMotionXZ), rotationMotion.method_10214(), rotationMotion.method_10215());
     } 
     if (rotationPoint.field_1351 >= lenghtY * 0.75D) {
       rotationMotion = new class_243(rotationMotion.method_10216(), -MathUtils.randomBest(minMotionY, maxMotionY), rotationMotion.method_10215());
     } else if (rotationPoint.field_1351 <= lenghtY * 0.3D) {
       rotationMotion = new class_243(rotationMotion.method_10216(), MathUtils.randomBest(minMotionY, maxMotionY), rotationMotion.method_10215());
     } 
     if (rotationPoint.field_1350 >= safeZ) {
       rotationMotion = new class_243(rotationMotion.method_10216(), rotationMotion.method_10214(), -MathUtils.randomBest(minMotionXZ, maxMotionXZ));
     } else if (rotationPoint.field_1350 <= -safeZ) {
       rotationMotion = new class_243(rotationMotion.method_10216(), rotationMotion.method_10214(), MathUtils.randomBest(minMotionXZ, maxMotionXZ));
     } 
     rotationPoint.method_1031(MathUtils.randomBest(-0.05000000074505806D, 0.05000000074505806D), 0.0D, MathUtils.randomBest(-0.05000000074505806D, 0.05000000074505806D));
 
 
     
     if (!RayTraceUtil.rayTrace(mc.field_1724.method_5720(), distance, target.method_5829())) {
       float halfBox = (float)(lenghtX / 2.0D) * 0.8F;
       
       float x1;
       for (x1 = -halfBox; x1 <= halfBox; x1 += 0.1F) {
         float z1; for (z1 = -halfBox; z1 <= halfBox; z1 += 0.1F) {
           float y1; for (y1 = (float)(lenghtY * 0.9D); y1 >= lenghtY * 0.3D; y1 -= 0.1F) {
             
             class_243 v1 = new class_243(target.method_23317() + x1, target.method_23318() + y1, target.method_23321() + z1);
             
             Rotation rotation = RotationUtils.fromVec3d(v1);
             if (RayTraceUtil.rayTrace(rotation.toVector(), distance, target.method_5829())) {
               rotationPoint = new class_243(x1, y1, z1);
               return target.method_19538().method_1019(rotationPoint);
             } 
           } 
         } 
       } 
     } 
     
     return target.method_19538().method_1019(rotationPoint);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\components\interpolation\BestPoint.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */