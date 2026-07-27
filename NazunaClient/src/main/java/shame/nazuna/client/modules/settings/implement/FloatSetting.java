package shame.nazuna.client.modules.settings.implement;

import java.util.function.Supplier;
import shame.nazuna.client.modules.settings.Setting;

public class FloatSetting extends Setting {
    private float value;
    private final float min;
    private final float max;
    private final float increment;
    private boolean active;

    public FloatSetting(String name, float value, float min, float max, float increment) {
        super(name);
        this.value = value;
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public float getMin() {
        return this.min;
    }

    public float getMax() {
        return this.max;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }

    public Number getValue() {
        return Float.valueOf(MathHelper.clamp(this.value, getMin(), getMax()));
    }

    public void setValue(float value) {
        this.value = MathHelper.clamp(value, getMin(), getMax());
    }

    public float get() {
        return getValue().floatValue();
    }

    public FloatSetting visible(Supplier<Boolean> state) {
        this.visible = state;
        return this;
    }
}
