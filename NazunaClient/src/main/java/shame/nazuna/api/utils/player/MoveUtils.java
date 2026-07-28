package shame.nazuna.api.utils.player;
 
 import java.util.Objects;
 import net.minecraft.Vec3d;
 import net.minecraft.MinecraftClient;
 import net.minecraft.MathHelper;
 import net.minecraft.ClientPlayerEntity;
 import shame.nazuna.api.utils.input.MovingUtil;
 
 
 public class MoveUtils
 {
   private static final MinecraftClient mc = MinecraftClient.method_1551();
   
   public static void setMotion(double motion) {
     if (mc.field_1724 == null)
       return; 
     double forward = mc.field_1724.field_3913.field_3905;
     double strafe = mc.field_1724.field_3913.field_3907;
     float yaw = mc.field_1724.method_36454();
     
     if (forward == 0.0D && strafe == 0.0D) {
       mc.field_1724.method_18800(0.0D, (mc.field_1724.method_18798()).field_1351, 0.0D);
     } else {
       if (forward != 0.0D) {
         if (strafe > 0.0D) {
           yaw += ((forward > 0.0D) ? -45 : 45);
         } else if (strafe < 0.0D) {
           yaw += ((forward > 0.0D) ? 45 : -45);
         } 
         strafe = 0.0D;
         if (forward > 0.0D) {
           forward = 1.0D;
         } else if (forward < 0.0D) {
           forward = -1.0D;
         } 
       } 
 
       
       double motionX = forward * motion * MathHelper.method_15362((float)Math.toRadians((yaw + 90.0F))) + strafe * motion * MathHelper.method_15374((float)Math.toRadians((yaw + 90.0F)));
       
       double motionZ = forward * motion * MathHelper.method_15374((float)Math.toRadians((yaw + 90.0F))) - strafe * motion * MathHelper.method_15362((float)Math.toRadians((yaw + 90.0F)));
       
       mc.field_1724.method_18800(motionX, (mc.field_1724.method_18798()).field_1351, motionZ);
     } 
   }
   
   public static double getSpeed() {
     if (mc.field_1724 == null) return 0.0D; 
     Vec3d velocity = mc.field_1724.method_18798();
     return Math.sqrt(velocity.field_1352 * velocity.field_1352 + velocity.field_1350 * velocity.field_1350);
   }
   
   public static void setVelocity(double velocity) {
     double[] direction = MovingUtil.calculateDirection(velocity);
     ((ClientPlayerEntity)Objects.<ClientPlayerEntity>requireNonNull(mc.field_1724)).method_18800(direction[0], mc.field_1724.method_18798().method_10214(), direction[1]);
   }
   
   public static void setVelocity(double velocity, double y) {
     double[] direction = MovingUtil.calculateDirection(velocity);
     ((ClientPlayerEntity)Objects.<ClientPlayerEntity>requireNonNull(mc.field_1724)).method_18800(direction[0], y, direction[1]);
   }
   
   public static void strafe() {
     strafe(getSpeed());
   }
   
   public static void strafe(double speed) {
     if (mc.field_1724 == null)
       return; 
     float yaw = mc.field_1724.method_36454();
     double forward = mc.field_1724.field_3913.field_3905;
     double strafe = mc.field_1724.field_3913.field_3907;
     
     if (forward == 0.0D && strafe == 0.0D) {
       mc.field_1724.method_18800(0.0D, (mc.field_1724.method_18798()).field_1351, 0.0D);
       
       return;
     } 
     if (forward != 0.0D) {
       if (strafe > 0.0D) {
         yaw += (forward > 0.0D) ? -45.0F : 45.0F;
       } else if (strafe < 0.0D) {
         yaw += (forward > 0.0D) ? 45.0F : -45.0F;
       } 
       strafe = 0.0D;
       forward = (forward > 0.0D) ? 1.0D : -1.0D;
     } 
     
     double rad = Math.toRadians((yaw + 90.0F));
     double motionX = forward * speed * Math.cos(rad) + strafe * speed * Math.sin(rad);
     double motionZ = forward * speed * Math.sin(rad) - strafe * speed * Math.cos(rad);
     
     mc.field_1724.method_18800(motionX, (mc.field_1724.method_18798()).field_1351, motionZ);
   }
   
   public static boolean isMoving() {
     if (mc.field_1724 == null) return false; 
     return (mc.field_1724.field_3913.field_3905 != 0.0F || mc.field_1724.field_3913.field_3907 != 0.0F);
   }
 }

