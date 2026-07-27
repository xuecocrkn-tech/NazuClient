package shame.nazuna.api.utils.player;
 import java.util.Arrays;
 import java.util.List;
 import net.minecraft.Hand;
 import net.minecraft.StatusEffectInstance;
 import net.minecraft.StatusEffects;
 import net.minecraft.PlayerEntity;
 import net.minecraft.Item;
 import net.minecraft.ItemStack;
 import net.minecraft.Items;
 import net.minecraft.Block;
 import net.minecraft.Packet;
 import net.minecraft.UpdateSelectedSlotC2SPacket;
 import net.minecraft.EntityAttributes;
 import net.minecraft.Registries;
 import net.minecraft.AttributeModifiersComponent;
 import net.minecraft.DataComponentTypes;
 import org.jetbrains.annotations.NotNull;
 
 public final class HotbarUtil implements QClient {
   private HotbarUtil() {
     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   } private static int cachedSlot = -1;
   
   public static int getItemCount(Item item) {
     if (mc.field_1724 == null) return 0;
     
     int counter = 0;
     for (int i = 0; i < mc.field_1724.method_31548().method_5439(); i++) {
       ItemStack stack = mc.field_1724.method_31548().method_5438(i);
       if (stack.method_31574(item)) counter += stack.method_7947(); 
     } 
     return counter;
   }
   
   public static SlotSearchResult getAxe() {
     return findBest(itemStack -> itemStack.method_7909() instanceof net.minecraft.AxeItem, false);
   }
   
   public static SlotSearchResult getAxeHotBar() {
     return findBest(itemStack -> itemStack.method_7909() instanceof net.minecraft.AxeItem, true);
   }
   
   public static SlotSearchResult getPickAxe() {
     return findBest(itemStack -> itemStack.method_7909() instanceof net.minecraft.PickaxeItem, false);
   }
   
   public static SlotSearchResult getPickAxeHotbar() {
     return getPickAxeHotBar();
   }
   
   public static SlotSearchResult getPickAxeHotBar() {
     return findBest(itemStack -> itemStack.method_7909() instanceof net.minecraft.PickaxeItem, true);
   }
   
   public static SlotSearchResult getSword() {
     return findBest(itemStack -> itemStack.method_7909() instanceof net.minecraft.SwordItem, false);
   }
   
   public static SlotSearchResult getSwordHotBar() {
     return findBest(itemStack -> itemStack.method_7909() instanceof net.minecraft.SwordItem, true);
   }
   
   public static SlotSearchResult getSkull() {
     return findInHotBar(stack -> (stack.method_31574(Items.field_8398) || stack.method_31574(Items.field_8791) || stack.method_31574(Items.field_8681) || stack.method_31574(Items.field_8575) || stack.method_31574(Items.field_8470)));
   }
 
 
 
 
   
   public static int getElytra() {
     if (mc.field_1724 == null) return -1;
     
     for (ItemStack stack : (mc.field_1724.method_31548()).field_7548) {
       if (stack.method_31574(Items.field_8833) && stack.method_7919() < stack.method_7936() - 1) {
         return -2;
       }
     } 
     
     for (int i = 0; i < 36; i++) {
       ItemStack stack = mc.field_1724.method_31548().method_5438(i);
       if (stack.method_31574(Items.field_8833) && stack.method_7919() < stack.method_7936() - 1) {
         return (i < 9) ? (i + 36) : i;
       }
     } 
     return -1;
   }
   
   public static SlotSearchResult findInHotBar(Searcher searcher) {
     if (mc.field_1724 != null) {
       if (searcher.isValid(mc.field_1724.method_6079())) {
         return SlotSearchResult.inOffhand(mc.field_1724.method_6079());
       }
       
       for (int i = 0; i < 9; i++) {
         ItemStack stack = mc.field_1724.method_31548().method_5438(i);
         if (searcher.isValid(stack)) return new SlotSearchResult(i, true, stack); 
       } 
     } 
     return SlotSearchResult.notFound();
   }
   
   public static SlotSearchResult findItemInHotBar(List<Item> items) {
     return findInHotBar(stack -> items.contains(stack.method_7909()));
   }
   
   public static SlotSearchResult findItemInHotBar(Item... items) {
     return findItemInHotBar(Arrays.asList(items));
   }
   
   public static SlotSearchResult findInInventory(Searcher searcher) {
     if (mc.field_1724 != null)
       for (int i = 35; i >= 0; i--) {
         ItemStack stack = mc.field_1724.method_31548().method_5438(i);
         if (searcher.isValid(stack)) return new SlotSearchResult(i, true, stack);
       
       }  
     return SlotSearchResult.notFound();
   }
   
   public static SlotSearchResult findItemInInventory(List<Item> items) {
     return findInInventory(stack -> items.contains(stack.method_7909()));
   }
   
   public static SlotSearchResult findItemInInventory(Item... items) {
     return findItemInInventory(Arrays.asList(items));
   }
   
   public static SlotSearchResult findBlockInHotBar(@NotNull List<Block> blocks) {
     return findItemInHotBar(blocks.stream().map(Block::method_8389).toList());
   }
   
   public static SlotSearchResult findBlockInHotBar(Block... blocks) {
     return findItemInHotBar(Arrays.<Block>stream(blocks).map(Block::method_8389).toList());
   }
   
   public static SlotSearchResult findBlockInInventory(@NotNull List<Block> blocks) {
     return findItemInInventory(blocks.stream().map(Block::method_8389).toList());
   }
   
   public static SlotSearchResult findBlockInInventory(Block... blocks) {
     return findItemInInventory(Arrays.<Block>stream(blocks).map(Block::method_8389).toList());
   }
   
   public static void saveSlot() {
     if (mc.field_1724 != null) cachedSlot = (mc.field_1724.method_31548()).field_7545; 
   }
   
   public static void returnSlot() {
     if (cachedSlot != -1) switchTo(cachedSlot); 
     cachedSlot = -1;
   }
   
   public static void saveAndSwitchTo(int slot) {
     saveSlot();
     switchTo(slot);
   }
   
   public static void switchTo(int slot) {
     if (mc.field_1724 == null || mc.method_1562() == null || slot < 0 || slot > 8)
       return;  if ((mc.field_1724.method_31548()).field_7545 == slot)
       return;  (mc.field_1724.method_31548()).field_7545 = slot;
     mc.method_1562().method_52787((Packet)new UpdateSelectedSlotC2SPacket(slot));
   }
   
   public static void switchToSilent(int slot) {
     if (mc.field_1724 == null || mc.method_1562() == null || slot < 0 || slot > 8)
       return;  mc.method_1562().method_52787((Packet)new UpdateSelectedSlotC2SPacket(slot));
   }
   
   public static SlotSearchResult getAntiWeaknessItem() {
     if (mc.field_1724 == null) return SlotSearchResult.notFound();
     
     Item mainHand = mc.field_1724.method_6047().method_7909();
     if (mainHand instanceof net.minecraft.SwordItem || mainHand instanceof net.minecraft.PickaxeItem || mainHand instanceof net.minecraft.AxeItem || mainHand instanceof net.minecraft.ShovelItem)
     {
 
       
       return new SlotSearchResult((mc.field_1724.method_31548()).field_7545, true, mc.field_1724.method_6047());
     }
     
     return findInHotBar(stack -> (stack.method_7909() instanceof net.minecraft.SwordItem || stack.method_7909() instanceof net.minecraft.PickaxeItem || stack.method_7909() instanceof net.minecraft.AxeItem || stack.method_7909() instanceof net.minecraft.ShovelItem));
   }
 
 
 
   
   public static float getHitDamage(@NotNull ItemStack weapon, PlayerEntity entity) {
     if (mc.field_1724 == null || mc.field_1687 == null) return 0.0F;
     
     float baseDamage = getBaseAttackDamage(weapon);
     
     if (mc.field_1724.field_6017 > 0.0F) baseDamage += baseDamage / 2.0F;
     
     if (mc.field_1724.method_6059(StatusEffects.field_5910)) {
       int strength = ((StatusEffectInstance)Objects.<StatusEffectInstance>requireNonNull(mc.field_1724.method_6112(StatusEffects.field_5910))).method_5578() + 1;
       baseDamage += 3.0F * strength;
     } 
     
     return DamageUtil.method_5496((LivingEntity)entity, baseDamage, mc.field_1687.method_48963().method_48830(), entity.method_6096(), 
         (float)entity.method_45325(EntityAttributes.field_23725));
   }
   
   public static SlotSearchResult findBedInHotBar() {
     return findInHotBar(stack -> stack.method_7909() instanceof net.minecraft.BedItem);
   }
   
   public static SlotSearchResult findBed() {
     return findInInventory(stack -> stack.method_7909() instanceof net.minecraft.BedItem);
   }
   
   public static Item getItem(String name) {
     if (name == null) return Items.field_8162; 
     String normalized = name.toLowerCase();
     
     for (Block block : Registries.field_41175) {
       if (block.method_63499().replace("block.minecraft.", "").equals(normalized)) {
         return Item.method_7867(block);
       }
     } 
     
     for (Item item : Registries.field_41178) {
       if (item.method_7876().replace("item.minecraft.", "").equals(normalized)) {
         return item;
       }
     } 
     
     return Items.field_8831;
   }
   
   public static int getBedsCount() {
     if (mc.field_1724 == null) return 0;
     
     int counter = 0;
     for (int i = 0; i < mc.field_1724.method_31548().method_5439(); i++) {
       ItemStack stack = mc.field_1724.method_31548().method_5438(i);
       if (stack.method_7909() instanceof net.minecraft.BedItem) counter += stack.method_7947(); 
     } 
     return counter;
   }
   
   private static SlotSearchResult findBest(Searcher searcher, boolean hotbarOnly) {
     if (mc.field_1724 == null) return SlotSearchResult.notFound();
     
     int bestSlot = -1;
     float bestDamage = 0.0F;
     int end = hotbarOnly ? 8 : 35;
     
     for (int i = 0; i <= end; i++) {
       ItemStack stack = mc.field_1724.method_31548().method_5438(i);
       if (searcher.isValid(stack)) {
         
         float damage = getBaseAttackDamage(stack);
         if (damage > bestDamage) {
           bestDamage = damage;
           bestSlot = i;
         } 
       } 
     } 
     return (bestSlot == -1) ? 
       SlotSearchResult.notFound() : 
       new SlotSearchResult(bestSlot, true, mc.field_1724.method_31548().method_5438(bestSlot));
   }
   
   private static float getBaseAttackDamage(ItemStack stack) {
     AttributeModifiersComponent component = (AttributeModifiersComponent)stack.method_57825(DataComponentTypes.field_49636, AttributeModifiersComponent.field_49326);
     double damage = 1.0D;
     
     for (AttributeModifiersComponent.class_9287 entry : component.comp_2393()) {
       if (entry.comp_2395().equals(EntityAttributes.field_23721)) {
         damage += entry.comp_2396().comp_2449();
       }
     } 
     
     return (float)damage;
   }
   
   public static boolean isHolding(Item item) {
     return (mc.field_1724 != null && (mc.field_1724.method_6047().method_31574(item) || mc.field_1724.method_6079().method_31574(item)));
   }
   
   public static Hand getHand(Item item) {
     if (mc.field_1724 == null) return null; 
     if (mc.field_1724.method_6079().method_31574(item)) return Hand.field_5810; 
     if (mc.field_1724.method_6047().method_31574(item)) return Hand.field_5808; 
     return null;
   }
   
   public static interface Searcher {
     boolean isValid(ItemStack param1class_1799);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\player\HotbarUtil.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */