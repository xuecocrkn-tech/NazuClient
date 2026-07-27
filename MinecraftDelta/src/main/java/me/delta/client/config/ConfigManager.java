package me.delta.client.config;

import com.google.gson.*;
import me.delta.client.DeltaClient;
import me.delta.client.module.Module;
import me.delta.client.settings.Setting;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_FILE = "deltaclient.json";

    private File configFile;

    public void load() {
        try {
            configFile = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE).toFile();

            if (!configFile.exists()) {
                DeltaClient.LOGGER.info("No config found, creating default");
                save();
                return;
            }

            FileReader reader = new FileReader(configFile);
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();

            // Load modules
            JsonObject modulesObj = root.getAsJsonObject("modules");
            if (modulesObj != null) {
                for (Module module : DeltaClient.MODULE_MANAGER.getModules()) {
                    JsonObject moduleObj = modulesObj.getAsJsonObject(module.getName());
                    if (moduleObj != null) {
                        // Enable state
                        if (moduleObj.has("enabled") && moduleObj.get("enabled").getAsBoolean()) {
                            module.enable();
                        }
                        // Keybind
                        if (moduleObj.has("keybind")) {
                            module.setKeyBind(moduleObj.get("keybind").getAsInt());
                        }
                        // Settings
                        JsonObject settingsObj = moduleObj.getAsJsonObject("settings");
                        if (settingsObj != null) {
                            for (Setting<?> setting : module.getSettings()) {
                                JsonElement value = settingsObj.get(setting.getName());
                                if (value != null) {
                                    setting.loadFromConfig(value);
                                }
                            }
                        }
                    }
                }
            }

            // Load HUD state
            JsonObject hudObj = root.getAsJsonObject("hud");
            if (hudObj != null && hudObj.has("visible")) {
                DeltaClient.HUD.toggleVisibility(); // Sync up if needed
            }

            DeltaClient.LOGGER.info("Config loaded successfully");

        } catch (Exception e) {
            DeltaClient.LOGGER.error("Failed to load config", e);
        }
    }

    public void save() {
        try {
            if (configFile == null) {
                configFile = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE).toFile();
            }

            configFile.getParentFile().mkdirs();

            JsonObject root = new JsonObject();

            // Save modules
            JsonObject modulesObj = new JsonObject();
            for (Module module : DeltaClient.MODULE_MANAGER.getModules()) {
                JsonObject moduleObj = new JsonObject();
                moduleObj.addProperty("enabled", module.isEnabled());
                moduleObj.addProperty("keybind", module.getKeyBind());

                // Save settings
                JsonObject settingsObj = new JsonObject();
                for (Setting<?> setting : module.getSettings()) {
                    settingsObj.add(setting.getName(), new GsonBuilder().create().toJsonTree(setting.saveToConfig()));
                }
                moduleObj.add("settings", settingsObj);

                modulesObj.add(module.getName(), moduleObj);
            }
            root.add("modules", modulesObj);

            // Save HUD state
            JsonObject hudObj = new JsonObject();
            hudObj.addProperty("visible", DeltaClient.HUD.isVisible());
            root.add("hud", hudObj);

            // Save version info
            root.addProperty("version", 1);
            root.addProperty("client", "DeltaClient");

            FileWriter writer = new FileWriter(configFile);
            GSON.toJson(root, writer);
            writer.close();

            DeltaClient.LOGGER.info("Config saved");

        } catch (Exception e) {
            DeltaClient.LOGGER.error("Failed to save config", e);
        }
    }
}
