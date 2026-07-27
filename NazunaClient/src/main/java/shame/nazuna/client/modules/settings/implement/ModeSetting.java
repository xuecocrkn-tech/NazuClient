package shame.nazuna.client.modules.settings.implement;
 
 import java.util.List;
 import shame.nazuna.astra;
 
 public class ModeSetting extends Setting {
   private List<String> mods;
   private String current;
   private int index;
   
   
   public List<String> getMods() { return this.mods; }
   public int getIndex() {
     return this.index;
   }
   public ModeSetting(String name, String current, String... modes) {
     super(name);
     this.mods = Arrays.asList(modes);
     this.index = this.mods.indexOf(current);
     if (this.index < 0) {
       this.index = 0;
     }
     this.current = this.mods.get(this.index);
   }
   
   public void set(String selected) {
     int newIndex = this.mods.indexOf(selected);
     if (newIndex < 0) {
       return;
     }
     this.current = selected;
     this.index = newIndex;
   }
   
   public boolean is(String mode) {
     return this.current.equals(mode);
   }
   
   public String displayMode(String mode) {
     return (NazunaClient.INSTANCE.localizationStorage == null) ? mode : NazunaClient.INSTANCE.localizationStorage.translate(mode);
   }
   
   public String displayCurrent() {
     return displayMode(this.current);
   }
   
   public ModeSetting visible(Supplier<Boolean> state) {
     this.visible = state;
     return this;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\settings\implement\ModeSetting.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */