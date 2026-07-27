package shame.nazuna.client.modules.impl.misc;
 
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class ClientSounds extends Module {
   public static ClientSounds INSTANCE = new ClientSounds();
   
   public final ModeSetting stateSounds = new ModeSetting("Режим", "Нет", new String[] { "Первый", "Второй", "Третий", "Четвертый", "Пятый", "Шестой" });
   
   public final FloatSetting volume = new FloatSetting("Громкость", 50.0F, 1.0F, 100.0F, 0.5F);
   
   public ClientSounds() {
     super("ClientSounds", "Добавляет звуки клиента", Module.ModuleCategory.MISC);
     addSettings(new Setting[] { (Setting)this.stateSounds, (Setting)this.volume });
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\misc\ClientSounds.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */