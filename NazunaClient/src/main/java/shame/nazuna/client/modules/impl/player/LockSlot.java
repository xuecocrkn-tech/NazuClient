package shame.nazuna.client.modules.impl.player;
 import net.minecraft.SlotActionType;
 import net.minecraft.Slot;
 import net.minecraft.Packet;
 import net.minecraft.ClickSlotC2SPacket;
 import net.minecraft.PlayerActionC2SPacket;
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
       return;  if (mc.field_1755 instanceof net.minecraft.HandledScreen)
       return; 
     Packet Packet = event.getPacket(); if (Packet instanceof PlayerActionC2SPacket) { PlayerActionC2SPacket packet = (PlayerActionC2SPacket)Packet;
       if (packet.method_12363() != PlayerActionC2SPacket.class_2847.field_12975 && packet
         .method_12363() != PlayerActionC2SPacket.class_2847.field_12970) {
         return;
       }
       if (isCurrentSlotLockedForDrop()) {
         event.cancel();
         sendLockedMessage((mc.field_1724.method_31548()).field_7545);
       } 
       
       return; }
     
     Packet = event.getPacket(); if (Packet instanceof ClickSlotC2SPacket) { ClickSlotC2SPacket packet = (ClickSlotC2SPacket)Packet; if (packet.method_12195() == SlotActionType.field_7795) {
         int hotbarSlot = getHotbarSlotFromClick(packet.method_12192());
         if (hotbarSlot >= 0 && isHotbarSlotLocked(hotbarSlot)) {
           event.cancel();
           sendLockedMessage(hotbarSlot);
         } 
       }  }
   
   }
   public boolean isCurrentSlotLockedForDrop() {
     if (!isEnable() || mc.field_1724 == null || mc.field_1724.method_6047().method_7960()) return false; 
     if (mc.field_1755 instanceof net.minecraft.HandledScreen) return false; 
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
     
     Slot slot = mc.field_1724.field_7512.method_7611(slotId);
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

