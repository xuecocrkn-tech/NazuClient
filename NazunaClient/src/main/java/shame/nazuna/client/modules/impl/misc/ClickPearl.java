package shame.nazuna.client.modules.impl.misc;
 import net.minecraft.Hand;
 import net.minecraft.PlayerEntity;
 import net.minecraft.Items;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventBinding;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.utils.player.InventoryUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BindSetting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 
 public class ClickPearl extends Module {
   public static ClickPearl INSTANCE = new ClickPearl();
   
   private final BindSetting keyToPearl = new BindSetting("Кнопка", -1);
   private final BooleanSetting bypass = new BooleanSetting("Обход", true);
   
   private boolean use;
   
   public ClickPearl() {
     super("ClickPearl", "Кидает перку по внутреннему бинду", Module.ModuleCategory.MISC);
     addSettings(new Setting[] { (Setting)this.keyToPearl, (Setting)this.bypass });
   }
 
   
   public void onEnable() {
     this.use = false;
     super.onEnable();
   }
   
   @EventLink
   public void onEvent(EventBinding event) {
     if (mc.field_1755 != null)
       return;  if (event.getKey() == this.keyToPearl.getKey()) {
       this.use = true;
     }
   }
   
   @EventLink
   public void onEvent(EventUpdate event) {
     if (!this.use)
       return;  if (mc.field_1724 == null || mc.field_1687 == null) {
       this.use = false;
       
       return;
     } 
     int oldSlot = (mc.field_1724.method_31548()).field_7545;
     int pearlSlot = InventoryUtils.find(Items.field_8634, 0, 36);
     
     if (pearlSlot == -1) {
       this.use = false;
       
       return;
     } 
     if (pearlSlot > 9) {
       mc.field_1724.method_5728(false);
     }
     
     if (this.bypass.isState()) {
       (mc.field_1724.method_31548()).field_7545 = pearlSlot;
       mc.field_1761.method_2919((PlayerEntity)mc.field_1724, Hand.field_5808);
       (mc.field_1724.method_31548()).field_7545 = oldSlot;
     } else {
       InventoryUtils.swapAndUseHvH(Items.field_8634);
     } 
     
     this.use = false;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\misc\ClickPearl.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */