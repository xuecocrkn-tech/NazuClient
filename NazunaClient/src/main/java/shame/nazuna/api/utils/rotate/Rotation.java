package shame.nazuna.api.utils.rotate;
 
 import net.minecraft.Entity;
 import net.minecraft.Vec3d;
 import net.minecraft.MathHelper;
 
 public class Rotation implements QClient {
   private float yaw;
   private float pitch;
   
   
   public Rotation(Entity entity) {
     this.yaw = entity.method_36454();
     this.pitch = entity.method_36455();
   }
   
   public float getDelta(Rotation target) {
     float yawDelta = MathHelper.method_15393(target.getYaw() - this.yaw);
     float pitchDelta = target.getPitch() - this.pitch;
     return (float)Math.hypot(Math.abs(yawDelta), Math.abs(pitchDelta));
   }
   
   public double getDeltaDouble(Rotation target) {
     double yawDelta = MathHelper.method_15393(target.getYaw() - this.yaw);
     double pitchDelta = MathHelper.method_15393(target.getPitch() - this.pitch);
     return Math.hypot(yawDelta, pitchDelta);
   }
   
   public static Vector2f camera() {
     return new Vector2f(cameraYaw(), cameraPitch());
   }
   
   public static float cameraYaw() {
     return MathHelper.method_15393(mc.field_1773.method_19418().method_19330() + (mc.field_1773.method_19418().method_19333() ? '´' : false));
   }
   
   public static float cameraPitch() {
     return (mc.field_1773.method_19418().method_19333() ? -1 : true) * mc.field_1773.method_19418().method_19329();
   }
   
   public static Rotation from(PlayerEntity player, Entity target) {
     Vec3d playerPos = player.method_5836(0.0F);
     Vec3d targetPos = target.method_19538().method_1031(0.0D, target.method_17682() * 0.5D, 0.0D);
     
     double dx = targetPos.field_1352 - playerPos.field_1352;
     double dy = targetPos.field_1351 - playerPos.field_1351;
     double dz = targetPos.field_1350 - playerPos.field_1350;
     
     double distanceXZ = Math.sqrt(dx * dx + dz * dz);
     
     float yaw = (float)Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
     float pitch = (float)-Math.toDegrees(Math.atan2(dy, distanceXZ));
     
     return new Rotation(yaw, pitch);
   }
   
   public final Vec3d toVector() {
     float f = this.pitch * 0.017453292F;
     float g = -this.yaw * 0.017453292F;
     float h = MathHelper.method_15362(g);
     float i = MathHelper.method_15374(g);
     float j = MathHelper.method_15362(f);
     float k = MathHelper.method_15374(f);
     return new Vec3d((i * j), -k, (h * j));
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\rotate\Rotation.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */