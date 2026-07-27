package shame.nazuna.client.modules.impl.player;
 
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventBinding;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BindSetting;
 
 public class HelpMessage extends Module {
   public static HelpMessage INSTANCE = new HelpMessage();
   
   private final BindSetting bind = new BindSetting("Бинд", -1);
   
   public HelpMessage() {
     super("HelpMessage", "Отправляет координаты в глобальный чат", Module.ModuleCategory.PLAYER);
     addSettings(new Setting[] { (Setting)this.bind });
   }
   
   @EventLink
   public void onBinding(EventBinding event) {
     if (mc.field_1724 == null || mc.method_1562() == null || mc.field_1755 != null) {
       return;
     }
     
     if (event.getKey() != this.bind.getKey()) {
       return;
     }
     
     int x = mc.field_1724.method_31477();
     int y = mc.field_1724.method_31478();
     int z = mc.field_1724.method_31479();
     mc.method_1562().method_45729("! " + x + " " + y + " " + z);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\player\HelpMessage.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */