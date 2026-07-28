package shame.nazuna.api.utils.player;
 import net.minecraft.EquippableComponent;
 import net.minecraft.Hand;
 import net.minecraft.PlayerEntity;
 import net.minecraft.SlotActionType;
 import net.minecraft.ArmorItem;
 import net.minecraft.Item;
 import net.minecraft.ItemStack;
 import net.minecraft.Items;
 import net.minecraft.UseAction;
 import net.minecraft.Enchantment;
 import net.minecraft.Enchantments;
 import net.minecraft.Packet;
 import net.minecraft.CloseHandledScreenC2SPacket;
 import net.minecraft.ClientCommandC2SPacket;
 import net.minecraft.PlayerInputC2SPacket;
 import net.minecraft.UpdateSelectedSlotC2SPacket;
 import net.minecraft.RegistryKey;
 import net.minecraft.RegistryEntry;
 import net.minecraft.ItemEnchantmentsComponent;
 import net.minecraft.DataComponentTypes;
 
 public final class InventoryUtils implements QClient {
   private InventoryUtils() {
     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
   public static int getItemSlot(Item input) {
     for (ItemStack stack : mc.field_1724.method_5661()) {
       if (stack.method_7909() == input) {
         return -2;
       }
     } 
     int slot = -1;
     for (int i = 0; i < 36; i++) {
       ItemStack s = mc.field_1724.method_31548().method_5438(i);
       if (s.method_7909() == input) {
         slot = i;
         break;
       } 
     } 
     if (slot < 9 && slot != -1) {
       slot += 36;
     }
     return slot;
   }
   
   public static int getEnchantmentLevel(ItemStack stack, RegistryKey<Enchantment> enchantmentKey) {
     ItemEnchantmentsComponent enchantments = (ItemEnchantmentsComponent)stack.method_57825(DataComponentTypes.field_49633, ItemEnchantmentsComponent.field_49385);
 
 
 
     
     for (RegistryEntry<Enchantment> enchantment : (Iterable<RegistryEntry<Enchantment>>)enchantments.method_57534()) {
       if (enchantment.method_40225(enchantmentKey)) {
         return enchantments.method_57536(enchantment);
       }
     } 
     return 0;
   }
 
   
   public static int findBestElytraSlot() {
     if (mc.field_1724 == null) return -1;
     
     int bestSlot = -1;
     double bestScore = -1.0D;
     
     for (int slot = 0; slot < 36; slot++) {
       ItemStack stack = mc.field_1724.method_31548().method_5438(slot);
       
       if (stack.method_7909() == Items.field_8833) {
         
         int protection = getEnchantmentLevel(stack, Enchantments.field_9111);
         int unbreaking = getEnchantmentLevel(stack, Enchantments.field_9119);
         int mending = getEnchantmentLevel(stack, Enchantments.field_9101);
         
         int maxDurability = stack.method_7936();
         int currentDamage = stack.method_7919();
         double durabilityRatio = (maxDurability - currentDamage) / maxDurability;
         
         double score = (protection * 100 + unbreaking * 10 + ((mending > 0) ? 1 : 0)) + durabilityRatio * 10.0D;
 
         
         if (score > bestScore) {
           bestScore = score;
           bestSlot = slot;
         } 
       } 
     }  return bestSlot;
   }
   
   public static int findBestChestplateSlot() {
     if (mc.field_1724 == null) return -1;
     
     int bestSlot = -1;
     double bestScore = -1.0D;
     
     for (int slot = 0; slot < 36; slot++) {
       ItemStack stack = mc.field_1724.method_31548().method_5438(slot);
       Item Item = stack.method_7909(); if (Item instanceof ArmorItem) { ArmorItem armor = (ArmorItem)Item;
         
         EquippableComponent equippable = (EquippableComponent)stack.method_57824(DataComponentTypes.field_54196);
         if (equippable != null && equippable.comp_3174() == EquipmentSlot.field_6174) {
           
           int protection = getEnchantmentLevel(stack, Enchantments.field_9111);
           int unbreaking = getEnchantmentLevel(stack, Enchantments.field_9119);
           int mending = getEnchantmentLevel(stack, Enchantments.field_9101);
           int priority = getChestplatePriority((Item)armor);
           
           int maxDamage = stack.method_7936();
           int damage = stack.method_7919();
           double durabilityRatio = (maxDamage == 0) ? 1.0D : ((maxDamage - damage) / maxDamage);
 
 
 
           
           double score = priority * 10000.0D + protection * 100.0D + unbreaking * 10.0D + ((mending > 0) ? true : false) + durabilityRatio * 10.0D;
 
           
           if (score > bestScore)
           { bestScore = score;
             bestSlot = slot; } 
         }  }
     
     }  return bestSlot;
   }
   
   public static int getChestplatePriority(Item item) {
     if (item == Items.field_22028) return 5; 
     if (item == Items.field_8058) return 4; 
     if (item == Items.field_8523) return 3; 
     if (item == Items.field_8678) return 2; 
     if (item == Items.field_8873) return 2; 
     if (item == Items.field_8577) return 1; 
     return 0;
   }
   
   public static int find(Item item, int start, int end) {
     if (mc.field_1724 != null) {
       for (int i = end; i >= start; i--) {
         if (mc.field_1724.field_7512.field_7763 != 0 && mc.field_1724.field_7512.method_7611(i).method_7677().method_7909() == item) {
           return i;
         }
         
         if (mc.field_1724.field_7512.field_7763 == 0 && mc.field_1724.method_31548().method_5438(i).method_7909() == item) {
           return i;
         }
       } 
     }
     
     return -1;
   }
   
   public static void swapAndUseHvH(Item item) {
     int slot = find(item, 9, 45);
     int slotHotbar = find(item, 0, 8);
     int previousSlot = (mc.field_1724.method_31548()).field_7545;
     
     boolean isUsingItem = mc.field_1724.method_6115();
     
     if (mc.field_1724.method_6047().method_7909() == item) {
       if (!isUsingItem) {
         mc.field_1761.method_2919((PlayerEntity)mc.field_1724, Hand.field_5808);
       }
       
       return;
     } 
     if (mc.field_1724.method_6079().method_7909() == item) {
       mc.field_1761.method_2919((PlayerEntity)mc.field_1724, Hand.field_5810);
       
       return;
     } 
     if (isUsingItem) {
       if (slotHotbar != -1) {
         mc.field_1761.method_2906(0, 36 + slotHotbar, 40, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
         mc.field_1724.field_3944.method_52787((Packet)new CloseHandledScreenC2SPacket(0));
         mc.field_1761.method_2919((PlayerEntity)mc.field_1724, Hand.field_5810);
         mc.field_1761.method_2906(0, 36 + slotHotbar, 40, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
         mc.field_1724.field_3944.method_52787((Packet)new CloseHandledScreenC2SPacket(0));
       } else if (slot != -1) {
         mc.field_1761.method_2906(0, slot, 40, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
         mc.field_1724.field_3944.method_52787((Packet)new CloseHandledScreenC2SPacket(0));
         mc.field_1761.method_2919((PlayerEntity)mc.field_1724, Hand.field_5810);
         mc.field_1761.method_2906(0, slot, 40, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
         mc.field_1724.field_3944.method_52787((Packet)new CloseHandledScreenC2SPacket(0));
       } 
       
       return;
     } 
     if (slotHotbar != -1) {
       mc.field_1724.field_3944.method_52787((Packet)new UpdateSelectedSlotC2SPacket(slotHotbar));
       mc.field_1761.method_2919((PlayerEntity)mc.field_1724, Hand.field_5808);
       mc.field_1724.field_3944.method_52787((Packet)new UpdateSelectedSlotC2SPacket(previousSlot));
       
       return;
     } 
     if (slot != -1) {
       int slotCorrectable = -1;
       
       for (int slotNone = 0; slotNone < 8; slotNone++) {
         ItemStack stack = mc.field_1724.method_31548().method_5438(slotNone);
         if (stack.method_7960()) {
           slotCorrectable = slotNone;
           
           break;
         } 
         UseAction action = stack.method_7976();
         if (action == UseAction.field_8952) {
           slotCorrectable = slotNone;
         }
       } 
       
       boolean wasSprinting = false;
       
       if (mc.field_1724.method_5624()) {
         mc.field_1724.field_3944.method_52787((Packet)new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, false, false)));
         mc.field_1724.method_5728(false);
         mc.field_1724.field_3944.method_52787((Packet)new ClientCommandC2SPacket((Entity)mc.field_1724, ClientCommandC2SPacket.class_2849.field_12985));
         if (!ModuleClass.sprint.isEnable()) {
           mc.field_1690.field_1867.method_23481(false);
         }
         wasSprinting = true;
       } 
       
       if (slotCorrectable == -1) {
         mc.field_1761.method_2906(0, slot, 8, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
         mc.field_1724.field_3944.method_52787((Packet)new CloseHandledScreenC2SPacket(0));
         mc.field_1724.field_3944.method_52787((Packet)new UpdateSelectedSlotC2SPacket(8));
         mc.field_1761.method_2919((PlayerEntity)mc.field_1724, Hand.field_5808);
         mc.field_1724.field_3944.method_52787((Packet)new UpdateSelectedSlotC2SPacket(previousSlot));
       } else {
         mc.field_1761.method_2906(0, slot, slotCorrectable, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
         mc.field_1724.field_3944.method_52787((Packet)new CloseHandledScreenC2SPacket(0));
         mc.field_1724.field_3944.method_52787((Packet)new UpdateSelectedSlotC2SPacket(slotCorrectable));
         mc.field_1761.method_2919((PlayerEntity)mc.field_1724, Hand.field_5808);
         mc.field_1724.field_3944.method_52787((Packet)new UpdateSelectedSlotC2SPacket(previousSlot));
         mc.field_1761.method_2906(0, slot, slotCorrectable, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
         mc.field_1724.field_3944.method_52787((Packet)new CloseHandledScreenC2SPacket(0));
       } 
       
       if (wasSprinting)
         mc.field_1724.field_3944.method_52787((Packet)new PlayerInputC2SPacket(mc.field_1724.field_3913.field_54155)); 
     } 
   }
 }

