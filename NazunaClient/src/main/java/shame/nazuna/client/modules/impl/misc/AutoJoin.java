package shame.nazuna.client.modules.impl.misc;
 import net.minecraft.Hand;
 import net.minecraft.PlayerEntity;
 import net.minecraft.ScreenHandler;
 import net.minecraft.SlotActionType;
 import net.minecraft.Slot;
 import net.minecraft.Packet;
 import net.minecraft.UpdateSelectedSlotC2SPacket;
 import net.minecraft.Screen;
 import net.minecraft.GenericContainerScreen;
 import net.minecraft.GameMessageS2CPacket;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventPacket;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.utils.chat.ChatUtils;
 import shame.nazuna.api.utils.math.TimerUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public final class AutoJoin extends Module {
   public static AutoJoin INSTANCE = new AutoJoin();
   
   private static final long CLICK_DELAY_MS = 30L;
   private static final int NEXT_PAGE_SLOT = 44;
   private static final int MAX_PAGE_SWITCHES = 5;
   private final FloatSetting grief = new FloatSetting("Гриф", 5.0F, 1.0F, 64.0F, 1.0F);
   
   private final TimerUtils clickTimer = new TimerUtils();
   private final TimerUtils compassTimer = new TimerUtils();
   
   private boolean joining;
   private int pageSwitches;
   private int targetGrief;
   
   public AutoJoin() {
     super("AutoJoin", "Автоматически заходит на выбранный гриф", Module.ModuleCategory.MISC);
     addSettings(new Setting[] { (Setting)this.grief });
   }
   
   public void startJoinTo(int griefId) {
     this.grief.setValue(griefId);
     if (!isEnable()) {
       toggle();
       return;
     } 
     startJoin();
   }
 
   
   public void onEnable() {
     super.onEnable();
     startJoin();
   }
 
   
   public void onDisable() {
     this.joining = false;
     this.pageSwitches = 0;
     super.onDisable();
   }
 
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (!this.joining || mc.field_1724 == null || mc.field_1687 == null || mc.field_1761 == null)
       return; 
     if (!(mc.field_1755 instanceof GenericContainerScreen)) {
       openServerSelector(false);
       
       return;
     } 
     handleServerMenu();
   }
   @EventLink
   public void onPacket(EventPacket event) {
     GameMessageS2CPacket packet;
     if (!this.joining || mc.field_1724 == null || mc.field_1687 == null || event.getType() != EventPacket.Type.RECEIVE)
       return; 
     if (event.getPacket() instanceof net.minecraft.GameJoinS2CPacket) {
       ChatUtils.sendMessage("Вход на гриф #" + this.targetGrief + ": успешно");
       this.joining = false;
       this.pageSwitches = 0;
       
       return;
     } 
     Packet Packet = event.getPacket(); if (Packet instanceof GameMessageS2CPacket) { packet = (GameMessageS2CPacket)Packet; }
     else { return; }
      String message = packet.comp_763().getString();
     if (message.contains("Подождите несколько секунд перед повторным подключением")) {
       event.cancel();
       
       return;
     } 
     if (message.contains("К сожалению сервер переполнен")) {
       event.cancel();
       ChatUtils.sendMessage("Вход на гриф #" + this.targetGrief + ": неудачно");
       
       return;
     } 
     openServerSelector(false);
   }
   
   private void startJoin() {
     this.joining = true;
     this.pageSwitches = 0;
     this.targetGrief = Math.round(this.grief.get());
     this.clickTimer.reset();
     this.compassTimer.reset();
     
     if (mc.field_1724 != null && mc.field_1687 != null) {
       openServerSelector(true);
     }
   }
   
   private void openServerSelector(boolean force) {
     if (!force && !this.compassTimer.finished(30L))
       return; 
     if (mc.field_1724 == null || mc.field_1761 == null || mc.method_1562() == null)
       return; 
     int previousSlot = (mc.field_1724.method_31548()).field_7545;
     int slot = findCompassSlot();
     if (slot == -1) {
       return;
     }
     
     this.pageSwitches = 0;
     (mc.field_1724.method_31548()).field_7545 = slot;
     mc.method_1562().method_52787((Packet)new UpdateSelectedSlotC2SPacket(slot));
     mc.field_1761.method_2919((PlayerEntity)mc.field_1724, Hand.field_5808);
     (mc.field_1724.method_31548()).field_7545 = previousSlot;
     mc.method_1562().method_52787((Packet)new UpdateSelectedSlotC2SPacket(previousSlot));
     this.compassTimer.reset();
   }
   
   private int findCompassSlot() {
     if (mc.field_1724 == null) return -1;
     
     for (int i = 0; i < 9; i++) {
       if (mc.field_1724.method_31548().method_5438(i).method_7909() == Items.field_8251) {
         return i;
       }
     } 
     
     return -1;
   }
   private void handleServerMenu() {
     GenericContainerScreen screen;
     Screen Screen = mc.field_1755; if (Screen instanceof GenericContainerScreen) { screen = (GenericContainerScreen)Screen; } else { return; }
      if (!this.clickTimer.finished(30L))
       return; 
     String title = screen.method_25440().getString();
     ScreenHandler handler = screen.method_17577();
     
     if (title.contains("Выбор сервера")) {
       clickSlot(handler, 21);
       this.pageSwitches = 0;
       this.clickTimer.reset();
       
       return;
     } 
     if (clickTargetGriefIfVisible(handler)) {
       return;
     }
     
     if (this.targetGrief > 36 && this.pageSwitches < 5) {
       Slot nextPageSlot = getSlot(handler, 44);
       if (nextPageSlot != null && nextPageSlot.method_7681()) {
         clickSlot(handler, 44);
         this.pageSwitches++;
         this.clickTimer.reset();
       } 
     } 
   }
   
   private boolean clickTargetGriefIfVisible(ScreenHandler handler) {
     String targetName = "ГРИФ #" + this.targetGrief + " (1.16.5+)";
     String targetPrefix = "ГРИФ #" + this.targetGrief;
     
     for (int slot = 0; slot < handler.field_7761.size(); slot++) {
       Slot containerSlot = handler.method_7611(slot);
       if (containerSlot != null && containerSlot.method_7681()) {
         
         String itemName = containerSlot.method_7677().method_7964().getString();
         if (itemName.equalsIgnoreCase(targetName) || itemName.toUpperCase().contains(targetPrefix)) {
           clickSlot(handler, slot);
           this.pageSwitches = 0;
           this.clickTimer.reset();
           return true;
         } 
       } 
     } 
     return false;
   }
   
   private void clickSlot(ScreenHandler handler, int slot) {
     if (mc.field_1724 == null || mc.field_1761 == null)
       return;  if (slot < 0 || slot >= handler.field_7761.size())
       return; 
     mc.field_1761.method_2906(handler.field_7763, slot, 0, SlotActionType.field_7790, (PlayerEntity)mc.field_1724);
   }
   
   private Slot getSlot(ScreenHandler handler, int slot) {
     if (slot < 0 || slot >= handler.field_7761.size()) return null; 
     return handler.method_7611(slot);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\misc\AutoJoin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */