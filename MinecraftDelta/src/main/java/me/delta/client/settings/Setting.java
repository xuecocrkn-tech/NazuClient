package me.delta.client.settings;

public abstract class Setting<T> {
    private final String name;
    private final String description;
    private T value;

    public Setting(String name, String description, T defaultValue) {
        this.name = name;
        this.description = description;
        this.value = defaultValue;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public T getValue() { return value; }
    public void setValue(T value) { this.value = value; }

    public abstract void loadFromConfig(Object obj);
    public abstract Object saveToConfig();
}
