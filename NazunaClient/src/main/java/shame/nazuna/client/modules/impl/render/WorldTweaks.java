package shame.nazuna.client.modules.impl.render;
 
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ListSetting;
 
 public class WorldTweaks extends Module {
   public static WorldTweaks INSTANCE = new WorldTweaks();
   
   private final ListSetting worldSettings = new ListSetting("Настройки мира", new BooleanSetting[] { new BooleanSetting("Время", true), new BooleanSetting("Фог", true) });
 
 
 
   
   private final FloatSetting timeSetting = (new FloatSetting("Время", 12.0F, 0.0F, 24.0F, 1.0F))
     .visible(() -> Boolean.valueOf(this.worldSettings.is("Время")));
   
   private final FloatSetting fogDistanceSetting = (new FloatSetting("Дистанция фога", 100.0F, 20.0F, 200.0F, 1.0F))
     .visible(() -> Boolean.valueOf(this.worldSettings.is("Фог")));
   
   public WorldTweaks() {
     super("CustomWorld", "Настройки мира", Module.ModuleCategory.RENDER);
     addSettings(new Setting[] { (Setting)this.worldSettings, (Setting)this.timeSetting, (Setting)this.fogDistanceSetting });
   }
   
   public boolean isTimeEnabled() {
     return (isEnable() && this.worldSettings.is("Время"));
   }
   
   public boolean isFogEnabled() {
     return (isEnable() && this.worldSettings.is("Фог"));
   }
   
   public long getForcedTime() {
     return (long)this.timeSetting.get() * 1000L;
   }
   
   public float getFogDistance() {
     return this.fogDistanceSetting.get();
   }
   
   public int getFogColor() {
     if (!NazunaClient.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
       return (NazunaClient.INSTANCE.themeStorage.getThemes().getTheme()).color[0];
     }
     return ColorUtils.getThemeColor();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\WorldTweaks.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */