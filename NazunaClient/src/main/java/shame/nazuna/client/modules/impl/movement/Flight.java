package shame.nazuna.client.modules.impl.movement;
 
 import net.minecraft.Vec3d;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class Flight
   extends Module {
   public static Flight INSTANCE = new Flight();
   
   private final FloatSetting speed = new FloatSetting("Скорость", 2.0F, 0.1F, 10.0F, 0.1F);
   
   public Flight() {
     super("Flight", "Полёт", Module.ModuleCategory.MOVEMENT);
     addSettings(new Setting[] { (Setting)this.speed });
   }
 
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null)
       return; 
     double spd = this.speed.get();
     float yaw = (float)Math.toRadians(mc.field_1724.method_36454());
     
     double motionX = 0.0D;
     double motionY = 0.0D;
     double motionZ = 0.0D;
     
     double forward = 0.0D;
     double strafe = 0.0D;
     
     if (mc.field_1690.field_1894.method_1434()) forward++; 
     if (mc.field_1690.field_1881.method_1434()) forward--; 
     if (mc.field_1690.field_1913.method_1434()) strafe++; 
     if (mc.field_1690.field_1849.method_1434()) strafe--;
     
     if (forward != 0.0D || strafe != 0.0D) {
       double angle = Math.atan2(forward, strafe) - 1.5707963267948966D;
       motionX = -Math.sin(yaw + angle) * spd;
       motionZ = Math.cos(yaw + angle) * spd;
     } 
     
     if (mc.field_1690.field_1903.method_1434()) {
       motionY = spd;
     } else if (mc.field_1690.field_1832.method_1434()) {
       motionY = -spd;
     } 
     
     mc.field_1724.method_18799(new Vec3d(motionX, motionY, motionZ));
   }
 
   
   public void onDisable() {
     super.onDisable();
     if (mc.field_1724 != null)
       mc.field_1724.method_18799(Vec3d.field_1353); 
   }
 }

