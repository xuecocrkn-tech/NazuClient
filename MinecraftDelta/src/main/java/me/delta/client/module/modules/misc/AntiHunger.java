package me.delta.client.module.modules.misc;

import me.delta.client.module.Category;
import me.delta.client.module.Module;

public class AntiHunger extends Module {
    private final me.delta.client.settings.BooleanSetting spoofSprint = createBoolean("Spoof Sprint", "Prevents hunger from sprinting", true);
    private final me.delta.client.settings.BooleanSetting noFoodHeal = createBoolean("No Food Heal", "Prevents food usage from healing", false);

    public AntiHunger() {
        super("AntiHunger", "Reduces hunger loss", Category.MISC);
    }

    public boolean shouldSpoofSprint() {
        return isEnabled() && spoofSprint.getValue();
    }
}
