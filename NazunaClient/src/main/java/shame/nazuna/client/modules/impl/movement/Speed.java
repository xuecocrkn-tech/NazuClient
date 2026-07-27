package shame.nazuna.client.modules.impl.movement;
 
 import net.minecraft.class_1309;
 import net.minecraft.class_238;
 import net.minecraft.class_243;
 import org.jetbrains.annotations.NotNull;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.combat.PredictUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.impl.combat.Aura;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class Speed
   extends Module implements QClient {
   public static Speed INSTANCE = new Speed();
   
   private final FloatSetting speed = new FloatSetting("Скорость", 1.0F, 0.1F, 2.0F, 0.01F);
   private final FloatSetting radius = new FloatSetting("Радиус", 1.0F, 0.01F, 3.0F, 0.1F);
   private final FloatSetting predict = new FloatSetting("Предикт", 1.0F, 0.0F, 5.0F, 0.1F);
   private final BooleanSetting onlyElytra = new BooleanSetting("Только на элитре", false);
   
   public Speed() {
     super("Speed", "Дополнительное ускорение", Module.ModuleCategory.MOVEMENT);
     addSettings(new Setting[] { (Setting)this.speed, (Setting)this.radius, (Setting)this.predict, (Setting)this.onlyElytra });
   }
   
   @EventLink
   private void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     collisionSpeed();
   }
   
   private void collisionSpeed() {
     Aura aura = ModuleClass.aura;
     if (aura == null || !aura.isEnable())
       return; 
     class_1309 target = aura.getTarget();
     if (target == null || target == mc.field_1724)
       return; 
     if (this.onlyElytra.isState() && !mc.field_1724.method_6128())
       return; 
     class_238 expandedBox = mc.field_1724.method_5829().method_1014(this.radius.getValue().doubleValue());
     
     boolean canSpeed = false;
     
     if (mc.field_1724.method_6128() || target.method_5829().method_994(expandedBox)) {
       if (mc.field_1724.method_6128()) {
         class_243 predictedPos = PredictUtils.predict(target, target.method_19538(), this.predict.getValue().intValue());
         double distanceToPredict = mc.field_1724.method_33571().method_1022(predictedPos);
         double distanceToTarget = mc.field_1724.method_33571().method_1022(target.method_5829().method_1005());
         
         if (distanceToPredict <= 2.5D || distanceToTarget <= 2.5D) {
           canSpeed = true;
         }
       } else {
         canSpeed = true;
       } 
     }
     
     if (canSpeed) {
       class_243 newVelocity = calculateVelocity(target);
       mc.field_1724.method_18799(newVelocity);
     } 
   }
 
 
 
 
   
   @NotNull
   private class_243 calculateVelocity(class_1309 target) {
     class_243 predictedPos = PredictUtils.predict(target, target.method_19538(), this.predict.getValue().intValue());
     double deltaX = predictedPos.field_1352 - mc.field_1724.method_23317();
     double deltaZ = predictedPos.field_1350 - mc.field_1724.method_23321();
     
     float targetYaw = (float)(Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0D);
     double radYaw = Math.toRadians(targetYaw);
     
     double force = 0.072D * this.speed.getValue().doubleValue();
     
     class_243 currentVelocity = mc.field_1724.method_18798();
     
     return new class_243(currentVelocity.field_1352 + 
         -Math.sin(radYaw) * force, currentVelocity.field_1351, currentVelocity.field_1350 + 
         
         Math.cos(radYaw) * force);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\movement\Speed.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */