package shame.nazuna.api.utils.rotate;
 
 import net.minecraft.class_1297;
 import net.minecraft.class_243;
 import net.minecraft.class_3532;
 
 public class Rotation implements QClient {
   private float yaw;
   private float pitch;
   
   
   public Rotation(class_1297 entity) {
     this.yaw = entity.method_36454();
     this.pitch = entity.method_36455();
   }
   
   public float getDelta(Rotation target) {
     float yawDelta = class_3532.method_15393(target.getYaw() - this.yaw);
     float pitchDelta = target.getPitch() - this.pitch;
     return (float)Math.hypot(Math.abs(yawDelta), Math.abs(pitchDelta));
   }
   
   public double getDeltaDouble(Rotation target) {
     double yawDelta = class_3532.method_15393(target.getYaw() - this.yaw);
     double pitchDelta = class_3532.method_15393(target.getPitch() - this.pitch);
     return Math.hypot(yawDelta, pitchDelta);
   }
   
   public static class_5611 camera() {
     return new class_5611(cameraYaw(), cameraPitch());
   }
   
   public static float cameraYaw() {
     return class_3532.method_15393(mc.field_1773.method_19418().method_19330() + (mc.field_1773.method_19418().method_19333() ? '´' : false));
   }
   
   public static float cameraPitch() {
     return (mc.field_1773.method_19418().method_19333() ? -1 : true) * mc.field_1773.method_19418().method_19329();
   }
   
   public static Rotation from(class_1657 player, class_1297 target) {
     class_243 playerPos = player.method_5836(0.0F);
     class_243 targetPos = target.method_19538().method_1031(0.0D, target.method_17682() * 0.5D, 0.0D);
     
     double dx = targetPos.field_1352 - playerPos.field_1352;
     double dy = targetPos.field_1351 - playerPos.field_1351;
     double dz = targetPos.field_1350 - playerPos.field_1350;
     
     double distanceXZ = Math.sqrt(dx * dx + dz * dz);
     
     float yaw = (float)Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
     float pitch = (float)-Math.toDegrees(Math.atan2(dy, distanceXZ));
     
     return new Rotation(yaw, pitch);
   }
   
   public final class_243 toVector() {
     float f = this.pitch * 0.017453292F;
     float g = -this.yaw * 0.017453292F;
     float h = class_3532.method_15362(g);
     float i = class_3532.method_15374(g);
     float j = class_3532.method_15362(f);
     float k = class_3532.method_15374(f);
     return new class_243((i * j), -k, (h * j));
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\rotate\Rotation.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */