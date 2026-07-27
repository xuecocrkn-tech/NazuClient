package shame.nazuna.client.ui.clickgui;
 
 import java.util.List;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.ListSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 
 
 
 
 
 
 
 
 public final class ClickGuiLayout
 {
   public static final float WIDTH = 100.0F;
   public static final float HEIGHT = 275.0F;
   public static final float CATEGORY_PANEL_STEP = 108.0F;
   public static final float THEME_PANEL_Y = 100.0F;
   public static final float THEME_PANEL_H = 15.0F;
   public static final float THEME_BOX_SIZE = 8.0F;
   public static final float THEME_BOX_GAP = 4.0F;
   public static final float THEME_BOX_RADIUS = 2.0F;
   public static final float THEME_SIDE_PADDING = 4.0F;
   public static final float MODULE_PADDING = 3.0F;
   public static final float MODULE_GAP = 4.0F;
   public static final float MODULE_HEADER_HEIGHT = 20.0F;
   public static final float MODULE_INNER_WIDTH = 93.5F;
   public static final float SETTING_START_Y = 20.0F;
   public static final float SETTING_PADDING = 4.0F;
   public static final float SETTING_BOTTOM_PADDING = 3.0F;
   public static final float SETTING_LEFT = 10.0F;
   public static final float SETTING_RIGHT = 89.0F;
   public static final float SLIDER_WIDTH = 79.0F;
   public static final float TEXT_SETTING_WIDTH = 42.0F;
   public static final float CLICKABLE_WIDTH = 79.0F;
   public static final int SEARCH_MAX_CHARS = 24;
   public static final float SEARCH_WIDTH = 75.0F;
   public static final float SEARCH_HEIGHT = 18.0F;
   public static final float SEARCH_GAP = 8.0F;
   public static final float SEARCH_ICON_X = 3.5F;
   public static final float SEARCH_TEXT_X = 19.0F;
   public static final float SEARCH_RIGHT_PADDING = 8.0F;
   
   public static float getTotalCategoriesWidth(int categoryCount) {
     return 100.0F * categoryCount + 8.0F * (categoryCount - 1);
   }
   
   public static float getCategoryPanelX(float x, int index) {
     return x + index * 108.0F;
   }
   
   public static float getContentY(float y) {
     return y + 25.0F;
   }
   
   public static float getContentHeight() {
     return 245.0F;
   }
   
   public static float getSearchX(float x, int categoryCount) {
     return x + getTotalCategoriesWidth(categoryCount) / 2.0F - 37.5F;
   }
   
   public static float getSearchX(float x, int categoryCount, float searchWidth) {
     return x + getTotalCategoriesWidth(categoryCount) / 2.0F - searchWidth / 2.0F;
   }
   
   public static float getSearchY(float y) {
     return y + 275.0F + 8.0F;
   }
   
   public static boolean hasVisibleSettings(List<Setting> settings) {
     for (Setting setting : settings) {
       if (setting != null && setting.visible().booleanValue()) {
         return true;
       }
     } 
     return false;
   }
   
   public static float calculateModeSettingHeight(ModeSetting modeSetting) {
     return (modeSetting.getMods().size() * 10 + 12);
   }
   
   public static float calculateListSettingHeight(ListSetting listSetting) {
     int visibleCount = 0;
     for (BooleanSetting entry : listSetting.getSettings()) {
       if (entry.visible().booleanValue()) {
         visibleCount++;
       }
     } 
     return (visibleCount * 10 + 12);
   }
   
   public static float calculateSettingsHeight(Module module) {
     float height = 0.0F;
     List<Setting> settings = module.getSettings();
     if (settings == null || settings.isEmpty()) {
       return 0.0F;
     }
     
     boolean hasVisibleSetting = false;
     for (Setting setting : settings) {
       if (setting == null || !setting.visible().booleanValue()) {
         continue;
       }
       
       hasVisibleSetting = true;
       if (setting instanceof BooleanSetting || setting instanceof shame.astra.client.modules.settings.implement.BindSetting) {
         height += 12.0F; continue;
       }  if (setting instanceof shame.astra.client.modules.settings.implement.TextSetting) {
         height += 12.0F; continue;
       }  if (setting instanceof shame.astra.client.modules.settings.implement.FloatSetting) {
         height += 22.0F; continue;
       }  if (setting instanceof ModeSetting) { ModeSetting modeSetting = (ModeSetting)setting;
         height += calculateModeSettingHeight(modeSetting); continue; }
        if (setting instanceof ListSetting) { ListSetting listSetting = (ListSetting)setting;
         height += calculateListSettingHeight(listSetting); }
     
     } 
     
     if (hasVisibleSetting) {
       height += 3.0F;
     }
     return height;
   }
   
   public static float getModuleHeight(Module module, float openProgress) {
     return 20.0F + calculateSettingsHeight(module) * openProgress;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\clien\\ui\clickgui\ClickGuiLayout.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */