package shame.nazuna.client.modules.impl.player;
 import net.minecraft.Hand;
 import net.minecraft.PlayerEntity;
 import net.minecraft.SlotActionType;
 import net.minecraft.ItemStack;
 import net.minecraft.Items;
 import net.minecraft.Packet;
 import net.minecraft.CloseHandledScreenC2SPacket;
 import net.minecraft.UpdateSelectedSlotC2SPacket;
 import net.minecraft.ClientPlayerEntity;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.impl.movement.Sprint;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class AutoEat extends Module {
   public static final AutoEat INSTANCE = new AutoEat();
   
   private static final String BARITONE_API_CLASS = "baritone.api.BaritoneAPI";
   
   private final FloatSetting hungerBars = new FloatSetting("Плашки голода", 6.0F, 1.0F, 10.0F, 1.0F);
   
   private boolean eating;
   private boolean sprintPaused;
   private boolean swappedFromInventory;
   private int originalSlot = -1;
   private int swappedInventorySlot = -1;
   
   public AutoEat() {
     super("AutoEat", "Автоматически ест при низком голоде", Module.ModuleCategory.PLAYER);
     addSettings(new Setting[] { (Setting)this.hungerBars });
   }
   
   public static boolean shouldSuppressCombat() {
     return (INSTANCE != null && INSTANCE.isEnable() && INSTANCE.eating);
   }
 
   
   public void onDisable() {
     stopEating();
     super.onDisable();
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1761 == null) {
       stopEating();
       
       return;
     } 
     if (mc.field_1755 != null) {
       stopEating();
       
       return;
     } 
     if ((mc.field_1724.method_31549()).field_7477 || mc.field_1724.method_7325()) {
       stopEating();
       
       return;
     } 
     if (!this.eating) {
       if (!shouldStartEating()) {
         return;
       }
       this.eating = true;
       this.originalSlot = (mc.field_1724.method_31548()).field_7545;
     } 
     
     tickEating();
   }
   
   private void tickEating() {
     ClientPlayerEntity player = mc.field_1724;
     if (player == null) {
       stopEating();
       
       return;
     } 
     pauseBaritone();
     
     if (!this.sprintPaused) {
       Sprint.pushPause(0L);
       this.sprintPaused = true;
     } 
     
     mc.field_1690.field_1886.method_23481(false);
     
     if (!needsFood()) {
       if (!player.method_6115()) {
         stopEating();
       }
       
       return;
     } 
     if (!ensureFoodReady()) {
       stopEating();
       
       return;
     } 
     Hand eatingHand = getEatingHand(player);
     if (eatingHand == null) {
       stopEating();
       
       return;
     } 
     mc.field_1690.field_1904.method_23481(true);
     
     if (!player.method_6115() || player.method_6058() != eatingHand) {
       mc.field_1761.method_2919((PlayerEntity)player, eatingHand);
     }
   }
   
   private boolean shouldStartEating() {
     return (needsFood() && !mc.field_1724.method_6115() && (isValidFood(mc.field_1724.method_6079()) || findFoodSlot() != -1));
   }
   
   private boolean needsFood() {
     return (mc.field_1724 != null && mc.field_1724
       .method_7344().method_7586() < 20 && mc.field_1724
       .method_7344().method_7586() <= getFoodThreshold());
   }
   
   private int getFoodThreshold() {
     return Math.round(this.hungerBars.get()) * 2;
   }
   
   private boolean ensureFoodReady() {
     ClientPlayerEntity player = mc.field_1724;
     if (player == null) {
       return false;
     }
     
     if (isValidFood(player.method_6079())) {
       return true;
     }
     
     if (isValidFood(player.method_6047())) {
       return true;
     }
     
     int foodSlot = findFoodSlot();
     if (foodSlot == -1) {
       return false;
     }
     
     if (foodSlot < 9) {
       this.swappedFromInventory = false;
       this.swappedInventorySlot = -1;
       selectHotbarSlot(foodSlot);
       return isValidFood(player.method_6047());
     } 
     
     selectHotbarSlot((this.originalSlot == -1) ? (player.method_31548()).field_7545 : this.originalSlot);
     swapInventorySlotWithHotbar(foodSlot, (player.method_31548()).field_7545);
     this.swappedFromInventory = true;
     this.swappedInventorySlot = foodSlot;
     return isValidFood(player.method_6047());
   }
   
   private Hand getEatingHand(ClientPlayerEntity player) {
     if (player == null) {
       return null;
     }
     if (isValidFood(player.method_6079())) {
       return Hand.field_5810;
     }
     if (isValidFood(player.method_6047())) {
       return Hand.field_5808;
     }
     return null;
   }
   
   private int findFoodSlot() {
     ClientPlayerEntity player = mc.field_1724;
     if (player == null) {
       return -1;
     }
     
     int selected = (player.method_31548()).field_7545;
     if (isValidFood(player.method_31548().method_5438(selected))) {
       return selected;
     }
     int slot;
     for (slot = 0; slot < 9; slot++) {
       if (slot != selected)
       {
         
         if (isValidFood(player.method_31548().method_5438(slot))) {
           return slot;
         }
       }
     } 
     for (slot = 9; slot < 36; slot++) {
       if (isValidFood(player.method_31548().method_5438(slot))) {
         return slot;
       }
     } 
     
     return -1;
   }
   
   private boolean isValidFood(ItemStack stack) {
     if (stack == null || stack.method_7960()) {
       return false;
     }
     
     if (stack.method_31574(Items.field_8463) || stack.method_31574(Items.field_8367) || stack.method_31574(Items.field_8233)) {
       return false;
     }
     
     return (stack.method_7976() == UseAction.field_8950);
   }
   
   private void selectHotbarSlot(int slot) {
     if (mc.field_1724 == null || slot < 0 || slot > 8 || (mc.field_1724.method_31548()).field_7545 == slot) {
       return;
     }
     
     (mc.field_1724.method_31548()).field_7545 = slot;
     if (mc.method_1562() != null) {
       mc.method_1562().method_52787((Packet)new UpdateSelectedSlotC2SPacket(slot));
     }
   }
   
   private void swapInventorySlotWithHotbar(int inventorySlot, int hotbarSlot) {
     if (mc.field_1724 == null || mc.field_1761 == null || inventorySlot < 9 || inventorySlot > 35 || hotbarSlot < 0 || hotbarSlot > 8) {
       return;
     }
     
     mc.field_1761.method_2906(0, inventorySlot, hotbarSlot, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
     if (mc.method_1562() != null) {
       mc.method_1562().method_52787((Packet)new CloseHandledScreenC2SPacket(0));
     }
   }
   
   private void stopEating() {
     if (mc.field_1690 != null) {
       mc.field_1690.field_1904.method_23481(false);
     }
     
     if (this.sprintPaused) {
       Sprint.popPause();
       this.sprintPaused = false;
     } 
     
     restoreHeldItem();
     this.eating = false;
   }
   
   private void restoreHeldItem() {
     if (mc.field_1724 == null || mc.field_1761 == null) {
       resetSwapState();
       
       return;
     } 
     if (this.swappedFromInventory && this.swappedInventorySlot != -1) {
       int hotbarSlot = (this.originalSlot == -1) ? (mc.field_1724.method_31548()).field_7545 : this.originalSlot;
       selectHotbarSlot(hotbarSlot);
       swapInventorySlotWithHotbar(this.swappedInventorySlot, hotbarSlot);
     } 
     
     if (this.originalSlot != -1) {
       selectHotbarSlot(this.originalSlot);
     }
     
     resetSwapState();
   }
   
   private void resetSwapState() {
     this.swappedFromInventory = false;
     this.swappedInventorySlot = -1;
     this.originalSlot = -1;
   }
   
   private void pauseBaritone() {
     try {
       Object baritone = getPrimaryBaritone();
       if (baritone == null) {
         cancelVanillaBreaking();
         
         return;
       } 
       Object pathing = invoke(baritone, "getPathingBehavior");
       if (pathing == null || !Boolean.TRUE.equals(invoke(pathing, "hasPath"))) {
         cancelVanillaBreaking();
         
         return;
       } 
       Object input = invoke(baritone, "getInputOverrideHandler");
       if (input != null) {
         input.getClass().getMethod("clearAllKeys", new Class[0]).invoke(input, new Object[0]);
         Object blockBreakHelper = input.getClass().getMethod("getBlockBreakHelper", new Class[0]).invoke(input, new Object[0]);
         if (blockBreakHelper != null) {
           blockBreakHelper.getClass().getMethod("stopBreakingBlock", new Class[0]).invoke(blockBreakHelper, new Object[0]);
         }
       } 
       pathing.getClass().getMethod("requestPause", new Class[0]).invoke(pathing, new Object[0]);
       cancelVanillaBreaking();
     } catch (Throwable ignored) {
       cancelVanillaBreaking();
     } 
   }
   
   private void cancelVanillaBreaking() {
     try {
       if (mc.field_1761 != null) {
         mc.field_1761.method_2925();
       }
     } catch (Throwable throwable) {}
   }
 
   
   private static Object getPrimaryBaritone() throws ReflectiveOperationException {
     Class<?> apiClass = Class.forName("baritone.api.BaritoneAPI");
     Object provider = apiClass.getMethod("getProvider", new Class[0]).invoke(null, new Object[0]);
     return (provider == null) ? null : provider.getClass().getMethod("getPrimaryBaritone", new Class[0]).invoke(provider, new Object[0]);
   }
   
   private static Object invoke(Object target, String methodName) throws ReflectiveOperationException {
     return target.getClass().getMethod(methodName, new Class[0]).invoke(target, new Object[0]);
   }
 }

