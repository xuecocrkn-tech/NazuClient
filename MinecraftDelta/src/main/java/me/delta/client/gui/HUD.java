package me.delta.client.gui;

import me.delta.client.DeltaClient;
import me.delta.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.Comparator;
import java.util.List;

public class HUD {
    private boolean visible = true;
    private static final int LINE_HEIGHT = 11;
    private static final int RIGHT_MARGIN = 4;
    private static final int TOP_MARGIN = 4;
    private static final int ARRAYLIST_COLOR = 0xFFBB86FC; // Delta purple accent
    private static final int WATERMARK_COLOR = 0xFF9C27B0; // Delta brand purple

    public void toggleVisibility() {
        visible = !visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void render(DrawContext context) {
        if (!visible) return;
        if (MinecraftClient.getInstance().player == null) return;
        if (MinecraftClient.getInstance().options.hudHidden) return;

        // Watermark
        String watermark = "Delta Client v1.0";
        context.drawText(
                MinecraftClient.getInstance().textRenderer,
                watermark,
                4, 4,
                WATERMARK_COLOR,
                false
        );

        // FPS counter
        String fps = "FPS: " + MinecraftClient.getInstance().getCurrentFps();
        context.drawText(
                MinecraftClient.getInstance().textRenderer,
                fps,
                4, 4 + LINE_HEIGHT,
                0xFFAAAAAA,
                false
        );

        // Coordinates
        if (MinecraftClient.getInstance().player != null) {
            var pos = MinecraftClient.getInstance().player.getPos();
            String coords = String.format("XYZ: %.1f / %.1f / %.1f", pos.x, pos.y, pos.z);
            context.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    coords,
                    4, 4 + LINE_HEIGHT * 2,
                    0xFFAAAAAA,
                    false
            );
        }

        // Module array list (right-aligned)
        List<Module> enabledModules = DeltaClient.MODULE_MANAGER.getEnabledModules();
        enabledModules = enabledModules.stream()
                .sorted(Comparator.comparing(m -> -MinecraftClient.getInstance().textRenderer.getWidth(m.getName())))
                .toList();

        int y = TOP_MARGIN;
        for (Module module : enabledModules) {
            String name = module.getName();
            int width = MinecraftClient.getInstance().textRenderer.getWidth(name);
            int x = MinecraftClient.getInstance().getWindow().getScaledWidth() - width - RIGHT_MARGIN;

            // Background box
            context.fill(
                    x - 2, y - 1,
                    x + width + 2, y + LINE_HEIGHT - 1,
                    0x551A1A2E
            );

            context.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    name,
                    x, y,
                    ARRAYLIST_COLOR,
                    false
            );
            y += LINE_HEIGHT;
        }
    }
}
