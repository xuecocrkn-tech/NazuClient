package shame.nazuna.client.modules.impl.player;
 import net.minecraft.class_1657;
 import net.minecraft.class_1713;
 import net.minecraft.class_1738;
 import net.minecraft.class_1792;
 import net.minecraft.class_1799;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class AutoArmor extends Module {
   public static AutoArmor INSTANCE = new AutoArmor();
   
   private final FloatSetting delay = new FloatSetting("Задержка", 25.0F, 1.0F, 1000.0F, 1.0F);
   private long lastEquipTime = 0L;
   
   public AutoArmor() {
     super("AutoArmor", "Автоматически одевает броню", Module.ModuleCategory.PLAYER);
     addSettings(new Setting[] { (Setting)this.delay });
   }
   
   @EventLink
   public void onEvent(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return;  if (isMoving())
       return; 
     long currentTime = System.currentTimeMillis();
     if ((float)(currentTime - this.lastEquipTime) < this.delay.get())
       return; 
     for (int i = 0; i < 4; i++) {
       class_1799 currentArmor = mc.field_1724.method_31548().method_7372(i);
       
       if (currentArmor.method_7960())
         for (int j = 0; j < 36; j++) {
           class_1799 stack = mc.field_1724.method_31548().method_5438(j);
           
           if (!stack.method_7960()) { class_1792 class_1792 = stack.method_7909(); if (class_1792 instanceof class_1738) { class_1738 armorItem = (class_1738)class_1792;
               if (getArmorSlotIndex(armorItem) == i) {
                 int slotToEquip = j;
                 
                 if (j < 9) {
                   slotToEquip = j + 36;
                 }
                 
                 mc.field_1761.method_2906(0, slotToEquip, 0, class_1713.field_7794, (class_1657)mc.field_1724);
                 this.lastEquipTime = currentTime;
                 return;
               }  }
              }
         
         }  
     } 
   }
   
   private boolean isMoving() {
     return (mc.field_1724.field_3913.field_3905 != 0.0F || mc.field_1724.field_3913.field_3907 != 0.0F);
   }
   
   private int getArmorSlotIndex(class_1738 armor) {
     String itemName = armor.toString().toLowerCase();
     
     if (itemName.contains("helmet") || itemName.contains("skull"))
       return 3; 
     if (itemName.contains("chestplate") || itemName.contains("tunic"))
       return 2; 
     if (itemName.contains("leggings") || itemName.contains("pants"))
       return 1; 
     if (itemName.contains("boots") || itemName.contains("shoes")) {
       return 0;
     }
     
     return 0;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\player\AutoArmor.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */