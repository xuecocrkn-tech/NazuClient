package me.delta.client.module;

import me.delta.client.DeltaClient;
import me.delta.client.settings.*;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    private final String name;
    private final String description;
    private final Category category;
    private int keyBind;
    private boolean enabled;
    private boolean toggled;
    private final List<Setting<?>> settings = new ArrayList<>();

    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.keyBind = 0;
        this.enabled = false;
        this.toggled = false;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public int getKeyBind() { return keyBind; }
    public boolean isEnabled() { return enabled; }
    public boolean isToggled() { return toggled; }
    public List<Setting<?>> getSettings() { return settings; }

    public void setKeyBind(int keyBind) { this.keyBind = keyBind; }
    public void setToggled(boolean toggled) { this.toggled = toggled; }

    public void toggle() {
        if (enabled) disable();
        else enable();
    }

    public void enable() {
        if (enabled) return;
        enabled = true;
        toggled = true;
        onEnable();
    }

    public void disable() {
        if (!enabled) return;
        enabled = false;
        toggled = false;
        onDisable();
    }

    protected void addSetting(Setting<?> setting) {
        settings.add(setting);
    }

    protected BooleanSetting createBoolean(String name, String description, boolean defaultValue) {
        BooleanSetting setting = new BooleanSetting(name, description, defaultValue);
        settings.add(setting);
        return setting;
    }

    protected NumberSetting createNumber(String name, String description, double defaultValue, double min, double max, double step) {
        NumberSetting setting = new NumberSetting(name, description, defaultValue, min, max, step);
        settings.add(setting);
        return setting;
    }

    protected NumberSetting createNumber(String name, String description, double defaultValue, double min, double max, double step, String suffix) {
        NumberSetting setting = new NumberSetting(name, description, defaultValue, min, max, step, suffix);
        settings.add(setting);
        return setting;
    }

    protected ModeSetting createMode(String name, String description, String defaultValue, String... modes) {
        ModeSetting setting = new ModeSetting(name, description, defaultValue, modes);
        settings.add(setting);
        return setting;
    }

    public void onTick() {}

    protected void onEnable() {}
    protected void onDisable() {}
}
