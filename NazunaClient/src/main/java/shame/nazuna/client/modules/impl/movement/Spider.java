package shame.nazuna.client.modules.impl.movement;
 
 import net.minecraft.Hand;
 import net.minecraft.PlayerEntity;
 import net.minecraft.SlotActionType;
 import net.minecraft.ItemStack;
 import net.minecraft.Items;
 import net.minecraft.Packet;
 import net.minecraft.UpdateSelectedSlotC2SPacket;
 import net.minecraft.PlayerInteractItemC2SPacket;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.storages.implement.RotationStorage;
 import shame.nazuna.api.utils.rotate.Rotation;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class Spider extends Module {
   public static Spider INSTANCE = new Spider();
   
   private final ModeSetting mode = new ModeSetting("Мод", "Вода", new String[] { "Вода", "SpookyTime" });
   private final BooleanSetting legit = new BooleanSetting("Легит", false);
   
   private int lastSlot = -1;
   private boolean isClimbing = false;
   private int swapBackSlot = -1;
   private int spookyTicks;
   private int chargeSlot = -1;
   private boolean charging;
   
   public Spider() {
     super("Spider", "Позволяет взбираться по стенам", Module.ModuleCategory.MOVEMENT);
     addSettings(new Setting[] { (Setting)this.mode, (Setting)this.legit });
   }
 
   
   public void onDisable() {
     super.onDisable();
     if (mc.field_1724 == null)
       return; 
     if (this.lastSlot != -1 && this.legit.isState()) {
       (mc.field_1724.method_31548()).field_7545 = this.lastSlot;
     }
     
     this.lastSlot = -1;
     this.swapBackSlot = -1;
     this.isClimbing = false;
     this.spookyTicks = 0;
     this.chargeSlot = -1;
     this.charging = false;
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     if (!mc.field_1724.field_5976) {
       stopClimbing();
       
       return;
     } 
     this.isClimbing = true;
     RotationStorage.update(new Rotation(mc.field_1724.method_36454(), 0.0F), 360.0F, 360.0F, 360.0F, 360.0F, 1, 1, false);
     
     if (this.mode.is("SpookyTime")) {
       processSpookyTime();
       
       return;
     } 
     int bucketSlot = getBucketSlot(false);
     if (bucketSlot == -1)
       return; 
     useBucket(bucketSlot, this.legit.isState());
     mc.field_1724.method_18800((mc.field_1724.method_18798()).field_1352, 0.36D, (mc.field_1724.method_18798()).field_1350);
   }
   
   private void stopClimbing() {
     if (this.lastSlot != -1 && this.legit.isState()) {
       (mc.field_1724.method_31548()).field_7545 = this.lastSlot;
       this.lastSlot = -1;
     } 
     
     if (this.swapBackSlot != -1) {
       mc.field_1761.method_2906(0, this.swapBackSlot, 0, SlotActionType.field_7794, (PlayerEntity)mc.field_1724);
       this.swapBackSlot = -1;
     } 
     
     this.isClimbing = false;
     this.spookyTicks = 0;
     this.chargeSlot = -1;
     this.charging = false;
   }
   
   private void processSpookyTime() {
     int bucketSlot = getBucketSlot(true);
     boolean bucketPulse = (this.spookyTicks % 5 == 0);
     boolean boostPulse = (this.spookyTicks % 4 != 3);
     
     keepChargeHeld();
     
     if (bucketSlot != -1 && bucketPulse) {
       useBucket(bucketSlot, false);
       keepChargeHeld();
     } 
     
     double y = boostPulse ? 0.18D : 0.03D;
     mc.field_1724.method_18800((mc.field_1724.method_18798()).field_1352, y, (mc.field_1724.method_18798()).field_1350);
     this.spookyTicks++;
   }
   
   private void useBucket(int bucketSlot, boolean legitMode) {
     if (!legitMode) {
       int currentSlot = (mc.field_1724.method_31548()).field_7545;
       boolean bool = (bucketSlot >= 9 && bucketSlot <= 35);
       
       if (bool) {
         mc.field_1761.method_2906(0, bucketSlot, currentSlot, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
         mc.field_1761.method_2919((PlayerEntity)mc.field_1724, Hand.field_5808);
         mc.field_1761.method_2906(0, bucketSlot, currentSlot, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
       } else {
         mc.field_1724.field_3944.method_52787((Packet)new UpdateSelectedSlotC2SPacket(bucketSlot));
         mc.field_1761.method_2919((PlayerEntity)mc.field_1724, Hand.field_5808);
         mc.field_1724.field_3944.method_52787((Packet)new UpdateSelectedSlotC2SPacket(currentSlot));
       } 
       
       return;
     } 
     boolean isInventorySwap = (bucketSlot >= 9 && bucketSlot <= 35);
     
     if (isInventorySwap) {
       mc.field_1761.method_2906(0, bucketSlot, (mc.field_1724.method_31548()).field_7545, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
       this.swapBackSlot = bucketSlot;
     } else if ((mc.field_1724.method_31548()).field_7545 != bucketSlot) {
       if (this.lastSlot == -1) {
         this.lastSlot = (mc.field_1724.method_31548()).field_7545;
       }
       (mc.field_1724.method_31548()).field_7545 = bucketSlot;
     } 
     
     mc.field_1761.method_2919((PlayerEntity)mc.field_1724, Hand.field_5808);
   }
   
   private void keepChargeHeld() {
     if (isChargeItem(mc.field_1724.method_6079())) {
       if (!this.charging || this.spookyTicks % 12 == 0) {
         sendChargeUsePacket(Hand.field_5810);
       }
       this.charging = true;
       
       return;
     } 
     if (this.chargeSlot == -1 || !isChargeItem(mc.field_1724.method_31548().method_5438(this.chargeSlot))) {
       this.chargeSlot = getChargeHotbarSlot();
       this.charging = false;
     } 
     if (this.chargeSlot == -1)
       return; 
     if ((mc.field_1724.method_31548()).field_7545 != this.chargeSlot) {
       mc.field_1724.field_3944.method_52787((Packet)new UpdateSelectedSlotC2SPacket(this.chargeSlot));
       (mc.field_1724.method_31548()).field_7545 = this.chargeSlot;
       this.charging = false;
     } 
     
     if (!this.charging || this.spookyTicks % 12 == 0) {
       sendChargeUsePacket(Hand.field_5808);
     }
     this.charging = true;
   }
   
   private void sendChargeUsePacket(Hand hand) {
     mc.field_1724.field_3944.method_52787((Packet)new PlayerInteractItemC2SPacket(hand, 0, mc.field_1724.method_36454(), mc.field_1724.method_36455()));
   }
   private int getBucketSlot(boolean allowLava) {
     int i;
     for (i = 0; i < 9; i++) {
       ItemStack stack = mc.field_1724.method_31548().method_5438(i);
       if (isBucket(stack, allowLava)) {
         return i;
       }
     } 
     
     if (!this.legit.isState() || this.mode.is("SpookyTime")) {
       for (i = 9; i < 36; i++) {
         ItemStack stack = mc.field_1724.method_31548().method_5438(i);
         if (isBucket(stack, allowLava)) {
           return i;
         }
       } 
     }
     
     return -1;
   }
   
   private int getChargeHotbarSlot() {
     for (int i = 0; i < 9; i++) {
       if (isChargeItem(mc.field_1724.method_31548().method_5438(i))) {
         return i;
       }
     } 
     return -1;
   }
   
   private boolean isBucket(ItemStack stack, boolean allowLava) {
     return (stack.method_7909() == Items.field_8705 || (allowLava && stack.method_7909() == Items.field_8187));
   }
   
   private boolean isChargeItem(ItemStack stack) {
     return (stack.method_7909() instanceof net.minecraft.BowItem || stack.method_7909() instanceof net.minecraft.TridentItem);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\movement\Spider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */