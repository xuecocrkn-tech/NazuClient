package shame.nazuna.client.modules.impl.player;
 import net.minecraft.class_1713;
 import net.minecraft.class_1735;
 import net.minecraft.class_2596;
 import net.minecraft.class_2813;
 import net.minecraft.class_2846;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventPacket;
 import shame.nazuna.api.utils.chat.ChatUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.ListSetting;
 import shame.nazuna.mixin.SlotAccessor;
 
 public class LockSlot extends Module {
   public static LockSlot INSTANCE = new LockSlot();
   
   private final ListSetting slots = new ListSetting("Слоты", new BooleanSetting[] { new BooleanSetting("1", false), new BooleanSetting("2", false), new BooleanSetting("3", false), new BooleanSetting("4", false), new BooleanSetting("5", false), new BooleanSetting("6", false), new BooleanSetting("7", false), new BooleanSetting("8", false), new BooleanSetting("9", false) });
 
 
 
 
 
 
 
 
 
 
   
   public LockSlot() {
     super("LockSlot", "Блокирует выброс предметов из выбранных слотов", Module.ModuleCategory.PLAYER);
     addSettings(new Setting[] { (Setting)this.slots });
   }
   
   @EventLink
   public void onPacket(EventPacket event) {
     if (mc.field_1724 == null || event.getType() != EventPacket.Type.SEND)
       return;  if (mc.field_1755 instanceof net.minecraft.class_465)
       return; 
     class_2596 class_2596 = event.getPacket(); if (class_2596 instanceof class_2846) { class_2846 packet = (class_2846)class_2596;
       if (packet.method_12363() != class_2846.class_2847.field_12975 && packet
         .method_12363() != class_2846.class_2847.field_12970) {
         return;
       }
       if (isCurrentSlotLockedForDrop()) {
         event.cancel();
         sendLockedMessage((mc.field_1724.method_31548()).field_7545);
       } 
       
       return; }
     
     class_2596 = event.getPacket(); if (class_2596 instanceof class_2813) { class_2813 packet = (class_2813)class_2596; if (packet.method_12195() == class_1713.field_7795) {
         int hotbarSlot = getHotbarSlotFromClick(packet.method_12192());
         if (hotbarSlot >= 0 && isHotbarSlotLocked(hotbarSlot)) {
           event.cancel();
           sendLockedMessage(hotbarSlot);
         } 
       }  }
   
   }
   public boolean isCurrentSlotLockedForDrop() {
     if (!isEnable() || mc.field_1724 == null || mc.field_1724.method_6047().method_7960()) return false; 
     if (mc.field_1755 instanceof net.minecraft.class_465) return false; 
     return isHotbarSlotLocked((mc.field_1724.method_31548()).field_7545);
   }
   
   private boolean isHotbarSlotLocked(int slot) {
     if (slot < 0 || slot >= this.slots.getSettings().size()) return false; 
     return ((BooleanSetting)this.slots.getSettings().get(slot)).isState();
   }
   
   private int getHotbarSlotFromClick(int slotId) {
     if (mc.field_1724 == null || slotId < 0 || slotId >= mc.field_1724.field_7512.field_7761.size()) {
       return -1;
     }
     
     class_1735 slot = mc.field_1724.field_7512.method_7611(slotId);
     SlotAccessor accessor = (SlotAccessor)slot;
     int inventoryIndex = accessor.astra$getIndex();
     if (accessor.astra$getInventory() == mc.field_1724.method_31548() && inventoryIndex >= 0 && inventoryIndex <= 8) {
       return inventoryIndex;
     }
     return -1;
   }
   
   private void sendLockedMessage(int slot) {
     ChatUtils.sendMessage("Выброс предмета из слота " + slot + 1 + " заблокирован");
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\player\LockSlot.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */