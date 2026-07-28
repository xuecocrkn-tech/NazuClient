package shame.nazuna.client.modules.impl.player;
 import net.minecraft.HitResult;
 import net.minecraft.Packet;
 import net.minecraft.BlockState;
 import net.minecraft.UpdateSelectedSlotC2SPacket;
 import net.minecraft.BlockHitResult;
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
       mc.method_1562().method_52787((Packet)new UpdateSelectedSlotC2SPacket(slot));
     } else if (this.packet.isState()) {
       (mc.field_1724.method_31548()).field_7545 = slot;
       mc.method_1562().method_52787((Packet)new UpdateSelectedSlotC2SPacket(slot));
     } else {
       (mc.field_1724.method_31548()).field_7545 = slot;
     } 
   }
   private int findOptimalTool() {
     BlockHitResult blockHitResult;
     HitResult hitResult = mc.field_1765;
     
     if (hitResult instanceof BlockHitResult) { blockHitResult = (BlockHitResult)hitResult; }
     else { return -1; }
 
     
     BlockState blockState = mc.field_1687.method_8320(blockHitResult.method_17777());
     return findBestToolSlot(blockState);
   }
   
   private int findBestToolSlot(BlockState blockState) {
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

