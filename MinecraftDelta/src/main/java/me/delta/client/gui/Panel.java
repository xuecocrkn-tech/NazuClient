package me.delta.client.gui;

import me.delta.client.module.Category;
import me.delta.client.module.Module;
import me.delta.client.settings.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class Panel {
    private final Category category;
    private double x, y;
    private final double width;
    private final ClickGUI gui;
    private double scrollOffset = 0;
    private boolean dragging = false;
    private double dragX, dragY;
    private boolean expanded = true;
    private Module selectedModule = null;
    private boolean showSettings = false;

    private static final int HEADER_HEIGHT = 18;
    private static final int MODULE_HEIGHT = 16;
    private static final int SETTING_HEIGHT = 14;

    public Panel(Category category, double x, double y, double width, ClickGUI gui) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width;
        this.gui = gui;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        List<Module> modules = getModules();
        double panelHeight = HEADER_HEIGHT + modules.size() * MODULE_HEIGHT + 2;

        if (showSettings && selectedModule != null) {
            panelHeight += selectedModule.getSettings().size() * SETTING_HEIGHT + HEADER_HEIGHT;
        }

        // Clamp scroll
        double maxScroll = Math.max(0, panelHeight - 250);
        scrollOffset = Math.min(0, Math.max(-maxScroll, scrollOffset));

        // Panel background
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + Math.min(panelHeight, 250)), ClickGUI.BG_COLOR);

        // Header
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + HEADER_HEIGHT), ClickGUI.HEADER_COLOR);
        context.drawText(MinecraftClient.getInstance().textRenderer,
                category.getName(), (int) x + 4, (int) y + 5, ClickGUI.TEXT_COLOR, false);

        // Module list
        double currentY = y + HEADER_HEIGHT + scrollOffset;
        for (Module module : modules) {
            if (currentY + MODULE_HEIGHT > y + HEADER_HEIGHT && currentY < y + Math.min(panelHeight, 250)) {
                boolean hovered = mouseX >= x && mouseX <= x + width
                        && mouseY >= currentY && mouseY <= currentY + MODULE_HEIGHT;

                int bg = module.isEnabled() ? ClickGUI.MODULE_ON : ClickGUI.MODULE_OFF;
                if (hovered) bg = module.isEnabled() ? 0xAA9C27B0 : ClickGUI.HOVER_COLOR;

                context.fill((int) x, (int) currentY, (int) (x + width), (int) (currentY + MODULE_HEIGHT), bg);

                // Module name
                String display = module.getName();
                if (module.getKeyBind() != 0) {
                    display += " [" + getKeyName(module.getKeyBind()) + "]";
                }
                context.drawText(MinecraftClient.getInstance().textRenderer,
                        display, (int) x + 4, (int) currentY + 4, ClickGUI.TEXT_COLOR, false);
            }
            currentY += MODULE_HEIGHT;
        }

        // Settings sub-panel
        if (showSettings && selectedModule != null) {
            double settingsY = y + HEADER_HEIGHT + modules.size() * MODULE_HEIGHT + 2 + scrollOffset;

            // Settings header
            context.fill((int) x, (int) settingsY, (int) (x + width), (int) (settingsY + HEADER_HEIGHT), 0xDD333355);
            context.drawText(MinecraftClient.getInstance().textRenderer,
                    selectedModule.getName() + " Settings",
                    (int) x + 4, (int) settingsY + 4, ClickGUI.ACCENT_COLOR, false);

            double settingY = settingsY + HEADER_HEIGHT;
            for (Setting<?> setting : selectedModule.getSettings()) {
                if (settingY + SETTING_HEIGHT > y + HEADER_HEIGHT && settingY < y + Math.min(panelHeight, 250)) {
                    context.fill((int) x, (int) settingY, (int) (x + width), (int) (settingY + SETTING_HEIGHT), 0x55222244);
                    context.drawText(MinecraftClient.getInstance().textRenderer,
                            truncateText(setting.getName() + ": " + displaySettingValue(setting), 12),
                            (int) x + 4, (int) settingY + 3, ClickGUI.TEXT_COLOR, false);
                }
                settingY += SETTING_HEIGHT;
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check header drag
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + HEADER_HEIGHT) {
            if (button == 0) {
                dragging = true;
                dragX = mouseX - x;
                dragY = mouseY - y;
                return true;
            }
        }

        List<Module> modules = getModules();
        double currentY = y + HEADER_HEIGHT + scrollOffset;

        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);
            if (mouseX >= x && mouseX <= x + width
                    && mouseY >= currentY && mouseY <= currentY + MODULE_HEIGHT) {
                if (button == 0) {
                    module.toggle();
                    return true;
                } else if (button == 1) {
                    selectedModule = module;
                    showSettings = !showSettings;
                    return true;
                }
            }
            currentY += MODULE_HEIGHT;
        }

        // Settings interactions
        if (showSettings && selectedModule != null) {
            double settingsY = y + HEADER_HEIGHT + modules.size() * MODULE_HEIGHT + 2 + scrollOffset + HEADER_HEIGHT;
            for (Setting<?> setting : selectedModule.getSettings()) {
                if (mouseX >= x && mouseX <= x + width
                        && mouseY >= settingsY && mouseY <= settingsY + SETTING_HEIGHT) {
                    if (setting instanceof BooleanSetting b) {
                        b.setValue(!b.getValue());
                        return true;
                    } else if (setting instanceof ModeSetting m) {
                        m.cycle();
                        return true;
                    } else if (setting instanceof NumberSetting n) {
                        if (button == 0) n.setValue(n.getValue() + n.getStep());
                        else if (button == 1) n.setValue(n.getValue() - n.getStep());
                        return true;
                    }
                }
                settingsY += SETTING_HEIGHT;
            }
        }

        return false;
    }

    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }
    }

    public void mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 250) {
            scrollOffset += scrollAmount * 8;
        }
    }

    public boolean keyPressed(int keyCode) {
        // Handle keybind setting
        return false;
    }

    private List<Module> getModules() {
        return MinecraftClient.getInstance() != null
                ? me.delta.client.DeltaClient.MODULE_MANAGER.getModulesByCategory(category)
                : List.of();
    }

    private String getKeyName(int key) {
        return switch (key) {
            case 32 -> "SPACE";
            case 257 -> "ENTER";
            case 258 -> "TAB";
            case 341 -> "CTRL";
            case 340 -> "SHIFT";
            case 344 -> "RSHIFT";
            case 345 -> "LALT";
            case 256 -> "ESC";
            default -> {
                if (key >= 48 && key <= 57) yield String.valueOf((char) key);
                if (key >= 65 && key <= 90) yield String.valueOf((char) key);
                if (key >= 320 && key <= 329) yield "F" + (key - 319);
                yield "KEY_" + key;
            }
        };
    }

    private String truncateText(String text, int maxChars) {
        return text.length() <= maxChars ? text : text.substring(0, maxChars - 2) + "..";
    }

    private String displaySettingValue(Setting<?> setting) {
        if (setting instanceof BooleanSetting b) return b.getValue() ? "✓" : "✗";
        if (setting instanceof NumberSetting n) return String.format("%.1f%s", n.getValue(), n.getSuffix());
        if (setting instanceof ModeSetting m) return m.getValue();
        return String.valueOf(setting.getValue());
    }
}
