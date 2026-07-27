package shame.nazuna.client.modules.impl.player;
 
 import net.minecraft.Entity;
 import net.minecraft.ItemEntity;
 import net.minecraft.Items;
 import net.minecraft.Vec2f;
 import net.minecraft.Vec3d;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.storages.implement.RotationStorage;
 import shame.nazuna.api.utils.rotate.Rotation;
 import shame.nazuna.api.utils.rotate.RotationUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.ListSetting;
 
 public class ItemAim
   extends Module {
   public static ItemAim INSTANCE = new ItemAim();
   public ListSetting element = new ListSetting("Лутать", new BooleanSetting[] { new BooleanSetting("Шары", true), new BooleanSetting("Элитры", true) });
 
 
   
   public ItemAim() {
     super("ItemAim", "Автоматически наводиться на предмет", Module.ModuleCategory.PLAYER);
     addSettings(new Setting[] { (Setting)this.element });
   }
 
   
   @EventLink
   public void onEvent(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     ItemEntity targetItem = findTargetItem();
     if (targetItem == null)
       return; 
     Vec2f rotations = getItemRotations(targetItem);
     RotationStorage.update(new Rotation(rotations.field_1343, rotations.field_1342), 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
   }
   
   private ItemEntity findTargetItem() {
     ItemEntity bestItem = null;
     double bestDistance = Double.MAX_VALUE;
     
     for (Entity entity : mc.field_1687.method_18112()) {
       if (entity instanceof ItemEntity) { ItemEntity itemEntity = (ItemEntity)entity;
         if (!isWantedItem(itemEntity))
           continue; 
         double distance = mc.field_1724.method_5858((Entity)itemEntity);
         if (distance < bestDistance) {
           bestDistance = distance;
           bestItem = itemEntity;
         }  }
     
     } 
     return bestItem;
   }
   
   private boolean isWantedItem(ItemEntity itemEntity) {
     return ((this.element.is("Шары") && itemEntity.method_6983().method_31574(Items.field_8575)) || (this.element
       .is("Элитры") && itemEntity.method_6983().method_31574(Items.field_8833)));
   }
   
   private Vec2f getItemRotations(ItemEntity itemEntity) {
     Vec3d targetPos = itemEntity.method_5829().method_1005();
     return RotationUtils.getRotations(targetPos);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\player\ItemAim.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */