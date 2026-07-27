package shame.nazuna.client.modules.settings.implement;
 import java.util.function.Supplier;
 
 public class FloatSetting extends Setting {
   private float value;
   private final float min;
   
   public void setActive(boolean active) {
     this.active = active;
   } private final float max; private final float increment; private boolean active;
   public float getMin() {
   public boolean isActive() { return this.active; }
   
   public FloatSetting(String name, float value, float min, float max, float increment) {
     super(name);
     this.value = value;
     this.min = min;
     this.max = max;
     this.increment = increment;
   }
   
   public Number getValue() {
     return Float.valueOf(MathHelper.method_15363(this.value, getMin(), getMax()));
   }
   
   public void setValue(float value) {
     this.value = MathHelper.method_15363(value, getMin(), getMax());
   }
   
   public float get() {
     return getValue().floatValue();
   }
   
   public FloatSetting visible(Supplier<Boolean> state) {
     this.visible = state;
     return this;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\settings\implement\FloatSetting.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */