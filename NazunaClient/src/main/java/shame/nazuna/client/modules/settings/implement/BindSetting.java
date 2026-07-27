package shame.nazuna.client.modules.settings.implement;
 
 import java.util.function.Supplier;
 import shame.nazuna.client.modules.settings.Setting;
 
 public class BindSetting extends Setting {
   public void setKey(int key) {
     this.key = key;
   public int getKey() {
     return this.key;
   }
   public BindSetting(String name, int keyDefault) {
     super(name);
     this.key = keyDefault;
   }
   
   public BindSetting visible(Supplier<Boolean> state) {
     this.visible = state;
     return this;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\settings\implement\BindSetting.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */