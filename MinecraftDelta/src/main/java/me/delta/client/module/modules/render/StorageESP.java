package me.delta.client.module.modules.render;

import me.delta.client.module.Category;
import me.delta.client.module.Module;

public class StorageESP extends Module {
    private final me.delta.client.settings.NumberSetting range = createNumber("Range", "ESP range", 32.0, 8.0, 128.0, 8.0, " blocks");

    public StorageESP() {
        super("StorageESP", "Highlights chests, hoppers and other storage blocks", Category.RENDER);
    }

    public double getRange() { return range.getValue(); }
}
