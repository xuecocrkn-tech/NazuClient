package shame.nazuna.client.modules.impl.render;
 
 import net.minecraft.class_1306;
 import net.minecraft.class_4587;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class ViewModel extends Module {
   public static ViewModel INSTANCE = new ViewModel();
   
   public final FloatSetting mainHandX = new FloatSetting("Правая рука X", 0.0F, -2.0F, 2.0F, 0.01F);
   public final FloatSetting mainHandY = new FloatSetting("Правая рука Y", 0.0F, -2.0F, 2.0F, 0.01F);
   public final FloatSetting mainHandZ = new FloatSetting("Правая рука Z", 0.0F, -2.0F, 2.0F, 0.01F);
   
   public final FloatSetting offHandX = new FloatSetting("Левая рука X", 0.0F, -2.0F, 2.0F, 0.01F);
   public final FloatSetting offHandY = new FloatSetting("Левая рука Y", 0.0F, -2.0F, 2.0F, 0.01F);
   public final FloatSetting offHandZ = new FloatSetting("Левая рука Z", 0.0F, -2.0F, 2.0F, 0.01F);
   
   public final BooleanSetting onlyAura = new BooleanSetting("Только с аурой", false);
   
   public ViewModel() {
     super("ViewModel", "Оффсеты рук от первого лица", Module.ModuleCategory.RENDER);
     addSettings(new Setting[] { (Setting)this.mainHandX, (Setting)this.mainHandY, (Setting)this.mainHandZ, (Setting)this.offHandX, (Setting)this.offHandY, (Setting)this.offHandZ, (Setting)this.onlyAura });
   }
   
   public void applyHandPosition(class_4587 matrices, class_1306 arm) {
     if (arm == class_1306.field_6183) {
       matrices.method_46416(this.mainHandX.get(), this.mainHandY.get(), this.mainHandZ.get());
     } else {
       matrices.method_46416(this.offHandX.get(), this.offHandY.get(), this.offHandZ.get());
     } 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\ViewModel.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */