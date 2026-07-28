package shame.nazuna.client.modules.impl.movement;
 
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class TpBack
   extends Module {
   public static TpBack INSTANCE = new TpBack();
   
   private boolean isDead = false;
   private boolean waitingForRespawn = false;
   private int tickCounter = 0;
 
   
   public FloatSetting delay = new FloatSetting("Задержка", 5.0F, 1.0F, 20.0F, 1.0F);
   
   public TpBack() {
     super("TpBack", "Возвращает на точки смерти", Module.ModuleCategory.MOVEMENT);
     addSettings(new Setting[] { (Setting)this.delay });
   }
 
   
   @EventLink
   public void onEvent(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     int i = ((mc.field_1724.method_6032() <= 0.0F) ? 1 : 0) & ((mc.field_1724.field_6213 > 0) ? 1 : 0);
     
     if (i != 0 && !this.isDead) {
       this.isDead = true;
       mc.field_1724.field_3944.method_45729("/sethome astra");
       mc.field_1724.method_7331();
       this.waitingForRespawn = true;
       this.tickCounter = 0;
     } 
     
     if (this.waitingForRespawn && i == 0) {
       this.tickCounter++;
       if (this.tickCounter >= this.delay.get()) {
         mc.field_1724.field_3944.method_45729("/home astra");
         this.waitingForRespawn = false;
         this.tickCounter = 0;
       } 
     } 
     
     if (i == 0 && !this.waitingForRespawn)
       this.isDead = false; 
   }
 }

