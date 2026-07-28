package shame.nazuna.client.modules.impl.misc;
 
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BindSetting;
 
 public class AutoBuy extends Module {
   public static AutoBuy INSTANCE = new AutoBuy();
   
   public BindSetting openKey = new BindSetting("Бинд гуи", -1);
   
   public AutoBuy() {
     super("AutoBuy", Module.ModuleCategory.MISC);
     addSettings(new Setting[] { (Setting)this.openKey });
   }
 }

