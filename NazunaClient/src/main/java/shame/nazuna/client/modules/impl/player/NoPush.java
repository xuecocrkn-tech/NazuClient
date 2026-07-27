package shame.nazuna.client.modules.impl.player;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.ListSetting;
 
 public class NoPush extends Module {
   public void setCollisionList(ListSetting collisionList) {
     this.collisionList = collisionList;
   }
   
   public static NoPush INSTANCE = new NoPush();
   
 
 
 
 
   
   public NoPush() {
     super("NoPush", "Отключает коллизию", Module.ModuleCategory.MISC);
     addSettings(new Setting[] { (Setting)this.collisionList });
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\player\NoPush.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */