package me.delta.client;

import me.delta.client.config.ConfigManager;
import me.delta.client.core.EventBus;
import me.delta.client.gui.ClickGUI;
import me.delta.client.gui.HUD;
import me.delta.client.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeltaClient implements ClientModInitializer {
    public static final String MOD_ID = "deltaclient";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final EventBus EVENT_BUS = new EventBus();
    public static final ModuleManager MODULE_MANAGER = new ModuleManager();
    public static final ConfigManager CONFIG_MANAGER = new ConfigManager();
    public static final HUD HUD = new HUD();
    public static final ClickGUI CLICK_GUI = new ClickGUI();

    private static KeyBinding clickGuiKey;
    private static KeyBinding toggleHudKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing Delta Client v{}", getClass().getPackage().getImplementationVersion());

        // Register keybinds
        clickGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.deltaclient.clickgui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.deltaclient"
        ));
        toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.deltaclient.togglehud",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                "category.deltaclient"
        ));

        // Initialize modules
        MODULE_MANAGER.initialize();

        // Register tick event for keybinds
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (clickGuiKey.wasPressed()) {
                CLICK_GUI.toggle();
            }
            if (toggleHudKey.wasPressed()) {
                HUD.toggleVisibility();
            }
        });

        // Load config
        CONFIG_MANAGER.load();

        LOGGER.info("Delta Client ready — {} modules loaded", MODULE_MANAGER.getModules().size());
    }

    public static void scheduleTask(Runnable task) {
        ClientTickEvents.END_CLIENT_TICK.register(client -> task.run());
    }
}
