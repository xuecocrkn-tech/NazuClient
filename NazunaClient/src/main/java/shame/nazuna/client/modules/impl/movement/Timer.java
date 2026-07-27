package shame.nazuna.client.modules.impl.movement;
 
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class Timer
   extends Module {
   public static Timer INSTANCE = new Timer();
   
   public FloatSetting speed = new FloatSetting("Скорость", 2.0F, 0.1F, 10.0F, 0.1F);
   
   public Timer() {
     super("Timer", "Ускоряет время в игре", Module.ModuleCategory.MOVEMENT);
     addSettings(new Setting[] { (Setting)this.speed });
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     mc.field_1724.field_28627 = this.speed.getValue().floatValue();
   }
 
   
   public void onDisable() {
     super.onDisable();
     mc.field_1724.field_28627 = 1.0F;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\movement\Timer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */