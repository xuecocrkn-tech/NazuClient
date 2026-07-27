package me.delta.client.gui;

import me.delta.client.DeltaClient;
import me.delta.client.module.Category;
import me.delta.client.module.Module;
import me.delta.client.settings.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends Screen {
    private final List<Panel> panels = new ArrayList<>();
    private boolean open = false;
    private Panel draggedPanel = null;
    private double dragX, dragY;

    // Delta color scheme
    public static final int BG_COLOR = 0xDD1A1A2E;
    public static final int HEADER_COLOR = 0xDD9C27B0;
    public static final int MODULE_OFF = 0x882A2A3E;
    public static final int MODULE_ON = 0x889C27B0;
    public static final int HOVER_COLOR = 0x44333355;
    public static final int TEXT_COLOR = 0xFFE0E0E0;
    public static final int ACCENT_COLOR = 0xFFBB86FC;

    public ClickGUI() {
        super(Text.literal("Delta Client"));
    }

    public void toggle() {
        open = !open;
        if (open) {
            MinecraftClient.getInstance().setScreen(this);
        } else {
            MinecraftClient.getInstance().setScreen(null);
        }
    }

    @Override
    protected void init() {
        super.init();
        panels.clear();

        int x = 10;
        for (Category category : Category.values()) {
            Panel panel = new Panel(category, x, 10, 110, this);
            panels.add(panel);
            x += 120;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        for (Panel panel : panels) {
            panel.render(context, mouseX, mouseY, delta);
        }

        // Session info at bottom
        int enabledCount = (int) DeltaClient.MODULE_MANAGER.getEnabledModules().size();
        String info = String.format("Delta Client — %d/%d modules active",
                enabledCount, DeltaClient.MODULE_MANAGER.getModules().size());
        context.drawText(MinecraftClient.getInstance().textRenderer,
                info, 5, height - 15, ACCENT_COLOR, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Panel panel : panels) {
            if (panel.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (Panel panel : panels) {
            panel.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (Panel panel : panels) {
            panel.mouseScrolled(mouseX, mouseY, verticalAmount);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Panel panel : panels) {
            if (panel.keyPressed(keyCode)) return true;
        }

        if (keyCode == 344 || keyCode == 345) { // RSHIFT or LALT to close
            toggle();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        open = false;
        DeltaClient.CONFIG_MANAGER.save();
        super.close();
    }
}
