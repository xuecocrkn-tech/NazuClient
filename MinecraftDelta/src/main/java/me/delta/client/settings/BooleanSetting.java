package me.delta.client.settings;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, String description, boolean defaultValue) {
        super(name, description, defaultValue);
    }

    @Override
    public void loadFromConfig(Object obj) {
        if (obj instanceof Boolean b) setValue(b);
    }

    @Override
    public Object saveToConfig() {
        return getValue();
    }
}
