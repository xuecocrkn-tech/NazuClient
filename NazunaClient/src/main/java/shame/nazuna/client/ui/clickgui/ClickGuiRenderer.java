package shame.nazuna.client.ui.clickgui;
 
 import java.util.ArrayList;
 import java.util.List;
 import net.minecraft.Window;
 import net.minecraft.DrawContext;
 import net.minecraft.MathHelper;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.input.KeyBoardUtils;
 import shame.nazuna.api.utils.math.HoveringUtils;
 import shame.nazuna.api.utils.render.RenderUtils;
 import shame.nazuna.api.utils.render.fonts.msdf.Font;
 import shame.nazuna.api.utils.render.fonts.msdf.Fonts;
 import shame.nazuna.api.utils.scissor.ScissorUtils;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 
 public class ClickGuiRenderer
 {
   private final ClickGuiState state;
   private final ClickGuiSettingRenderer settingRenderer;
   private final ClickGuiThemeSelector themeSelector;
   
   public ClickGuiRenderer(ClickGuiState state, ClickGuiSettingRenderer settingRenderer, ClickGuiThemeSelector themeSelector) {
     this.state = state;
     this.settingRenderer = settingRenderer;
     this.themeSelector = themeSelector;
   }
   
   public void render(DrawContext context, int mouseX, int mouseY, Window window, float animationProgress) {
     if (window == null) {
       return;
     }
     
     float alphaMul = MathHelper.method_15363(animationProgress, 0.0F, 1.0F);
     int shadeColor = getFadeShadeColor(alphaMul, 120);
     int colorTheme = getThemeColor();
     Module hoveredModule = null;
     
     Module.ModuleCategory[] categories = Module.ModuleCategory.values();
     for (int i = 0; i < categories.length; i++) {
       Module.ModuleCategory category = categories[i];
       float panelX = ClickGuiLayout.getCategoryPanelX(this.state.getX(), i);
       Module categoryHoveredModule = renderCategoryPanel(context, mouseX, mouseY, panelX, category, colorTheme, alphaMul, shadeColor);
       if (categoryHoveredModule != null) {
         hoveredModule = categoryHoveredModule;
       }
     } 
     
     renderSearch(context, categories.length, colorTheme, alphaMul, getFadeShadeColor(alphaMul, 95));
     this.themeSelector.render(context, window, this.state.getRenderOffsetY(), alphaMul, getFadeShadeColor(alphaMul, 95));
     renderDescription(context, window, hoveredModule, colorTheme, animationProgress);
   }
   
   private Module renderCategoryPanel(DrawContext context, int mouseX, int mouseY, float panelX, Module.ModuleCategory category, int colorTheme, float alphaMul, int shadeColor) {
     float panelY = this.state.getY() + this.state.getRenderOffsetY();
     RenderUtils.drawRoundedRect(context.method_51448(), panelX, panelY, 100.0F, 275.0F, 8.0F, ColorUtils.darken(colorTheme, 0.07F));
     RenderUtils.drawRoundedRect(context.method_51448(), panelX, panelY + 23.0F, 100.0F, 0.5F, 0.0F, ColorUtils.rgb(19, 18, 24));
     if ((shadeColor >> 24 & 0xFF) > 0) {
       RenderUtils.drawRoundedRect(context.method_51448(), panelX, panelY, 100.0F, 275.0F, 8.0F, shadeColor);
     }
     
     icons(14).drawCenteredString(context.method_51448(), category.getIcons(), panelX + 50.0F - issue(15).getWidth(category.getName()) / 2.0F - 4.0F, panelY + 10.0F, alpha(colorTheme, alphaMul));
     issue(15).drawCenteredString(context.method_51448(), category.getName(), panelX + 52.0F, panelY + 9.0F, alpha(-1, alphaMul));
     
     float contentY = ClickGuiLayout.getContentY(panelY);
     float contentHeight = ClickGuiLayout.getContentHeight();
     this.state.clampScroll(category, contentHeight);
     float moduleY = contentY + this.state.getScroll(category);
     Module hoveredModule = null;
     
     ScissorUtils.push();
     ScissorUtils.setFromComponentCoordinates(panelX, contentY, 100.0D, contentHeight);
     
     for (Module module : this.state.getModules(category)) {
       float openProgress = this.state.getOpenProgress(module);
       float moduleHeight = ClickGuiLayout.getModuleHeight(module, openProgress);
       
       if (moduleY + moduleHeight + 4.0F >= contentY && moduleY <= contentY + contentHeight) {
         Module moduleHover = renderModule(context, mouseX, mouseY, panelX, moduleY, module, openProgress, moduleHeight, colorTheme, alphaMul, shadeColor);
         if (moduleHover != null) {
           hoveredModule = moduleHover;
         }
       } 
       
       moduleY += 4.0F + moduleHeight;
     } 
     
     ScissorUtils.pop();
     return hoveredModule;
   }
   
   private Module renderModule(DrawContext context, int mouseX, int mouseY, float panelX, float moduleY, Module module, float openProgress, float moduleHeight, int colorTheme, float alphaMul, int shadeColor) {
     List<Setting> settings = module.getSettings();
     renderModuleBackground(context, panelX, moduleY, moduleHeight, module.isEnable(), colorTheme, shadeColor);
     
     String moduleName = module.getName();
     String bindText = "";
     if (this.state.getBindingModule() == module) {
       bindText = " [...]";
     } else if (module.getKey() != -1) {
       bindText = " [" + this.state.toEnglish(KeyBoardUtils.getBindName(module.getKey())) + "]";
     } 
     
     int nameColor = module.isEnable() ? alpha(-1, alphaMul) : alpha(ColorUtils.rgba(255, 255, 255, 170), alphaMul);
     int bindColor = module.isEnable() ? alpha(ColorUtils.rgba(255, 255, 255, 150), alphaMul) : alpha(ColorUtils.rgba(255, 255, 255, 100), alphaMul);
     
     issue(14).draw(context.method_51448(), moduleName, panelX + 10.0F, moduleY + 8.0F, nameColor);
     if (!bindText.isEmpty()) {
       float nameWidth = issue(14).getWidth(moduleName);
       issue(11).draw(context.method_51448(), bindText, panelX + 10.0F + nameWidth, moduleY + 9.0F, bindColor);
     } 
     
     if (settings != null && !settings.isEmpty() && ClickGuiLayout.hasVisibleSettings(settings)) {
       renderModuleDots(context, panelX, moduleY, module, module.isEnable(), alphaMul);
     }
     
     if (settings != null && !settings.isEmpty()) {
       this.settingRenderer.render(context, module, panelX, moduleY, openProgress, colorTheme, mouseX, mouseY, this.state);
     }
     
     if (HoveringUtils.isHovered(mouseX, mouseY, (panelX + 3.0F), moduleY, 93.5D, moduleHeight)) {
       return module;
     }
     return null;
   }
   
   private void renderModuleBackground(DrawContext context, float panelX, float moduleY, float moduleHeight, boolean enabled, int colorTheme, int shadeColor) {
     if (enabled) {
       RenderUtils.drawRoundedRect(context.method_51448(), panelX + 3.0F, moduleY - 0.5F, 93.5F, moduleHeight + 1.0F, 5.0F, ColorUtils.darken(colorTheme, 0.17F));
       RenderUtils.drawGradientRect(context.method_51448(), panelX + 3.0F + 0.5F, moduleY, 92.5F, moduleHeight, 4.0F, ColorUtils.darken(colorTheme, 0.15F), ColorUtils.darken(colorTheme, 0.1F), false);
       if ((shadeColor >> 24 & 0xFF) > 0) {
         RenderUtils.drawRoundedRect(context.method_51448(), panelX + 3.0F + 0.5F, moduleY, 92.5F, moduleHeight, 4.0F, shadeColor);
       }
       
       return;
     } 
     RenderUtils.drawRoundedRect(context.method_51448(), panelX + 3.0F, moduleY - 0.5F, 93.5F, moduleHeight + 1.0F, 5.0F, ColorUtils.darken(colorTheme, 0.1F));
     RenderUtils.drawGradientRect(context.method_51448(), panelX + 3.0F + 0.5F, moduleY, 92.5F, moduleHeight, 4.0F, ColorUtils.darken(colorTheme, 0.09F), ColorUtils.darken(colorTheme, 0.08F), false);
     if ((shadeColor >> 24 & 0xFF) > 0) {
       RenderUtils.drawRoundedRect(context.method_51448(), panelX + 3.0F + 0.5F, moduleY, 92.5F, moduleHeight, 4.0F, shadeColor);
     }
   }
   
   private void renderModuleDots(DrawContext context, float panelX, float moduleY, Module module, boolean enabled, float alphaMul) {
     int dotsColor = enabled ? alpha(ColorUtils.rgba(255, 255, 255, 220), alphaMul) : alpha(ColorUtils.rgba(255, 255, 255, 100), alphaMul);
     float dotsX = panelX + 87.5F;
     float baseY = moduleY + 10.0F;
     float spacing = 2.0F;
     float radius = 2.1F;
     float bottomXOffset = 2.1F;
     float angle = this.state.updateDotsRotation(module, module.isOpen() ? 1.5707964F : 0.0F);
     float cos = (float)Math.cos(angle);
     float sin = (float)Math.sin(angle);
     float[][] offsets = { { 0.0F, -spacing }, { -bottomXOffset, spacing }, { bottomXOffset, spacing } };
 
 
 
 
     
     for (float[] offset : offsets) {
       float rx = offset[0] * cos - offset[1] * sin;
       float ry = offset[0] * sin + offset[1] * cos;
       RenderUtils.drawRoundCircle(context.method_51448(), dotsX + rx, baseY + ry, radius, dotsColor);
     } 
   }
   
   private int getThemeColor() {
     if (!NazunaClient.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
       return (NazunaClient.INSTANCE.themeStorage.getThemes().getTheme()).color[0];
     }
     return ColorUtils.getThemeColor();
   }
   
   private void renderSearch(DrawContext context, int categoryCount, int colorTheme, float alphaMul, int shadeColor) {
     float searchY = ClickGuiLayout.getSearchY(this.state.getY() + this.state.getRenderOffsetY());
     float searchW = getSearchWidth();
     float searchX = ClickGuiLayout.getSearchX(this.state.getX(), categoryCount, searchW);
     float searchH = 18.0F;
     float selectionPaddingLeft = 3.0F;
     float selectionPaddingRight = 1.5F;
     int borderColor = ColorUtils.darken(colorTheme, 0.12F);
     
     RenderUtils.drawRoundedRect(context.method_51448(), searchX - 0.5F, searchY - 0.5F, searchW + 1.0F, searchH + 1.0F, 5.5F, borderColor);
     RenderUtils.drawGradientRect(context.method_51448(), searchX, searchY, searchW, searchH, 5.0F, 
         ColorUtils.darken(colorTheme, 0.12F), 
         ColorUtils.darken(colorTheme, 0.08F), false);
     if ((shadeColor >> 24 & 0xFF) > 0) {
       RenderUtils.drawRoundedRect(context.method_51448(), searchX, searchY, searchW, searchH, 5.0F, shadeColor);
     }
     
     String query = this.state.getSearchText();
     String text = query.isEmpty() ? "Search..." : query;
 
     
     int textColor = query.isEmpty() ? alpha(ColorUtils.rgba(255, 255, 255, 110), alphaMul) : alpha(ColorUtils.rgba(255, 255, 255, 230), alphaMul);
     
     float iconX = searchX + 3.5F;
     float textX = searchX + 19.0F;
     float textY = searchY + 6.2F;
     iconsNew(18).drawGradientStringHorizontal(context.method_51448(), "l", iconX + 2.0F, searchY + 6.5F, alpha(colorTheme, alphaMul), alpha(colorTheme, alphaMul));
     
     ScissorUtils.push();
     ScissorUtils.setFromComponentCoordinates((textX - selectionPaddingLeft), searchY, (searchW - 19.0F - 8.0F + selectionPaddingLeft), searchH);
 
 
 
 
     
     if (!query.isEmpty() && this.state.hasSearchSelection()) {
       int selectionStart = this.state.getSearchSelectionStart();
       int selectionEnd = this.state.getSearchSelectionEnd();
       float selectedX = textX + issue(14).getWidth(query.substring(0, selectionStart)) - selectionPaddingLeft;
       float selectedW = issue(14).getWidth(query.substring(selectionStart, selectionEnd)) + selectionPaddingLeft + selectionPaddingRight;
       RenderUtils.drawRoundedRect(context.method_51448(), selectedX, searchY + 3.8F, selectedW, 10.5F, 1.5F, alpha(ColorUtils.rgba(42, 115, 255, 155), alphaMul));
     } 
     
     issue(14).draw(context.method_51448(), text, textX, textY + 1.0F, textColor);
     if (this.state.isSearchActive() && System.currentTimeMillis() / 500L % 2L == 0L) {
       float cursorX = textX + issue(14).getWidth(query.substring(0, Math.min(this.state.getSearchCursor(), query.length())));
       RenderUtils.drawRoundedRect(context.method_51448(), cursorX + 1.0F, searchY + 4.5F, 0.8F, 9.0F, 0.0F, alpha(ColorUtils.applyAlpha(colorTheme, 0.9F), alphaMul));
     } 
     ScissorUtils.pop();
   }
   
   private void renderDescription(DrawContext context, Window window, Module hoveredModule, int colorTheme, float alphaMul) {
     if (hoveredModule == null) {
       return;
     }
     
     String description = hoveredModule.getDisplayDescription();
     if (description == null || description.isBlank() || "NULLABLE".equalsIgnoreCase(description) || "desc".equalsIgnoreCase(description)) {
       return;
     }
     
     Font descriptionFont = issue(16);
     float maxWidth = window.method_4486() - 40.0F;
     List<String> lines = wrapDescription(descriptionFont, description, maxWidth);
     if (lines.isEmpty()) {
       return;
     }
     
     float lineHeight = descriptionFont.getHeight() - 2.0F;
     float boxHeight = lines.size() * lineHeight;
     float centerX = window.method_4486() * 0.5F;
     float startY = 100.0F - boxHeight - 6.0F;
     
     for (int i = 0; i < lines.size(); i++) {
       descriptionFont.drawCenteredString(context.method_51448(), lines.get(i), centerX, startY + i * lineHeight, ColorUtils.applyAlpha(-1, alphaMul));
     }
   }
   
   private List<String> wrapDescription(Font font, String text, float maxWidth) {
     List<String> lines = new ArrayList<>();
     String[] words = text.trim().split("\\s+");
     if (words.length == 0) {
       return lines;
     }
     
     StringBuilder currentLine = new StringBuilder();
     for (String word : words) {
       String candidate = currentLine.isEmpty() ? word : (String.valueOf(currentLine) + " " + String.valueOf(currentLine));
       if (font.getWidth(candidate) <= maxWidth || currentLine.isEmpty()) {
         currentLine.setLength(0);
         currentLine.append(candidate);
       }
       else {
         
         lines.add(currentLine.toString());
         currentLine.setLength(0);
         currentLine.append(word);
       } 
     } 
     if (!currentLine.isEmpty()) {
       lines.add(currentLine.toString());
     }
     
     return lines;
   }
   
   private float getSearchWidth() {
     String query = this.state.getSearchText();
     String text = query.isEmpty() ? "Search..." : query;
     float contentWidth = 19.0F + issue(14).getWidth(text) + 8.0F;
     return Math.max(75.0F, contentWidth);
   }
   
   private Font issue(int size) {
     return Fonts.getFont("suisse", size);
   }
   
   private Font icons(int size) {
     return Fonts.getFont("icon", size);
   }
   
   private Font iconsNew(int size) {
     return Fonts.getFont("icon1", size);
   }
   
   private int alpha(int color, float alphaMul) {
     return ColorUtils.applyAlpha(color, alphaMul);
   }
   
   private int getFadeShadeColor(float alphaMul, int maxAlpha) {
     int alpha = MathHelper.method_15340((int)((1.0F - alphaMul) * maxAlpha), 0, 255);
     return ColorUtils.rgba(0, 0, 0, alpha);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\clien\\ui\clickgui\ClickGuiRenderer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */