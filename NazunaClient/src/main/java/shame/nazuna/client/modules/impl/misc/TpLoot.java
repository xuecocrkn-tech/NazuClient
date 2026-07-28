package shame.nazuna.client.modules.impl.misc;
 
 import java.util.List;
 import net.minecraft.Entity;
 import net.minecraft.ItemEntity;
 import net.minecraft.Item;
 import net.minecraft.ItemStack;
 import net.minecraft.Items;
 import net.minecraft.Vec3d;
 import net.minecraft.Packet;
 import net.minecraft.PlayerMoveC2SPacket;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.utils.chat.ChatUtils;
 import shame.nazuna.api.utils.math.TimerUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public final class TpLoot
   extends Module {
   public static TpLoot INSTANCE = new TpLoot();
   
   private final FloatSetting range = new FloatSetting("Дистанция", 10.0F, 3.0F, 50.0F, 1.0F);
   private final FloatSetting lootDelay = new FloatSetting("Задержка лута", 500.0F, 100.0F, 5000.0F, 50.0F);
   private final ModeSetting afterLoot = new ModeSetting("После лута", "Возвращаться", new String[] { "Возвращаться", "Тепаться на спавн" });
   private final FloatSetting actionDelay = new FloatSetting("Задержка действия", 1000.0F, 200.0F, 10000.0F, 100.0F);
   
   private final TimerUtils lootTimer = new TimerUtils();
   private final TimerUtils actionTimer = new TimerUtils();
   private Vec3d originalPos = null;
   
   private boolean waitingAction = false;
   private static final List<Item> TARGET_ITEMS = List.of(new Item[] { Items.field_22022, Items.field_22027, Items.field_22028, Items.field_22029, Items.field_22030, Items.field_8575, Items.field_8463, Items.field_8367, Items.field_8301, Items.field_8288, Items.field_8833 });
 
 
 
 
 
 
 
 
 
 
 
 
   
   public TpLoot() {
     super("TPLoot", "Телепортирует к ресурсам", Module.ModuleCategory.MISC);
     addSettings(new Setting[] { (Setting)this.range, (Setting)this.lootDelay, (Setting)this.afterLoot, (Setting)this.actionDelay });
   }
 
 
   
   @EventLink
   public void onTick(EventUpdate e) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     if (this.waitingAction) {
       if (this.actionTimer.finished((long)this.actionDelay.getValue().floatValue())) {
         if (this.afterLoot.is("Возвращаться") && this.originalPos != null) {
           teleportTo(this.originalPos);
           ChatUtils.sendMessage("TpLoot: возврат на исходную позицию");
         } 
         if (this.afterLoot.is("Тепаться на спавн")) {
           mc.field_1724.field_3944.method_45730("spawn");
           ChatUtils.sendMessage("TpLoot: выполнен /spawn");
         } 
         this.waitingAction = false;
         this.originalPos = null;
         this.lootTimer.reset();
       } 
       
       return;
     } 
     if (!this.lootTimer.finished((long)this.lootDelay.getValue().floatValue()))
       return; 
     ItemEntity targetItem = findTargetItem();
     if (targetItem == null)
       return; 
     this.originalPos = mc.field_1724.method_19538();
     
     Vec3d itemPos = targetItem.method_19538();
     teleportTo(itemPos);
     
     ItemStack stack = targetItem.method_6983();
     ChatUtils.sendMessage("TpLoot: подобран " + stack.method_7964().getString());
     
     this.lootTimer.reset();
     this.waitingAction = true;
     this.actionTimer.reset();
   }
   
   private ItemEntity findTargetItem() {
     double maxRange = this.range.getValue().doubleValue();
     ItemEntity closest = null;
     double closestDist = Double.MAX_VALUE;
     
     for (Entity entity : mc.field_1687.method_18112()) {
       if (entity instanceof ItemEntity) { ItemEntity itemEntity = (ItemEntity)entity;
         
         ItemStack stack = itemEntity.method_6983();
         if (!isTargetItem(stack.method_7909()))
           continue; 
         double dist = mc.field_1724.method_5858(entity);
         if (dist > maxRange * maxRange)
           continue; 
         if (dist < closestDist) {
           closestDist = dist;
           closest = itemEntity;
         }  }
     
     } 
     return closest;
   }
   
   private boolean isTargetItem(Item item) {
     return TARGET_ITEMS.contains(item);
   }
 
   
   private void teleportTo(Vec3d pos) {
     int packets = (int)Math.ceil(mc.field_1724.method_19538().method_1022(pos) / 10.0D);
     packets = Math.max(packets, 3);
     
     for (int i = 0; i < packets; i++) {
       mc.field_1724.field_3944.method_52787((Packet)new PlayerMoveC2SPacket.class_5911(mc.field_1724.method_24828(), mc.field_1724.field_5976));
     }
     
     mc.field_1724.field_3944.method_52787((Packet)new PlayerMoveC2SPacket.class_2829(pos.field_1352, pos.field_1351, pos.field_1350, false, mc.field_1724.field_5976));
     mc.field_1724.method_5814(pos.field_1352, pos.field_1351, pos.field_1350);
   }
   
   public void onEnable() {
     this.originalPos = null;
     this.waitingAction = false;
     this.lootTimer.reset();
     this.actionTimer.reset();
     super.onEnable();
   }
   
   public void onDisable() {
     this.originalPos = null;
     this.waitingAction = false;
     super.onDisable();
   }
 }

