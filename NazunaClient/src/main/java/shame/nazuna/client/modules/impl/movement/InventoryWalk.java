package shame.nazuna.client.modules.impl.movement;
 
 import java.util.ArrayList;
 import java.util.List;
 import net.minecraft.Packet;
 import net.minecraft.ClickSlotC2SPacket;
 import net.minecraft.CloseHandledScreenC2SPacket;
 import net.minecraft.KeyBinding;
 import net.minecraft.InputUtil;
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
   private final List<ClickSlotC2SPacket> pendingPackets = new ArrayList<>();
   private CloseHandledScreenC2SPacket pendingClosePacket = null;
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
     KeyBinding[] pressedKeys = { mc.field_1690.field_1894, mc.field_1690.field_1881, mc.field_1690.field_1913, mc.field_1690.field_1849, mc.field_1690.field_1903, mc.field_1690.field_1867 };
 
 
 
 
 
 
 
     
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
       for (KeyBinding keyBinding : pressedKeys) {
         keyBinding.method_23481(false);
       }
       this.tick--;
       
       if (this.tick == 0 && this.sprintPaused) {
         this.sprintPaused = false;
         Sprint.popPause();
       } 
       
       return;
     } 
     if (mc.field_1755 instanceof net.minecraft.ChatScreen || mc.field_1755 instanceof net.minecraft.SignEditScreen) {
       return;
     }
     
     if (this.mode.is("Grim") && mc.field_1755 instanceof net.minecraft.HandledScreen && !(mc.field_1755 instanceof net.minecraft.InventoryScreen)) {
       return;
     }
     
     if (this.waitingToClose) {
       for (KeyBinding keyBinding : pressedKeys) {
         keyBinding.method_23481(false);
       }
       
       return;
     } 
     for (KeyBinding keyBinding : pressedKeys) {
       boolean isKeyPressed = InputUtil.method_15987(mc.method_22683().method_4490(), keyBinding.method_1429().method_1444());
       keyBinding.method_23481(isKeyPressed);
     } 
   }
   
   @EventLink
   public void onPacket(EventPacket event) {
     if (event.getType() != EventPacket.Type.SEND || this.flushingPackets) {
       return;
     }
     
     Object packet = event.getPacket();
     if (!this.mode.is("Grim") || !MoveUtils.isMoving() || !(mc.field_1755 instanceof net.minecraft.InventoryScreen)) {
       return;
     }
     
     if (packet instanceof ClickSlotC2SPacket) { ClickSlotC2SPacket clickPacket = (ClickSlotC2SPacket)packet;
       this.pendingPackets.add(clickPacket);
       event.cancel();
       
       return; }
     
     if (packet instanceof CloseHandledScreenC2SPacket) { CloseHandledScreenC2SPacket closePacket = (CloseHandledScreenC2SPacket)packet;
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
     if (this.mode.is("Grim") && this.grimVersion.is("1.16.5") && MoveUtils.isMoving() && mc.field_1755 instanceof net.minecraft.InventoryScreen) {
       this.pendingClosePacket = new CloseHandledScreenC2SPacket(eventCloseInv.windowId);
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
       for (ClickSlotC2SPacket packet : this.pendingPackets) {
         mc.method_1562().method_52787((Packet)packet);
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
         mc.method_1562().method_52787((Packet)this.pendingClosePacket);
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

