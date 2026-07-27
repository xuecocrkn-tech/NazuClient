package shame.nazuna.client.modules.impl.movement;
 
 import java.util.ArrayList;
 import java.util.List;
 import net.minecraft.class_2596;
 import net.minecraft.class_2813;
 import net.minecraft.class_2815;
 import net.minecraft.class_304;
 import net.minecraft.class_3675;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventCloseInv;
 import shame.nazuna.api.events.implement.EventPacket;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.player.MoveUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 
 
 public class InventoryWalk
   extends Module
 {
   public static InventoryWalk INSTANCE = new InventoryWalk();
   
   public ModeSetting mode = new ModeSetting("Обход", "Обычный", new String[] { "Обычный", "Grim" });
   public ModeSetting grimVersion = (new ModeSetting("Версия свапа", "1.21.4", new String[] { "1.21.4", "1.16.5"
       })).visible(() -> Boolean.valueOf(this.mode.is("Grim")));
   
   public int tick = 0;
   private final List<class_2813> pendingPackets = new ArrayList<>();
   private class_2815 pendingClosePacket = null;
   private boolean sprintPaused = false;
   private boolean waitingToClose = false;
   private int delayedFlushTicks = -1;
   private boolean flushingPackets = false;
   
   public InventoryWalk() {
     super("InventoryWalk", "Ходьба с открытым инвентарём", Module.ModuleCategory.MOVEMENT);
     addSettings(new Setting[] { (Setting)this.mode, (Setting)this.grimVersion });
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null)
       return; 
     class_304[] pressedKeys = { mc.field_1690.field_1894, mc.field_1690.field_1881, mc.field_1690.field_1913, mc.field_1690.field_1849, mc.field_1690.field_1903, mc.field_1690.field_1867 };
 
 
 
 
 
 
 
     
     if (this.mode.is("Grim") && this.grimVersion.is("1.21.4") && this.waitingToClose && !MoveUtils.isMoving()) {
       flushQueuedPackets(true);
       this.waitingToClose = false;
       this.tick = 3;
     } 
     
     if (this.mode.is("Grim") && this.grimVersion.is("1.16.5") && this.delayedFlushTicks >= 0) {
       if (this.delayedFlushTicks == 0) {
         flushQueuedPackets(true);
         this.delayedFlushTicks = -1;
         this.tick = 1;
       } else {
         this.delayedFlushTicks--;
       } 
     }
     
     if (this.tick == 0 && !this.pendingPackets.isEmpty() && mc.field_1755 == null && !this.waitingToClose) {
       sendPendingPackets();
     }
     
     if (this.tick != 0) {
       for (class_304 keyBinding : pressedKeys) {
         keyBinding.method_23481(false);
       }
       this.tick--;
       
       if (this.tick == 0 && this.sprintPaused) {
         this.sprintPaused = false;
         Sprint.popPause();
       } 
       
       return;
     } 
     if (mc.field_1755 instanceof net.minecraft.class_408 || mc.field_1755 instanceof net.minecraft.class_498) {
       return;
     }
     
     if (this.mode.is("Grim") && mc.field_1755 instanceof net.minecraft.class_465 && !(mc.field_1755 instanceof net.minecraft.class_490)) {
       return;
     }
     
     if (this.waitingToClose) {
       for (class_304 keyBinding : pressedKeys) {
         keyBinding.method_23481(false);
       }
       
       return;
     } 
     for (class_304 keyBinding : pressedKeys) {
       boolean isKeyPressed = class_3675.method_15987(mc.method_22683().method_4490(), keyBinding.method_1429().method_1444());
       keyBinding.method_23481(isKeyPressed);
     } 
   }
   
   @EventLink
   public void onPacket(EventPacket event) {
     if (event.getType() != EventPacket.Type.SEND || this.flushingPackets) {
       return;
     }
     
     Object packet = event.getPacket();
     if (!this.mode.is("Grim") || !MoveUtils.isMoving() || !(mc.field_1755 instanceof net.minecraft.class_490)) {
       return;
     }
     
     if (packet instanceof class_2813) { class_2813 clickPacket = (class_2813)packet;
       this.pendingPackets.add(clickPacket);
       event.cancel();
       
       return; }
     
     if (packet instanceof class_2815) { class_2815 closePacket = (class_2815)packet;
       this.pendingClosePacket = closePacket;
       if (this.grimVersion.is("1.16.5")) {
         this.delayedFlushTicks = 1;
         this.waitingToClose = false;
       } else {
         this.waitingToClose = true;
       } 
       pauseSprint();
       event.cancel(); }
   
   }
   
   @EventLink
   public void onCloseInv(EventCloseInv eventCloseInv) {
     if (this.mode.is("Grim") && this.grimVersion.is("1.16.5") && MoveUtils.isMoving() && mc.field_1755 instanceof net.minecraft.class_490) {
       this.pendingClosePacket = new class_2815(eventCloseInv.windowId);
       this.delayedFlushTicks = 1;
       pauseSprint();
       this.tick = 1;
       eventCloseInv.cancel();
       
       return;
     } 
     if (this.mode.is("Grim") && !this.waitingToClose) {
       pauseSprint();
       this.tick = 1;
     } 
   }
   
   private void pauseSprint() {
     if (this.sprintPaused) {
       return;
     }
     
     Sprint.pushPause(0L);
     this.sprintPaused = true;
   }
   
   private void sendPendingPackets() {
     if (mc.field_1724 == null || mc.method_1562() == null) {
       this.pendingPackets.clear();
       
       return;
     } 
     this.flushingPackets = true;
     try {
       for (class_2813 packet : this.pendingPackets) {
         mc.method_1562().method_52787((class_2596)packet);
       }
     } finally {
       this.flushingPackets = false;
     } 
     this.pendingPackets.clear();
   }
   
   private void flushQueuedPackets(boolean includeClose) {
     if (mc.field_1724 == null || mc.method_1562() == null) {
       this.pendingPackets.clear();
       this.pendingClosePacket = null;
       
       return;
     } 
     sendPendingPackets();
     
     if (includeClose && this.pendingClosePacket != null) {
       this.flushingPackets = true;
       try {
         mc.method_1562().method_52787((class_2596)this.pendingClosePacket);
       } finally {
         this.flushingPackets = false;
       } 
       this.pendingClosePacket = null;
     } 
   }
   
   public static void stopTick(int ticks) {
     InventoryWalk inventoryWalk = ModuleClass.inventoryWalk;
     if (inventoryWalk != null && inventoryWalk.isEnable()) {
       inventoryWalk.tick = Math.max(inventoryWalk.tick, ticks);
     }
   }
 
   
   public void onDisable() {
     super.onDisable();
     flushQueuedPackets(true);
     if (this.sprintPaused) {
       this.sprintPaused = false;
       Sprint.popPause();
     } 
     this.waitingToClose = false;
     this.delayedFlushTicks = -1;
     this.flushingPackets = false;
     this.tick = 0;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\movement\InventoryWalk.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */