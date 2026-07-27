package me.delta.client.module.modules.render;

import me.delta.client.module.Category;
import me.delta.client.module.Module;

public class ESP extends Module {
    private final me.delta.client.settings.ModeSetting mode = createMode("Mode", "ESP mode", "Glow", "Glow", "Box", "Flat", "None");
    private final me.delta.client.settings.BooleanSetting players = createBoolean("Players", "Highlight players", true);
    private final me.delta.client.settings.BooleanSetting monsters = createBoolean("Monsters", "Highlight monsters", false);
    private final me.delta.client.settings.BooleanSetting animals = createBoolean("Animals", "Highlight animals", false);
    private final me.delta.client.settings.NumberSetting range = createNumber("Range", "Max render range", 64.0, 8.0, 256.0, 8.0, " blocks");
    private final me.delta.client.settings.NumberSetting lineWidth = createNumber("Line Width", "Outline thickness", 2.0, 1.0, 5.0, 0.5);

    public ESP() {
        super("ESP", "Highlights entities through walls", Category.RENDER);
    }

    public String getMode() { return mode.getValue(); }
    public boolean showPlayers() { return players.getValue(); }
    public boolean showMonsters() { return monsters.getValue(); }
    public boolean showAnimals() { return animals.getValue(); }
    public double getRange() { return range.getValue(); }
    public float getLineWidth() { return lineWidth.getValue().floatValue(); }

    public boolean shouldRender() {
        return isEnabled() && !"None".equals(mode.getValue());
    }
}
