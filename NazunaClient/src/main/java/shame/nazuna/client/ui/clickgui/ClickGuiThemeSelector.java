package shame.nazuna.client.ui.clickgui;
 
 import it.unimi.dsi.fastutil.objects.ObjectArrayList;
 import net.minecraft.Window;
 import net.minecraft.DrawContext;
 import shame.nazuna.api.storages.implement.ThemeStorage;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.math.HoveringUtils;
 import shame.nazuna.api.utils.render.RenderUtils;
 import shame.nazuna.astra;
 
 public class ClickGuiThemeSelector {
   public void render(DrawContext context, Window window, float offsetY, float alphaMul, int shadeColor) {
     if (context == null || window == null) {
       return;
     }
     
     ObjectArrayList<ThemeStorage.Themes> objectArrayList = NazunaClient.INSTANCE.themeStorage.getThemeList();
     if (objectArrayList == null || objectArrayList.isEmpty()) {
       return;
     }
     
     float totalWidth = objectArrayList.size() * 8.0F + (objectArrayList.size() - 1) * 4.0F;
     float panelWidth = totalWidth + 8.0F;
     float panelX = getThemePanelX(window, panelWidth);
     float panelY = 100.0F + offsetY;
     float startX = panelX + 4.0F;
     float startY = panelY + 3.5F;
     
     RenderUtils.drawGradientRect(context
         .method_51448(), panelX, panelY, panelWidth, 15.0F, 3.5F, 
 
 
 
 
         
         ColorUtils.darken(ColorUtils.getThemeColor(), 0.12F), 
         ColorUtils.darken(ColorUtils.getThemeColor(), 0.1F), false);
 
     
     if ((shadeColor >> 24 & 0xFF) > 0) {
       RenderUtils.drawRoundedRect(context.method_51448(), panelX, panelY, panelWidth, 15.0F, 3.5F, shadeColor);
     }
     
     ThemeStorage.Themes selected = NazunaClient.INSTANCE.themeStorage.getThemes();
     for (int i = 0; i < objectArrayList.size(); i++) {
       ThemeStorage.Themes theme = objectArrayList.get(i);
       float boxX = startX + i * 12.0F;
       float boxY = startY;
       if (theme == selected) {
         RenderUtils.drawRoundedRect(context
             .method_51448(), boxX - 0.5F, boxY - 0.5F, 9.0F, 9.0F, 2.5F, 
 
 
 
 
             
             ColorUtils.setAlphaColor(-1, Math.max(1, (int)(200.0F * alphaMul))));
       }
       
       RenderUtils.drawRoundedRect(context
           .method_51448(), boxX, boxY, 8.0F, 8.0F, 2.0F, 
 
 
 
 
           
           ColorUtils.applyAlpha(getThemeDisplayColor(theme), Math.max(0.55F, alphaMul)));
     } 
   }
 
   
   public boolean handleClick(Window window, double mouseX, double mouseY, int button, float offsetY) {
     if (window == null || button != 0) {
       return false;
     }
     
     ObjectArrayList<ThemeStorage.Themes> objectArrayList = NazunaClient.INSTANCE.themeStorage.getThemeList();
     if (objectArrayList == null || objectArrayList.isEmpty()) {
       return false;
     }
     
     float totalWidth = objectArrayList.size() * 8.0F + (objectArrayList.size() - 1) * 4.0F;
     float panelWidth = totalWidth + 8.0F;
     float panelX = getThemePanelX(window, panelWidth);
     float panelY = 100.0F + offsetY;
     float startX = panelX + 4.0F;
     float startY = panelY + 3.5F;
     
     if (!HoveringUtils.isHovered(mouseX, mouseY, panelX, panelY, panelWidth, 15.0D)) {
       return false;
     }
     
     for (int i = 0; i < objectArrayList.size(); i++) {
       float boxX = startX + i * 12.0F;
       float boxY = startY;
       if (HoveringUtils.isHovered(mouseX, mouseY, boxX, boxY, 8.0D, 8.0D)) {
         NazunaClient.INSTANCE.themeStorage.setThemes(objectArrayList.get(i));
         return true;
       } 
     } 
     return false;
   }
   
   private int getThemeDisplayColor(ThemeStorage.Themes theme) {
     int color = theme.getTheme().getColor(0);
     if (ColorUtils.alpha(color) == 0) {
       return ColorUtils.rgba(220, 220, 220, 180);
     }
     return color;
   }
   
   private float getThemePanelX(Window window, float panelWidth) {
     return window.method_4486() / 2.0F - panelWidth / 2.0F;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\clien\\ui\clickgui\ClickGuiThemeSelector.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */