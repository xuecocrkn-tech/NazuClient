package me.delta.client.module.modules.render;

import me.delta.client.module.Category;
import me.delta.client.module.Module;

public class Chams extends Module {
    private final me.delta.client.settings.BooleanSetting players = createBoolean("Players", "Chams on players", true);
    private final me.delta.client.settings.BooleanSetting items = createBoolean("Items", "Chams on dropped items", false);
    private final me.delta.client.settings.BooleanSetting crystals = createBoolean("Crystals", "Chams on end crystals", true);
    private final me.delta.client.settings.BooleanSetting throughWalls = createBoolean("Through Walls", "Render through walls", true);

    public Chams() {
        super("Chams", "See entities through walls with colored overlay", Category.RENDER);
    }

    public boolean showPlayers() { return players.getValue(); }
    public boolean showItems() { return items.getValue(); }
    public boolean showCrystals() { return crystals.getValue(); }
    public boolean isThroughWalls() { return throughWalls.getValue(); }
}
