package me.delta.client.module.modules.render;

import me.delta.client.module.Category;
import me.delta.client.module.Module;

public class FullBright extends Module {
    private final me.delta.client.settings.NumberSetting brightness = createNumber("Brightness", "Gamma level", 15.0, 1.0, 15.0, 1.0);
    private double prevGamma = -1;

    public FullBright() {
        super("FullBright", "Makes everything bright", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        if (mc.options != null) {
            prevGamma = mc.options.getGamma().getValue();
            mc.options.getGamma().setValue(brightness.getValue());
        }
    }

    @Override
    protected void onDisable() {
        if (mc.options != null && prevGamma >= 0) {
            mc.options.getGamma().setValue(prevGamma);
            prevGamma = -1;
        }
    }
}
