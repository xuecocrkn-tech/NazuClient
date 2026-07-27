package shame.nazuna.api.utils.combat;
 import net.minecraft.MathHelper;
 import shame.nazuna.api.QClient;
 
 public final class BoostUtils implements QClient {
   private BoostUtils() {
     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
   
   public static double getBoost() {
     int[] vectors = { -45, 45, 135, -135 };
     int[] addVectors = { -90, 90, 180, -180, 0 };
     int[] pitchVectors = { -45, 45 };
     
     float lastYaw = mc.field_1724.field_5982;
     float lastPitch = mc.field_1724.field_6004;
     int minDist = findClosestVector(lastYaw, vectors);
     float maxDist = Math.abs(MathHelper.method_15393(lastYaw) - vectors[minDist]);
     int addMinDist = findClosestVector(lastYaw, addVectors);
     float addMaxDist = Math.abs(MathHelper.method_15393(lastYaw) - addVectors[addMinDist]);
     float countableSpeed = (minDist == -1) ? 1.5F : (1.95F - maxDist * 0.56F / 45.0F);
     if (addMaxDist < 10.0F) countableSpeed += 0.1F - 0.1F * addMaxDist / 10.0F; 
     int pitchMinDist = findClosestVector(lastPitch, pitchVectors);
     float pitchMaxDist = Math.abs(Math.abs(lastPitch) - Math.abs(pitchVectors[pitchMinDist]));
     
     if (pitchMaxDist < 26.0F) {
       countableSpeed = Math.max(1.94F, countableSpeed);
       countableSpeed += 0.05F - pitchMaxDist * 0.05F / 26.0F;
     } 
     
     countableSpeed = Math.min(2.045F, countableSpeed);
     if (mc.field_1724.field_6004 > -55.0F && mc.field_1724.field_6004 < -19.0F) { countableSpeed = 1.91F; }
     else if (mc.field_1724.field_6004 < -55.0F) { countableSpeed = 1.54F; }
      if (mc.field_1724.field_6004 > 19.0F && mc.field_1724.field_6004 < 55.0F) { countableSpeed = 1.8F; }
     else if (mc.field_1724.field_6004 > 55.0F) { countableSpeed = 1.54F; }
     
     return countableSpeed;
   }
   
   private static int findClosestVector(float lastYaw, int[] vectors) {
     int index = 0;
     int minDistIndex = -1;
     float minDist = Float.MAX_VALUE;
     
     for (int vector : vectors) {
       float dist = Math.abs(MathHelper.method_15393(lastYaw) - vector);
       if (dist < minDist) {
         minDist = dist;
         minDistIndex = index;
       } 
       
       index++;
     } 
     
     return minDistIndex;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\combat\BoostUtils.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */