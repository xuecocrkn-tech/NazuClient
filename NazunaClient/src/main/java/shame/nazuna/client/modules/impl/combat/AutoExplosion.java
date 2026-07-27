package shame.nazuna.client.modules.impl.combat;
 
 import net.minecraft.Hand;
 import net.minecraft.Entity;
 import net.minecraft.EndCrystalEntity;
 import net.minecraft.PlayerEntity;
 import net.minecraft.SlotActionType;
 import net.minecraft.Item;
 import net.minecraft.ItemStack;
 import net.minecraft.Items;
 import net.minecraft.ItemConvertible;
 import net.minecraft.Blocks;
 import net.minecraft.Block;
 import net.minecraft.BlockPos;
 import net.minecraft.Direction;
 import net.minecraft.Box;
 import net.minecraft.Vec3i;
 import net.minecraft.HitResult;
 import net.minecraft.Vec2f;
 import net.minecraft.Vec3d;
 import net.minecraft.Packet;
 import net.minecraft.CloseHandledScreenC2SPacket;
 import net.minecraft.UpdateSelectedSlotC2SPacket;
 import net.minecraft.PlayerInteractBlockC2SPacket;
 import net.minecraft.BlockHitResult;
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
   
   public BlockPos getTargetPos() {
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
     Packet Packet = event.getPacket(); if (Packet instanceof PlayerInteractBlockC2SPacket) { PlayerInteractBlockC2SPacket packet = (PlayerInteractBlockC2SPacket)Packet;
       BlockHitResult hit = packet.method_12543();
       BlockPos clickedPos = hit.method_17777();
       BlockPos placePos = clickedPos.method_10093(hit.method_17780());
       
       if (isHoldingObsidian() && isInRange(placePos) && !mc.field_1724.method_7357().method_7904(new ItemStack((ItemConvertible)Items.field_8301))) {
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
   
   private void tryPlaceCrystalFast(BlockPos pos) {
     if (this.targetSlot < 0 || this.targetSlot > 8 || !canPlaceCrystal(pos)) {
       return;
     }
     
     rotateTo(Vec3d.method_24953((Vec3i)pos));
     
     this.oldSlot = (mc.field_1724.method_31548()).field_7545;
     mc.method_1562().method_52787((Packet)new UpdateSelectedSlotC2SPacket(this.targetSlot));
     (mc.field_1724.method_31548()).field_7545 = this.targetSlot;
     
     Vec3d hitVec = Vec3d.method_24953((Vec3i)pos).method_1031(0.0D, 0.5D, 0.0D);
     BlockHitResult result = new BlockHitResult(hitVec, Direction.field_11036, pos, false);
     sendInteract(Hand.field_5808, result);
     mc.field_1724.method_6104(Hand.field_5808);
     
     this.needSync = true;
     this.crystalArea = boxFromBlock(pos.method_10084()).method_1014(0.1D);
     this.targetPos = null;
   }
   
   private void processCrystalArea() {
     if (this.crystalArea == null)
       return; 
     for (Entity entity : mc.field_1687.method_8335(null, this.crystalArea)) {
       if (entity instanceof EndCrystalEntity) { EndCrystalEntity crystal = (EndCrystalEntity)entity; if (!crystal.method_5805())
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
   
   private boolean shouldPlaceByRightClick(BlockPos clickedPos) {
     if (mc.field_1724.method_7357().method_7904(new ItemStack((ItemConvertible)Items.field_8301))) return false; 
     if (isHoldingBlockForPlace()) return false;
     
     Block block = mc.field_1687.method_8320(clickedPos).method_26204();
     if (block != Blocks.field_10540 && block != Blocks.field_9987) return false;
     
     return mc.field_1687.method_8320(clickedPos.method_10084()).method_26215();
   }
   
   private boolean placeCrystalFromOffhand(BlockHitResult hit, BlockPos clickedPos) {
     int slot = findScreenSlot(Items.field_8301);
     if (slot == -1 && mc.field_1724.method_6079().method_7909() != Items.field_8301) return false;
     
     boolean swapped = false;
     if (mc.field_1724.method_6079().method_7909() != Items.field_8301) {
       swapSlotToOffhand(slot);
       swapped = true;
     } 
     
     sendInteract(Hand.field_5810, hit);
     mc.field_1724.method_6104(Hand.field_5810);
     this.crystalArea = boxFromBlock(clickedPos.method_10084()).method_1014(0.1D);
     
     if (swapped) {
       swapSlotToOffhand(slot);
       mc.field_1724.field_3944.method_52787((Packet)new CloseHandledScreenC2SPacket(0));
     } 
     return true;
   }
   private void placeObsidianByCrosshair() {
     BlockHitResult hit;
     int obsidianSlot = findScreenSlot(Items.field_8281);
     int crystalSlot = findCrystalSlot();
     if (obsidianSlot == -1 || crystalSlot == -1)
       return;  HitResult HitResult = mc.field_1765; if (HitResult instanceof BlockHitResult) { hit = (BlockHitResult)HitResult; } else { return; }
      if (hit.method_17783() != HitResult.class_240.field_1332)
       return;  if (mc.field_1687.method_8320(hit.method_17777()).method_26215())
       return; 
     BlockPos placePos = hit.method_17777().method_10093(hit.method_17780());
     this.targetPos = placePos;
     this.targetSlot = crystalSlot;
     this.blocked = true;
     
     swapSlotToOffhand(obsidianSlot);
     sendInteract(Hand.field_5810, hit);
     mc.field_1724.method_6104(Hand.field_5810);
     swapSlotToOffhand(obsidianSlot);
     mc.field_1724.field_3944.method_52787((Packet)new CloseHandledScreenC2SPacket(0));
   }
   
   private void attackCrystal(EndCrystalEntity crystal) {
     mc.method_1562().method_52787((Packet)PlayerInteractEntityC2SPacket.method_34206((Entity)crystal, false));
     mc.field_1724.method_6104(Hand.field_5808);
   }
   
   private void sendInteract(Hand hand, BlockHitResult hitResult) {
     this.internalInteract = true;
     try {
       mc.method_1562().method_52787((Packet)new PlayerInteractBlockC2SPacket(hand, hitResult, 0));
     } finally {
       this.internalInteract = false;
     } 
   }
   
   private void rotateTo(Vec3d vec) {
     Vec2f rotation = RotationUtils.getRotations(vec);
     RotationStorage.update(new Rotation(rotation.field_1343, rotation.field_1342), 360.0F, 360.0F, 360.0F, 360.0F, 1, 2, false);
   }
   
   private boolean canPlaceCrystal(BlockPos pos) {
     BlockPos up1 = pos.method_10084();
     BlockPos up2 = pos.method_10086(2);
     
     if (!mc.field_1687.method_8320(up1).method_26215()) return false; 
     if (!mc.field_1687.method_8320(up2).method_26215()) return false;
 
 
     
     Box box = new Box(up1.method_10263(), up1.method_10264(), up1.method_10260(), up1.method_10263() + 1.0D, up1.method_10264() + 2.0D, up1.method_10260() + 1.0D);
 
     
     for (Entity entity : mc.field_1687.method_8335(null, box)) {
       if (!(entity instanceof EndCrystalEntity)) {
         return false;
       }
     } 
     return true;
   }
   
   private int findCrystalSlot() {
     for (int i = 0; i < 9; i++) {
       if (mc.field_1724.method_31548().method_5438(i).method_7909() == Items.field_8301) {
         return i;
       }
     } 
     return -1;
   }
   
   private int findScreenSlot(Item item) {
     for (int i = 9; i < 45; i++) {
       ItemStack stack = mc.field_1724.field_7498.method_7611(i).method_7677();
       if (stack.method_7909() == item) {
         return i;
       }
     } 
     return -1;
   }
   
   private void swapSlotToOffhand(int slot) {
     if (slot >= 36 && slot <= 44) {
       mc.field_1761.method_2906(0, 45, slot - 36, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
       
       return;
     } 
     mc.field_1761.method_2906(0, slot, 0, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
     mc.field_1761.method_2906(0, 45, 0, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
     mc.field_1761.method_2906(0, slot, 0, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
   }
   
   private void restoreSelectedSlot() {
     if (this.oldSlot != -1) {
       mc.method_1562().method_52787((Packet)new UpdateSelectedSlotC2SPacket(this.oldSlot));
       (mc.field_1724.method_31548()).field_7545 = this.oldSlot;
       this.oldSlot = -1;
     } 
   }
   
   private Box boxFromBlock(BlockPos pos) {
     return new Box(pos
         .method_10263(), pos.method_10264(), pos.method_10260(), pos
         .method_10263() + 1.0D, pos.method_10264() + 1.0D, pos.method_10260() + 1.0D);
   }
 
   
   private boolean isHoldingObsidian() {
     return (mc.field_1724.method_6047().method_7909() == Items.field_8281 || mc.field_1724
       .method_6079().method_7909() == Items.field_8281);
   }
   
   private boolean isHoldingBlockForPlace() {
     Item main = mc.field_1724.method_6047().method_7909();
     Item off = mc.field_1724.method_6079().method_7909();
     
     return ((main instanceof net.minecraft.BlockItem && main != Items.field_8575) || (off instanceof net.minecraft.BlockItem && off != Items.field_8575));
   }
 
   
   private boolean isInRange(BlockPos pos) {
     return (mc.field_1724.method_33571().method_1022(Vec3d.method_24953((Vec3i)pos)) <= 4.5D);
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