package me.delta.client.module.modules.render;

import me.delta.client.module.Category;
import me.delta.client.module.Module;

public class Tracers extends Module {
    private final me.delta.client.settings.NumberSetting range = createNumber("Range", "Tracer range", 64.0, 8.0, 256.0, 8.0, " blocks");
    private final me.delta.client.settings.BooleanSetting players = createBoolean("Players", "Show tracers to players", true);
    private final me.delta.client.settings.BooleanSetting friends = createBoolean("Friends", "Friend color override", false);

    public Tracers() {
        super("Tracers", "Draws lines to nearby entities", Category.RENDER);
    }

    public double getRange() { return range.getValue(); }
    public boolean showPlayers() { return players.getValue(); }
}
