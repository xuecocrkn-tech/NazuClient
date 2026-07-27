package shame.nazuna;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import shame.nazuna.api.QClient;
import shame.nazuna.api.storages.InitializeStorage;
import shame.nazuna.api.storages.implement.ConfigStorage;
import shame.nazuna.api.storages.implement.DragStorage;
import shame.nazuna.api.storages.implement.FreeLookStorage;
import shame.nazuna.api.storages.implement.FriendStorage;
import shame.nazuna.api.storages.implement.LocalizationStorage;
import shame.nazuna.api.storages.implement.MacroStorage;
import shame.nazuna.api.storages.implement.ModuleStorage;
import shame.nazuna.api.storages.implement.StaffStorage;
import shame.nazuna.api.utils.client.UserInfo;
import shame.nazuna.api.utils.draggable.Draggable;
import shame.nazuna.api.utils.tps.TPSCalc;
import shame.nazuna.client.modules.Module;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public final class NazunaClient implements ModInitializer, QClient {
    public static final NazunaClient INSTANCE = new NazunaClient();

    public File abItemsDir;
    public File configsDir;
    public File globalsDir;
    public UserInfo userInfo;
    public WaypointStorage waypointStorage;
    public StaffStorage staffStorage;
    public MacroStorage macroStorage;
    public FriendStorage friendStorage;
    public ConfigStorage configStorage;
    public LocalizationStorage localizationStorage;
    public CommandStorage commandStorage;
    public FreeLookStorage freeLookStorage;
    public RotationStorage rotationStorage;
    public ServerStorage serverStorage;
    public TPSCalc tpsCalc;
    public ThemeStorage themeStorage;
    public ModuleStorage moduleStorage;
    public InitializeStorage initializer;
    public static double deltaTime;
    private static double prevTime;
    public boolean isServer;

    private static final String[] STARTUP_LINKS = {
        "https://yougame.biz/userok/",
        "https://t.me/richpaster"
    };

    static {
        prevTime = 0.0D;
        deltaTime = 0.0D;
    }

    private NazunaClient() {}

    @Override
    public void onInitialize() {
        initStorage();
        openStartupLinks();
        WorldRenderEvents.START.register(client -> {
            double currentTime = GLFW.glfwGetTime();
            deltaTime = currentTime - prevTime;
            prevTime = currentTime;
        });
    }

    @Override
    public void initStorage() {
        MinecraftClient mc = MinecraftClient.getInstance();
        File runDir = mc.runDirectory;
        configsDir = new File(runDir, "nazuna/configs");
        globalsDir = new File(runDir, "nazuna/globals");
        abItemsDir = new File(runDir, "nazuna/abitems");

        if (!configsDir.exists()) configsDir.mkdirs();
        if (!globalsDir.exists()) globalsDir.mkdirs();
        if (!abItemsDir.exists()) abItemsDir.mkdirs();

        configStorage = new ConfigStorage();
        configStorage.initialize();
        friendStorage = new FriendStorage();
        friendStorage.initialize();
        moduleStorage = new ModuleStorage();
        moduleStorage.initialize();
        dragStorage = new DragStorage();
        dragStorage.initialize();
        macroStorage = new MacroStorage();
        macroStorage.initialize();
        localizationStorage = new LocalizationStorage();
        localizationStorage.initialize();
        freeLookStorage = new FreeLookStorage();
        freeLookStorage.initialize();
        staffStorage = new StaffStorage();
        staffStorage.initialize();
        initializer = new InitializeStorage();
        initializer.initialize();
    }

    public void openStartupLinks() {
        // Open startup links in browser
    }
}
