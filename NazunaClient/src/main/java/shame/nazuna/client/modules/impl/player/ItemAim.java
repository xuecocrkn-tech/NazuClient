package shame.nazuna.client.modules.impl.player;
 
 import net.minecraft.class_1297;
 import net.minecraft.class_1542;
 import net.minecraft.class_1802;
 import net.minecraft.class_241;
 import net.minecraft.class_243;
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
     class_1542 targetItem = findTargetItem();
     if (targetItem == null)
       return; 
     class_241 rotations = getItemRotations(targetItem);
     RotationStorage.update(new Rotation(rotations.field_1343, rotations.field_1342), 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
   }
   
   private class_1542 findTargetItem() {
     class_1542 bestItem = null;
     double bestDistance = Double.MAX_VALUE;
     
     for (class_1297 entity : mc.field_1687.method_18112()) {
       if (entity instanceof class_1542) { class_1542 itemEntity = (class_1542)entity;
         if (!isWantedItem(itemEntity))
           continue; 
         double distance = mc.field_1724.method_5858((class_1297)itemEntity);
         if (distance < bestDistance) {
           bestDistance = distance;
           bestItem = itemEntity;
         }  }
     
     } 
     return bestItem;
   }
   
   private boolean isWantedItem(class_1542 itemEntity) {
     return ((this.element.is("Шары") && itemEntity.method_6983().method_31574(class_1802.field_8575)) || (this.element
       .is("Элитры") && itemEntity.method_6983().method_31574(class_1802.field_8833)));
   }
   
   private class_241 getItemRotations(class_1542 itemEntity) {
     class_243 targetPos = itemEntity.method_5829().method_1005();
     return RotationUtils.getRotations(targetPos);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\player\ItemAim.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */