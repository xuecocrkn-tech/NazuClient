package shame.nazuna.client.modules.impl.combat;
 import net.minecraft.class_1657;
 import net.minecraft.class_1713;
 import net.minecraft.class_1792;
 import net.minecraft.class_1799;
 import net.minecraft.class_1802;
 import net.minecraft.class_2596;
 import net.minecraft.class_2815;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventBinding;
 import shame.nazuna.api.events.implement.EventMoveInput;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.impl.movement.Sprint;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BindSetting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class AutoSwap extends Module {
   public static AutoSwap INSTANCE = new AutoSwap();
   
   private final ModeSetting firstItem = new ModeSetting("Первый предмет", "Руна", new String[] { "Руна", "Тотем", "Шар", "Гепл", "Щит" });
   private final ModeSetting secondItem = new ModeSetting("Второй предмет", "Тотем", new String[] { "Руна", "Тотем", "Шар", "Гепл", "Щит" });
   private final BindSetting swapKey = new BindSetting("Кнопка свапа", -98);
   private final BooleanSetting bypassgrim = new BooleanSetting("Обходить Grim", true);
   
   private int bypassTicks;
   private boolean sprintPaused;
   private int swapCooldown;
   private int targetSlot = -1;
   private boolean needSwap = false;
   
   public AutoSwap() {
     super("AutoSwap", "Быстрая смена предметов в офф-хенде", Module.ModuleCategory.COMBAT);
     addSettings(new Setting[] { (Setting)this.firstItem, (Setting)this.secondItem, (Setting)this.swapKey, (Setting)this.bypassgrim });
   }
 
   
   public void onEnable() {
     this.needSwap = false;
     this.targetSlot = -1;
     this.bypassTicks = 0;
     this.swapCooldown = 0;
     super.onEnable();
   }
   
   @EventLink
   public void onBinding(EventBinding event) {
     if (mc.field_1755 != null)
       return;  if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     if (event.getKey() == this.swapKey.getKey() && 
       this.swapCooldown == 0) {
       this.needSwap = true;
     }
   }
 
   
   @EventLink
   public void onInput(EventMoveInput e) {
     if (this.bypassgrim.isState() && this.bypassTicks > 0) {
       if (mc.field_1724 == null)
         return;  mc.field_1724.method_5728(false);
       e.setForward(0.0F);
       e.setStrafe(0.0F);
       e.setJump(false);
       e.setSneak(false);
     } 
   }
   
   @EventLink
   public void onUpdate(EventUpdate e) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     if (this.swapCooldown > 0) {
       this.swapCooldown--;
     }
     
     if (this.bypassgrim.isState() && this.bypassTicks > 0) {
       mc.field_1724.method_5728(false);
       this.bypassTicks--;
       
       if (this.bypassTicks == 1) {
         performSwap();
       }
       
       if (this.bypassTicks == 0) {
         restoreSprint();
       }
       
       return;
     } 
     if (this.needSwap && this.targetSlot == -1) {
       int slot; this.needSwap = false;
       
       class_1792 offhand = mc.field_1724.method_6079().method_7909();
       class_1792 first = getItem(this.firstItem.getCurrent());
       class_1792 second = getItem(this.secondItem.getCurrent());
       
       int firstSlot = findItemSlot(first);
       int secondSlot = findItemSlot(second);
       
       if (firstSlot == -1 && secondSlot == -1) {
         return;
       }
       if (offhand == first && secondSlot != -1) {
         slot = secondSlot;
       } else if (firstSlot != -1) {
         slot = firstSlot;
       } else {
         slot = secondSlot;
       } 
       
       if (slot == -1)
         return; 
       this.targetSlot = slot;
       
       if (this.bypassgrim.isState()) {
         disableSprint();
         this.bypassTicks = 2;
         this.swapCooldown = 2;
       } else {
         performSwap();
         this.swapCooldown = 2;
       } 
     } 
   }
 
   
   private void performSwap() {
     if (this.targetSlot == -1)
       return; 
     doSwap(this.targetSlot);
     mc.field_1724.field_3944.method_52787((class_2596)new class_2815(0));
     
     this.targetSlot = -1;
   }
   
   private void doSwap(int slot) {
     if (slot >= 36 && slot <= 44) {
       int hotbarSlot = slot - 36;
       mc.field_1761.method_2906(0, 45, hotbarSlot, class_1713.field_7791, (class_1657)mc.field_1724);
     } else {
       mc.field_1761.method_2906(0, slot, 0, class_1713.field_7791, (class_1657)mc.field_1724);
       mc.field_1761.method_2906(0, 45, 0, class_1713.field_7791, (class_1657)mc.field_1724);
       mc.field_1761.method_2906(0, slot, 0, class_1713.field_7791, (class_1657)mc.field_1724);
     } 
   }
 
   
   private int findItemSlot(class_1792 item) {
     for (int i = 9; i < 45; i++) {
       class_1799 stack = mc.field_1724.field_7498.method_7611(i).method_7677();
       if (stack.method_7909() == item) {
         return i;
       }
     } 
     return -1;
   }
   
   private class_1792 getItem(String name) {
     switch (name) { case "Руна": case "Тотем": case "Шар": case "Гепл": case "Щит":  }  return 
 
 
 
 
       
       class_1802.field_8162;
   }
 
   
   private void disableSprint() {
     if (this.sprintPaused) {
       return;
     }
     
     Sprint.pushPause(1000L);
     this.sprintPaused = true;
   }
   
   private void restoreSprint() {
     if (!this.sprintPaused) {
       return;
     }
     
     this.sprintPaused = false;
     Sprint.popPause();
   }
 
   
   public void onDisable() {
     this.bypassTicks = 0;
     this.swapCooldown = 0;
     this.needSwap = false;
     this.targetSlot = -1;
     restoreSprint();
     super.onDisable();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\AutoSwap.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */