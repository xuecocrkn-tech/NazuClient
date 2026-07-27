package me.delta.client.settings;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting<String> {
    private final List<String> modes;
    private int index;

    public ModeSetting(String name, String description, String defaultValue, String... modes) {
        super(name, description, defaultValue);
        this.modes = Arrays.asList(modes);
        this.index = this.modes.indexOf(defaultValue);
        if (this.index == -1) this.index = 0;
    }

    public List<String> getModes() { return modes; }

    public void cycle() {
        index = (index + 1) % modes.size();
        setValue(modes.get(index));
    }

    public void cycleBack() {
        index = (index - 1 + modes.size()) % modes.size();
        setValue(modes.get(index));
    }

    @Override
    public void loadFromConfig(Object obj) {
        if (obj instanceof String s && modes.contains(s)) {
            setValue(s);
            index = modes.indexOf(s);
        }
    }

    @Override
    public Object saveToConfig() {
        return getValue();
    }
}
