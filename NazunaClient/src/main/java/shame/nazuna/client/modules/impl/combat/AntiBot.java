package shame.nazuna.client.modules.impl.combat;
 
 import java.util.ArrayList;
 import java.util.List;
 import net.minecraft.Entity;
 import net.minecraft.LivingEntity;
 import net.minecraft.PlayerEntity;
 import net.minecraft.ItemStack;
 import net.minecraft.Items;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 
 public class AntiBot extends Module {
   public static AntiBot INSTANCE = new AntiBot();
   
   public static final List<Entity> isBot = new ArrayList<>();
   
   public AntiBot() {
     super("AntiBot", "Определяет ботов на сервере", Module.ModuleCategory.COMBAT);
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     newMatrix();
   }
   
   public void newMatrix() {
     if (mc.field_1687 == null)
       return; 
     for (PlayerEntity player : mc.field_1687.method_18456()) {
       if (mc.field_1724 != player && (
         (ItemStack)(player.method_31548()).field_7548.get(0)).method_7909() != Items.field_8162 && (
         (ItemStack)(player.method_31548()).field_7548.get(1)).method_7909() != Items.field_8162 && (
         (ItemStack)(player.method_31548()).field_7548.get(2)).method_7909() != Items.field_8162 && (
         (ItemStack)(player.method_31548()).field_7548.get(3)).method_7909() != Items.field_8162 && (
         (ItemStack)(player.method_31548()).field_7548.get(0)).method_7923() && (
         (ItemStack)(player.method_31548()).field_7548.get(1)).method_7923() && (
         (ItemStack)(player.method_31548()).field_7548.get(2)).method_7923() && (
         (ItemStack)(player.method_31548()).field_7548.get(3)).method_7923() && player
         .method_6079().method_7909() == Items.field_8162 && ((
         (ItemStack)(player.method_31548()).field_7548.get(0)).method_7909() == Items.field_8370 || (
         (ItemStack)(player.method_31548()).field_7548.get(1)).method_7909() == Items.field_8570 || (
         (ItemStack)(player.method_31548()).field_7548.get(2)).method_7909() == Items.field_8577 || (
         (ItemStack)(player.method_31548()).field_7548.get(3)).method_7909() == Items.field_8267 || (
         (ItemStack)(player.method_31548()).field_7548.get(0)).method_7909() == Items.field_8660 || (
         (ItemStack)(player.method_31548()).field_7548.get(1)).method_7909() == Items.field_8396 || (
         (ItemStack)(player.method_31548()).field_7548.get(2)).method_7909() == Items.field_8523 || (
         (ItemStack)(player.method_31548()).field_7548.get(3)).method_7909() == Items.field_8743) && player
         .method_6047().method_7909() != Items.field_8162 && 
         !((ItemStack)(player.method_31548()).field_7548.get(0)).method_7986() && 
         !((ItemStack)(player.method_31548()).field_7548.get(1)).method_7986() && 
         !((ItemStack)(player.method_31548()).field_7548.get(2)).method_7986() && 
         !((ItemStack)(player.method_31548()).field_7548.get(3)).method_7986() && player
         .method_7344().method_7586() == 20) {
         if (!isBot.contains(player)) {
           isBot.add(player);
         }
         return;
       } 
       isBot.remove(player);
     } 
   }
   
   public static boolean checkBot(LivingEntity entity) {
     return (entity instanceof PlayerEntity && isBot.contains(entity));
   }
 
   
   public void onDisable() {
     super.onDisable();
     isBot.clear();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\AntiBot.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */