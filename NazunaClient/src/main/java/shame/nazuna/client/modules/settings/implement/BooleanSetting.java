package shame.nazuna.client.modules.settings.implement;
 
 import java.util.function.Supplier;
 import shame.nazuna.client.modules.settings.Setting;
 
 public class BooleanSetting extends Setting {
   public void setState(boolean state) {
     this.state = state;
   public boolean isState() {
     return this.state;
   }
   public BooleanSetting(String name, boolean state) {
     super(name);
     this.state = state;
   }
   
   public static BooleanSetting of(String name, boolean state) {
     return new BooleanSetting(name, state);
   }
   
   public BooleanSetting visible(Supplier<Boolean> state) {
     this.visible = state;
     return this;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\settings\implement\BooleanSetting.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */