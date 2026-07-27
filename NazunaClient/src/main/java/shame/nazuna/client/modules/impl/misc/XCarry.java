package shame.nazuna.client.modules.impl.misc;
 
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventPacket;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 
 public class XCarry
   extends Module
 {
   public static XCarry INSTANCE = new XCarry();
   public BooleanSetting autoDisable = new BooleanSetting("Авто выкл", true);
   
   private boolean wasInInventory = false;
   
   public XCarry() {
     super("XCarry", "Дополнительные слоты", Module.ModuleCategory.MISC);
     addSettings(new Setting[] { (Setting)this.autoDisable });
   }
   
   @EventLink
   public void onPacket(EventPacket event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     if (event.getPacket() instanceof net.minecraft.class_2815 && mc.field_1755 instanceof net.minecraft.class_490) {
       event.cancel();
       this.wasInInventory = true;
     } 
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     if (this.wasInInventory && mc.field_1755 == null) {
       if (this.autoDisable.isState()) {
         toggle();
       }
       this.wasInInventory = false;
     } 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\misc\XCarry.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */