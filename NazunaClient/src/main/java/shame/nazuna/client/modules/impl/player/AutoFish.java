package shame.nazuna.client.modules.impl.player;
 import net.minecraft.Hand;
 import net.minecraft.PlayerEntity;
 import net.minecraft.ItemStack;
 import net.minecraft.EnchantmentHelper;
 import net.minecraft.Packet;
 import net.minecraft.PlaySoundS2CPacket;
 import net.minecraft.UpdateSelectedSlotC2SPacket;
 import net.minecraft.SoundEvents;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventPacket;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 
 public class AutoFish extends Module {
   public static AutoFish INSTANCE = new AutoFish();
   
   private final BooleanSetting takeRod = new BooleanSetting("Автоматически брать удочку", true);
   
   private boolean isCached = false;
   private boolean needCached = false;
   private int rodHotbarSlot = -1;
   private long lastActionTime = 0L;
   private long catchTime = 0L;
   
   public AutoFish() {
     super("AutoFish", "Автоматизирует процесс рыбалки", Module.ModuleCategory.PLAYER);
     addSettings(new Setting[] { (Setting)this.takeRod });
   }
 
   
   public void onDisable() {
     this.isCached = false;
     this.needCached = false;
     this.rodHotbarSlot = -1;
     this.lastActionTime = 0L;
     this.catchTime = 0L;
     super.onDisable();
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null) {
       return;
     }
     
     if (this.takeRod.isState() && this.rodHotbarSlot == -1) {
       findBestFishingRodInHotbar();
     }
     
     if (this.rodHotbarSlot != -1 && (mc.field_1724.method_31548()).field_7545 != this.rodHotbarSlot) {
       (mc.field_1724.method_31548()).field_7545 = this.rodHotbarSlot;
       mc.field_1724.field_3944.method_52787((Packet)new UpdateSelectedSlotC2SPacket(this.rodHotbarSlot));
     } 
     
     long currentTime = System.currentTimeMillis();
     
     if (this.isCached && currentTime - this.catchTime >= 600L) {
       useFishingRod();
       this.isCached = false;
       this.needCached = true;
       this.lastActionTime = currentTime;
     } 
     
     if (this.needCached && currentTime - this.lastActionTime >= 300L) {
       useFishingRod();
       this.needCached = false;
       this.lastActionTime = currentTime;
     } 
   }
   
   @EventLink
   public void onPacket(EventPacket event) {
     if (mc.field_1724 == null || mc.field_1687 == null) {
       return;
     }
     
     Packet Packet = event.getPacket(); if (Packet instanceof PlaySoundS2CPacket) { PlaySoundS2CPacket packet = (PlaySoundS2CPacket)Packet;
       if (packet.method_11894().comp_349() == SoundEvents.field_14660) {
         this.isCached = true;
         this.catchTime = System.currentTimeMillis();
       }  }
   
   }
   
   private void useFishingRod() {
     if (mc.field_1724 == null || mc.field_1761 == null) {
       return;
     }
     
     if (this.rodHotbarSlot != -1 && this.rodHotbarSlot < 9) {
       ItemStack stack = mc.field_1724.method_31548().method_5438(this.rodHotbarSlot);
       
       if (stack.method_7909() instanceof net.minecraft.FishingRodItem) {
         if ((mc.field_1724.method_31548()).field_7545 != this.rodHotbarSlot) {
           (mc.field_1724.method_31548()).field_7545 = this.rodHotbarSlot;
           mc.field_1724.field_3944.method_52787((Packet)new UpdateSelectedSlotC2SPacket(this.rodHotbarSlot));
         } 
         
         mc.field_1761.method_2919((PlayerEntity)mc.field_1724, Hand.field_5808);
       } 
     } 
   }
   
   private void findBestFishingRodInHotbar() {
     if (mc.field_1724 == null) {
       return;
     }
     
     int bestRodSlot = -1;
     int maxEnchantments = -1;
     
     for (int i = 0; i < 9; i++) {
       ItemStack stack = mc.field_1724.method_31548().method_5438(i);
       
       if (stack.method_7909() instanceof net.minecraft.FishingRodItem) {
         int enchantmentCount = EnchantmentHelper.method_57532(stack).method_57541();
         
         if (enchantmentCount > maxEnchantments) {
           maxEnchantments = enchantmentCount;
           bestRodSlot = i;
         } 
       } 
     } 
     
     if (bestRodSlot != -1)
       this.rodHotbarSlot = bestRodSlot; 
   }
 }

