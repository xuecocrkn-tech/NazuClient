package shame.nazuna.client.modules.impl.player;
 import net.minecraft.class_239;
 import net.minecraft.class_2596;
 import net.minecraft.class_2680;
 import net.minecraft.class_2868;
 import net.minecraft.class_3965;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 
 public class AutoTool extends Module {
   public static AutoTool INSTANCE = new AutoTool();
   
   private final BooleanSetting packet = new BooleanSetting("Пакетный", false);
   private final BooleanSetting silent = new BooleanSetting("Видно только для других людей", false);
   
   private int previousSlot = -1;
   
   public AutoTool() {
     super("AutoTool", "При копании берет лучший предмет", Module.ModuleCategory.PLAYER);
     addSettings(new Setting[] { (Setting)this.packet, (Setting)this.silent });
   }
   
   @EventLink
   public void onEvent(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1761 == null || mc.field_1724.method_7337()) {
       this.previousSlot = -1;
       
       return;
     } 
     if (mc.field_1761.method_2923()) {
       if (this.previousSlot == -1) {
         this.previousSlot = (mc.field_1724.method_31548()).field_7545;
       }
       
       int toolSlot = findOptimalTool();
       if (toolSlot != -1) {
         switchToSlot(toolSlot);
       }
     } else if (this.previousSlot != -1) {
       switchToSlot(this.previousSlot);
       this.previousSlot = -1;
     } 
   }
   
   private void switchToSlot(int slot) {
     if (slot < 0 || slot > 8)
       return;  if ((mc.field_1724.method_31548()).field_7545 == slot)
       return; 
     if (this.silent.isState()) {
       mc.method_1562().method_52787((class_2596)new class_2868(slot));
     } else if (this.packet.isState()) {
       (mc.field_1724.method_31548()).field_7545 = slot;
       mc.method_1562().method_52787((class_2596)new class_2868(slot));
     } else {
       (mc.field_1724.method_31548()).field_7545 = slot;
     } 
   }
   private int findOptimalTool() {
     class_3965 blockHitResult;
     class_239 hitResult = mc.field_1765;
     
     if (hitResult instanceof class_3965) { blockHitResult = (class_3965)hitResult; }
     else { return -1; }
 
     
     class_2680 blockState = mc.field_1687.method_8320(blockHitResult.method_17777());
     return findBestToolSlot(blockState);
   }
   
   private int findBestToolSlot(class_2680 blockState) {
     int bestSlot = -1;
     float bestSpeed = 1.0F;
     
     for (int i = 0; i < 9; i++) {
       float speed = mc.field_1724.method_31548().method_5438(i).method_7924(blockState);
       
       if (speed > bestSpeed) {
         bestSpeed = speed;
         bestSlot = i;
       } 
     } 
     
     return bestSlot;
   }
 
   
   public void onDisable() {
     this.previousSlot = -1;
     super.onDisable();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\player\AutoTool.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */