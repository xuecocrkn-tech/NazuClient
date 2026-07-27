package me.delta.client.settings;

public class NumberSetting extends Setting<Double> {
    private final double min;
    private final double max;
    private final double step;
    private final String suffix;

    public NumberSetting(String name, String description, double defaultValue, double min, double max, double step) {
        this(name, description, defaultValue, min, max, step, "");
    }

    public NumberSetting(String name, String description, double defaultValue, double min, double max, double step, String suffix) {
        super(name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
        this.suffix = suffix;
    }

    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getStep() { return step; }
    public String getSuffix() { return suffix; }

    @Override
    public void setValue(Double value) {
        super.setValue(Math.round(value / step) * step);
    }

    @Override
    public void loadFromConfig(Object obj) {
        if (obj instanceof Number n) setValue(n.doubleValue());
    }

    @Override
    public Object saveToConfig() {
        return getValue();
    }
}
