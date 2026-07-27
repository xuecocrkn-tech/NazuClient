package shame.nazuna.api.utils.rotate;
 import net.minecraft.Entity;
 import net.minecraft.Box;
 import net.minecraft.Vec3d;
 import shame.nazuna.api.QClient;
 
 public final class MultipointUtils implements QClient {
   private MultipointUtils() {
     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   } public static Vec3d getClosestPoint(Entity entity) {
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
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\rotate\MultipointUtils.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */