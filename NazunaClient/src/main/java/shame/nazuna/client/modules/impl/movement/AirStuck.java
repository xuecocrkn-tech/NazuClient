package shame.nazuna.client.modules.impl.movement;
 import net.minecraft.class_1304;
 import net.minecraft.class_1657;
 import net.minecraft.class_1713;
 import net.minecraft.class_1799;
 import net.minecraft.class_243;
 import net.minecraft.class_2596;
 import net.minecraft.class_2815;
 import net.minecraft.class_2828;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventMove;
 import shame.nazuna.api.events.implement.EventPacket;
 import shame.nazuna.api.utils.network.NetworkUtils;
 import shame.nazuna.api.utils.player.InventoryUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class AirStuck extends Module {
   public static AirStuck INSTANCE = new AirStuck();
   
   private final ModeSetting mode = new ModeSetting("Мод", "Обычный", new String[] { "Обычный", "LonyGrief" });
   private final BooleanSetting cancelPackets = new BooleanSetting("Отменять пакеты", true);
   private final BooleanSetting swapElytra = new BooleanSetting("Свапать элитру", true);
   
   private class_243 freezePosition = class_243.field_1353;
   private boolean frozen = false;
   
   public AirStuck() {
     super("AirStuck", "Зависает в воздухе", Module.ModuleCategory.MOVEMENT);
     addSettings(new Setting[] { (Setting)this.mode, (Setting)this.cancelPackets, (Setting)this.swapElytra });
   }
 
   
   public void onEnable() {
     this.frozen = false;
     
     if (mc.field_1724 != null && this.swapElytra.isState()) {
       swapChestEquipment();
     }
     
     if (mc.field_1724 != null && this.mode.is("Обычный")) {
       this.freezePosition = mc.field_1724.method_19538();
       this.frozen = true;
     } 
     
     super.onEnable();
   }
 
   
   public void onDisable() {
     this.frozen = false;
     super.onDisable();
   }
   
   private void swapChestEquipment() {
     class_1799 chestStack = mc.field_1724.method_6118(class_1304.field_6174);
     
     if (!chestStack.method_31574(class_1802.field_8833)) {
       return;
     }
     
     int chestplateSlot = InventoryUtils.findBestChestplateSlot();
     if (chestplateSlot != -1) {
       doSwap(chestplateSlot);
     }
   }
   
   private void doSwap(int slot) {
     if (slot >= 0 && slot < 9) {
       mc.field_1761.method_2906(0, 6, slot, class_1713.field_7791, (class_1657)mc.field_1724);
     } else {
       mc.field_1761.method_2906(0, slot, 0, class_1713.field_7791, (class_1657)mc.field_1724);
       mc.field_1761.method_2906(0, 6, 0, class_1713.field_7791, (class_1657)mc.field_1724);
       mc.field_1761.method_2906(0, slot, 0, class_1713.field_7791, (class_1657)mc.field_1724);
     } 
     
     mc.field_1724.field_3944.method_52787((class_2596)new class_2815(0));
   }
   
   @EventLink
   public void onMove(EventMove e) {
     if (mc.field_1724 == null)
       return; 
     if (this.mode.is("LonyGrief") && !this.frozen && 
       mc.field_1724.field_6017 > 0.0F && (mc.field_1724.method_18798()).field_1351 < 0.0D) {
       this.freezePosition = mc.field_1724.method_19538();
       this.frozen = true;
     } 
 
     
     if (this.frozen) {
       e.setMovePos(class_243.field_1353);
       mc.field_1724.method_5814(this.freezePosition.field_1352, this.freezePosition.field_1351, this.freezePosition.field_1350);
       mc.field_1724.method_18800(0.0D, 0.0D, 0.0D);
     } 
   }
   
   @EventLink
   public void onPacket(EventPacket e) {
     if (!this.frozen || e.getType() != EventPacket.Type.SEND)
       return; 
     class_2596 class_2596 = e.getPacket(); if (class_2596 instanceof class_2828) { class_2828 packet = (class_2828)class_2596;
       if (this.cancelPackets.isState()) {
         e.cancel();
       } else {
         e.cancel();
         NetworkUtils.sendSilentPacket((class_2596)createFrozenPacket(packet));
       }  }
   
   }
   
   private class_2828 createFrozenPacket(class_2828 packet) {
     boolean onGround = packet.method_12273();
     boolean horizontalCollision = packet.method_61225();
     
     if (packet.method_36171() && packet.method_36172()) {
       return (class_2828)new class_2828.class_2830(this.freezePosition.field_1352, this.freezePosition.field_1351, this.freezePosition.field_1350, packet
 
 
           
           .method_12271(mc.field_1724.method_36454()), packet
           .method_12270(mc.field_1724.method_36455()), onGround, horizontalCollision);
     }
 
 
 
     
     if (packet.method_36171()) {
       return (class_2828)new class_2828.class_2829(this.freezePosition.field_1352, this.freezePosition.field_1351, this.freezePosition.field_1350, onGround, horizontalCollision);
     }
 
 
 
 
 
 
     
     if (packet.method_36172()) {
       return (class_2828)new class_2828.class_2831(packet
           .method_12271(mc.field_1724.method_36454()), packet
           .method_12270(mc.field_1724.method_36455()), onGround, horizontalCollision);
     }
 
 
 
     
     return (class_2828)new class_2828.class_5911(onGround, horizontalCollision);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\movement\AirStuck.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */