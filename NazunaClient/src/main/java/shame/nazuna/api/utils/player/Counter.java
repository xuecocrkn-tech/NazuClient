package shame.nazuna.api.utils.player;
 
 import net.minecraft.MathHelper;
 import shame.nazuna.api.QClient;
 
 public final class Counter implements QClient {
   public static int getCurrentFPS() {
     return currentFPS;
   }
   public static void updateFPS() {
     int prevFPS = mc.method_47599();
     currentFPS = MathHelper.method_48781(0.5F, prevFPS, currentFPS);
   }
 }

