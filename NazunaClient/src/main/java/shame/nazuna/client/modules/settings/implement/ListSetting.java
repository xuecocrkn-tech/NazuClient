package shame.nazuna.client.modules.settings.implement;
 
 import java.util.List;
 import java.util.function.Supplier;
 import shame.nazuna.client.modules.settings.Setting;
 
 public class ListSetting extends Setting {
   public void setSettings(List<BooleanSetting> settings) {
     this.settings = settings;
   public List<BooleanSetting> getSettings() {
     return this.settings;
   }
   public ListSetting(String name, BooleanSetting... settings) {
     super(name);
     this.settings = List.of(settings);
   }
   
   public ListSetting of(String name, BooleanSetting... settings) {
     return new ListSetting(name, settings);
   }
   
   public boolean is(String name) {
     return requireSetting(name).isState();
   }
   
   public void set(String name, boolean value) {
     requireSetting(name).setState(value);
   }
   
   public ListSetting visible(Supplier<Boolean> state) {
     this.visible = state;
     return this;
   }
   
   private BooleanSetting requireSetting(String name) {
     for (BooleanSetting option : this.settings) {
       if (option.name().equalsIgnoreCase(name)) {
         return option;
       }
     } 
     throw new NullPointerException("Unknown list setting entry: " + name);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\settings\implement\ListSetting.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */