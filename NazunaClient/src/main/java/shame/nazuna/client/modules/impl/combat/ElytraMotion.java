package shame.nazuna.client.modules.impl.combat;
 import net.minecraft.Entity;
 import net.minecraft.Vec3d;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventMove;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class ElytraMotion extends Module {
   public static ElytraMotion INSTANCE = new ElytraMotion();
   
   public FloatSetting distance = new FloatSetting("Дистанция до игрока", 3.0F, 0.0F, 6.0F, 0.1F);
   public BooleanSetting bypass = new BooleanSetting("Обход", false);
   public ElytraMotion() {
     super("ElytraMotion", "Зависает рядом с игроком на эликах", Module.ModuleCategory.COMBAT);
     addSettings(new Setting[] { (Setting)this.distance, (Setting)this.bypass });
   }
   @EventLink
   public void onMove(EventMove e) {
     if (!isEnable())
       return; 
     Aura aura = ModuleClass.aura;
     if (mc.field_1724 == null || mc.field_1687 == null || aura.getTarget() == null)
       return;  if (mc.field_1724.method_6128() && mc.field_1724.method_5739((Entity)aura.getTarget()) < this.distance.getValue().floatValue())
       if (this.bypass.isState()) {
         float yaw = mc.field_1724.method_36454();
         double rad = Math.toRadians(yaw);
         
         double forward = 0.01D;
         double down = -1.0E-4D;
         
         double moveX = -Math.sin(rad) * forward;
         double moveZ = Math.cos(rad) * forward;
         
         e.setMovePos(new Vec3d(moveX, down, moveZ));
       } else {
         e.setMovePos(Vec3d.field_1353);
       }  
   }
   
   public void onDisable() {}
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\ElytraMotion.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */