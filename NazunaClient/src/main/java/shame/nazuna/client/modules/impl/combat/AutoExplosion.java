package shame.nazuna.client.modules.impl.combat;
 
 import net.minecraft.class_1268;
 import net.minecraft.class_1297;
 import net.minecraft.class_1511;
 import net.minecraft.class_1657;
 import net.minecraft.class_1713;
 import net.minecraft.class_1792;
 import net.minecraft.class_1799;
 import net.minecraft.class_1802;
 import net.minecraft.class_1935;
 import net.minecraft.class_2246;
 import net.minecraft.class_2248;
 import net.minecraft.class_2338;
 import net.minecraft.class_2350;
 import net.minecraft.class_238;
 import net.minecraft.class_2382;
 import net.minecraft.class_239;
 import net.minecraft.class_241;
 import net.minecraft.class_243;
 import net.minecraft.class_2596;
 import net.minecraft.class_2815;
 import net.minecraft.class_2868;
 import net.minecraft.class_2885;
 import net.minecraft.class_3965;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventBinding;
 import shame.nazuna.api.events.implement.EventPacket;
 import shame.nazuna.api.utils.input.KeyBoardUtils;
 import shame.nazuna.api.utils.rotate.RotationUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BindSetting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public final class AutoExplosion extends Module {
 
   
   public static AutoExplosion INSTANCE = new AutoExplosion();
   
   public BindSetting getBind() { return this.bind; } private final BindSetting bind = (new BindSetting("Бинд", -1))
     .visible(() -> Boolean.valueOf(this.modeBaxa.is("По бинду")));
   
   public class_2338 getTargetPos() {
     return this.targetPos;
   public boolean isInternalInteract() { return this.internalInteract; }
   
   public AutoExplosion() {
     super("AutoExplosion", "Автоматически взрывает кристалл", Module.ModuleCategory.COMBAT);
     addSettings(new Setting[] { (Setting)this.modeBaxa, (Setting)this.bind, (Setting)this.explosionOnRightClick, (Setting)this.keepCrystal });
   }
   
   @EventLink
   public void onBinding(EventBinding event) {
     if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1755 != null)
       return;  if (!this.modeBaxa.is("По бинду")) {
       return;
     }
     
     boolean pressed = (this.bind.getKey() == -1) ? ((event.getKey() == KeyBoardUtils.createMouseBind(2))) : ((event.getKey() == this.bind.getKey()));
     
     if (pressed) {
       placeObsidianByCrosshair();
     }
   }
   
   @EventLink
   public void onPacket(EventPacket event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return;  if (event.getType() != EventPacket.Type.SEND)
       return;  if (this.internalInteract)
       return; 
     class_2596 class_2596 = event.getPacket(); if (class_2596 instanceof class_2885) { class_2885 packet = (class_2885)class_2596;
       class_3965 hit = packet.method_12543();
       class_2338 clickedPos = hit.method_17777();
       class_2338 placePos = clickedPos.method_10093(hit.method_17780());
       
       if (isHoldingObsidian() && isInRange(placePos) && !mc.field_1724.method_7357().method_7904(new class_1799((class_1935)class_1802.field_8301))) {
         int crystalSlot = findCrystalSlot();
         if (crystalSlot != -1) {
           this.targetPos = placePos;
           this.targetSlot = crystalSlot;
           this.blocked = true;
         } 
       } 
       
       if (this.explosionOnRightClick.isState() && shouldPlaceByRightClick(clickedPos) && 
         placeCrystalFromOffhand(hit, clickedPos)) {
         event.cancel();
       } }
   
   }
 
   
   @EventLink
   public void onTick(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null) {
       reset();
       
       return;
     } 
     if (this.needSync) {
       this.needSync = false;
       restoreSelectedSlot();
     } 
     
     if (this.targetPos != null) {
       if (mc.field_1687.method_8320(this.targetPos).method_26215()) {
         this.targetPos = null;
       } else if (this.blocked) {
         this.blocked = false;
       } else {
         tryPlaceCrystalFast(this.targetPos);
       } 
     }
     
     processCrystalArea();
   }
   
   private void tryPlaceCrystalFast(class_2338 pos) {
     if (this.targetSlot < 0 || this.targetSlot > 8 || !canPlaceCrystal(pos)) {
       return;
     }
     
     rotateTo(class_243.method_24953((class_2382)pos));
     
     this.oldSlot = (mc.field_1724.method_31548()).field_7545;
     mc.method_1562().method_52787((class_2596)new class_2868(this.targetSlot));
     (mc.field_1724.method_31548()).field_7545 = this.targetSlot;
     
     class_243 hitVec = class_243.method_24953((class_2382)pos).method_1031(0.0D, 0.5D, 0.0D);
     class_3965 result = new class_3965(hitVec, class_2350.field_11036, pos, false);
     sendInteract(class_1268.field_5808, result);
     mc.field_1724.method_6104(class_1268.field_5808);
     
     this.needSync = true;
     this.crystalArea = boxFromBlock(pos.method_10084()).method_1014(0.1D);
     this.targetPos = null;
   }
   
   private void processCrystalArea() {
     if (this.crystalArea == null)
       return; 
     for (class_1297 entity : mc.field_1687.method_8335(null, this.crystalArea)) {
       if (entity instanceof class_1511) { class_1511 crystal = (class_1511)entity; if (!crystal.method_5805())
           continue; 
         if (!crystal.method_5829().method_1006(mc.field_1724.method_33571())) {
           rotateTo(crystal.method_5829().method_1005());
         }
         attackCrystal(crystal);
         this.crystalArea = null;
         if (!this.keepCrystal.isState())
           restoreSelectedSlot(); 
         return; }
     
     } 
   }
   
   private boolean shouldPlaceByRightClick(class_2338 clickedPos) {
     if (mc.field_1724.method_7357().method_7904(new class_1799((class_1935)class_1802.field_8301))) return false; 
     if (isHoldingBlockForPlace()) return false;
     
     class_2248 block = mc.field_1687.method_8320(clickedPos).method_26204();
     if (block != class_2246.field_10540 && block != class_2246.field_9987) return false;
     
     return mc.field_1687.method_8320(clickedPos.method_10084()).method_26215();
   }
   
   private boolean placeCrystalFromOffhand(class_3965 hit, class_2338 clickedPos) {
     int slot = findScreenSlot(class_1802.field_8301);
     if (slot == -1 && mc.field_1724.method_6079().method_7909() != class_1802.field_8301) return false;
     
     boolean swapped = false;
     if (mc.field_1724.method_6079().method_7909() != class_1802.field_8301) {
       swapSlotToOffhand(slot);
       swapped = true;
     } 
     
     sendInteract(class_1268.field_5810, hit);
     mc.field_1724.method_6104(class_1268.field_5810);
     this.crystalArea = boxFromBlock(clickedPos.method_10084()).method_1014(0.1D);
     
     if (swapped) {
       swapSlotToOffhand(slot);
       mc.field_1724.field_3944.method_52787((class_2596)new class_2815(0));
     } 
     return true;
   }
   private void placeObsidianByCrosshair() {
     class_3965 hit;
     int obsidianSlot = findScreenSlot(class_1802.field_8281);
     int crystalSlot = findCrystalSlot();
     if (obsidianSlot == -1 || crystalSlot == -1)
       return;  class_239 class_239 = mc.field_1765; if (class_239 instanceof class_3965) { hit = (class_3965)class_239; } else { return; }
      if (hit.method_17783() != class_239.class_240.field_1332)
       return;  if (mc.field_1687.method_8320(hit.method_17777()).method_26215())
       return; 
     class_2338 placePos = hit.method_17777().method_10093(hit.method_17780());
     this.targetPos = placePos;
     this.targetSlot = crystalSlot;
     this.blocked = true;
     
     swapSlotToOffhand(obsidianSlot);
     sendInteract(class_1268.field_5810, hit);
     mc.field_1724.method_6104(class_1268.field_5810);
     swapSlotToOffhand(obsidianSlot);
     mc.field_1724.field_3944.method_52787((class_2596)new class_2815(0));
   }
   
   private void attackCrystal(class_1511 crystal) {
     mc.method_1562().method_52787((class_2596)class_2824.method_34206((class_1297)crystal, false));
     mc.field_1724.method_6104(class_1268.field_5808);
   }
   
   private void sendInteract(class_1268 hand, class_3965 hitResult) {
     this.internalInteract = true;
     try {
       mc.method_1562().method_52787((class_2596)new class_2885(hand, hitResult, 0));
     } finally {
       this.internalInteract = false;
     } 
   }
   
   private void rotateTo(class_243 vec) {
     class_241 rotation = RotationUtils.getRotations(vec);
     RotationStorage.update(new Rotation(rotation.field_1343, rotation.field_1342), 360.0F, 360.0F, 360.0F, 360.0F, 1, 2, false);
   }
   
   private boolean canPlaceCrystal(class_2338 pos) {
     class_2338 up1 = pos.method_10084();
     class_2338 up2 = pos.method_10086(2);
     
     if (!mc.field_1687.method_8320(up1).method_26215()) return false; 
     if (!mc.field_1687.method_8320(up2).method_26215()) return false;
 
 
     
     class_238 box = new class_238(up1.method_10263(), up1.method_10264(), up1.method_10260(), up1.method_10263() + 1.0D, up1.method_10264() + 2.0D, up1.method_10260() + 1.0D);
 
     
     for (class_1297 entity : mc.field_1687.method_8335(null, box)) {
       if (!(entity instanceof class_1511)) {
         return false;
       }
     } 
     return true;
   }
   
   private int findCrystalSlot() {
     for (int i = 0; i < 9; i++) {
       if (mc.field_1724.method_31548().method_5438(i).method_7909() == class_1802.field_8301) {
         return i;
       }
     } 
     return -1;
   }
   
   private int findScreenSlot(class_1792 item) {
     for (int i = 9; i < 45; i++) {
       class_1799 stack = mc.field_1724.field_7498.method_7611(i).method_7677();
       if (stack.method_7909() == item) {
         return i;
       }
     } 
     return -1;
   }
   
   private void swapSlotToOffhand(int slot) {
     if (slot >= 36 && slot <= 44) {
       mc.field_1761.method_2906(0, 45, slot - 36, class_1713.field_7791, (class_1657)mc.field_1724);
       
       return;
     } 
     mc.field_1761.method_2906(0, slot, 0, class_1713.field_7791, (class_1657)mc.field_1724);
     mc.field_1761.method_2906(0, 45, 0, class_1713.field_7791, (class_1657)mc.field_1724);
     mc.field_1761.method_2906(0, slot, 0, class_1713.field_7791, (class_1657)mc.field_1724);
   }
   
   private void restoreSelectedSlot() {
     if (this.oldSlot != -1) {
       mc.method_1562().method_52787((class_2596)new class_2868(this.oldSlot));
       (mc.field_1724.method_31548()).field_7545 = this.oldSlot;
       this.oldSlot = -1;
     } 
   }
   
   private class_238 boxFromBlock(class_2338 pos) {
     return new class_238(pos
         .method_10263(), pos.method_10264(), pos.method_10260(), pos
         .method_10263() + 1.0D, pos.method_10264() + 1.0D, pos.method_10260() + 1.0D);
   }
 
   
   private boolean isHoldingObsidian() {
     return (mc.field_1724.method_6047().method_7909() == class_1802.field_8281 || mc.field_1724
       .method_6079().method_7909() == class_1802.field_8281);
   }
   
   private boolean isHoldingBlockForPlace() {
     class_1792 main = mc.field_1724.method_6047().method_7909();
     class_1792 off = mc.field_1724.method_6079().method_7909();
     
     return ((main instanceof net.minecraft.class_1747 && main != class_1802.field_8575) || (off instanceof net.minecraft.class_1747 && off != class_1802.field_8575));
   }
 
   
   private boolean isInRange(class_2338 pos) {
     return (mc.field_1724.method_33571().method_1022(class_243.method_24953((class_2382)pos)) <= 4.5D);
   }
   
   private void reset() {
     if (this.oldSlot != -1 && mc.field_1724 != null && mc.method_1562() != null) {
       restoreSelectedSlot();
     }
     this.targetPos = null;
     this.targetSlot = -1;
     this.needSync = false;
     this.crystalArea = null;
     this.blocked = false;
     this.internalInteract = false;
   }
 
   
   public void onEnable() {
     super.onEnable();
     reset();
   }
 
   
   public void onDisable() {
     super.onDisable();
     reset();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\AutoExplosion.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */