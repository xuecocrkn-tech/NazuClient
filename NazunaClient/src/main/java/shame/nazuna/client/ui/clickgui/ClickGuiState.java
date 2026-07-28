package shame.nazuna.client.ui.clickgui;
 
 import java.util.ArrayList;
 import java.util.EnumMap;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import net.minecraft.Window;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.animation.AnimationUtils;
 import shame.nazuna.api.utils.animation.Easings;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.implement.BindSetting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.TextSetting;
 
 public class ClickGuiState
 {
   private static final Map<Character, Character> RU_TO_EN = new HashMap<>();
   
   static {
     String ru = "йцукенгшщзхъфывапролджэячсмитьбюЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ";
     
     String en = "qwertyuiop[]asdfghjkl;'zxcvbnm,.QWERTYUIOP[]ASDFGHJKL;'ZXCVBNM,.";
     int length = Math.min(ru.length(), en.length());
     for (int i = 0; i < length; i++) {
       RU_TO_EN.put(Character.valueOf(ru.charAt(i)), Character.valueOf(en.charAt(i)));
     }
   }
   
   private final Map<Module, Float> dotsRotation = new HashMap<>();
   private final Map<Module, AnimationUtils> moduleOpenAnimation = new HashMap<>();
   private final Map<BooleanSetting, AnimationUtils> booleanBackgroundAnimation = new HashMap<>();
   private final Map<BooleanSetting, AnimationUtils> booleanCircleAnimation = new HashMap<>();
   private final Map<FloatSetting, AnimationUtils> sliderAnimation = new HashMap<>();
   private final Map<FloatSetting, Double> sliderDragMouseX = new HashMap<>();
   private final Map<FloatSetting, Double> sliderDragRemainder = new HashMap<>();
   private final Map<String, AnimationUtils> modeAnimation = new HashMap<>();
   private final Map<String, AnimationUtils> listAnimation = new HashMap<>();
   private final Map<String, AnimationUtils> bindAnimation = new HashMap<>();
   private final Map<String, AnimationUtils> textHoverAnimation = new HashMap<>();
   private final Map<String, Float> textScrollPhase = new HashMap<>();
   private final Map<String, Boolean> textScrollFinishing = new HashMap<>();
   private final Map<String, Boolean> textScrollHovered = new HashMap<>();
   private final Map<Module.ModuleCategory, Float> categoryScrollTarget = new EnumMap<>(Module.ModuleCategory.class);
   private final Map<Module.ModuleCategory, AnimationUtils> categoryScrollAnimation = new EnumMap<>(Module.ModuleCategory.class);
   private final Map<Module.ModuleCategory, List<Module>> modulesByCategory = new EnumMap<>(Module.ModuleCategory.class);
   private final List<Module> allModules = new ArrayList<>();
   
   private float x;
   private float y;
   private BindSetting bindingSetting;
   private TextSetting editingTextSetting;
   private Module bindingModule;
   private float renderOffsetY;
   private boolean searchActive;
   private String searchText = "";
   private String undoSearchText = "";
   private int searchCursor = 0;
   private int searchSelectionAnchor = 0;
   private int searchSelectionCursor = 0;
   private boolean searchDragging;
   
   public ClickGuiState() {
     refreshModules();
   }
   
   public void refreshModules() {
     this.allModules.clear();
     this.allModules.addAll(ModuleClass.INSTANCE.getObject().stream()
         .filter(module -> (!"AutoBuy".equals(module.getName()) && !"AutoForest".equals(module.getName())))
         .toList());
     for (Module.ModuleCategory category : Module.ModuleCategory.values()) {
       this.modulesByCategory.put(category, this.allModules.stream().filter(module -> (module.getCategory() == category)).toList());
       this.categoryScrollTarget.putIfAbsent(category, Float.valueOf(0.0F));
       this.categoryScrollAnimation.putIfAbsent(category, new AnimationUtils(0.0F, 8.0F, Easings.CUBIC_OUT));
     } 
   }
   
   public void updatePosition(Window window, int categoryCount) {
     float totalCategoriesWidth = ClickGuiLayout.getTotalCategoriesWidth(categoryCount);
     this.x = window.method_4486() / 2.0F - totalCategoriesWidth / 2.0F;
     this.y = window.method_4502() / 2.0F - 137.5F;
   }
   
   public float getX() {
     return this.x;
   }
   
   public float getY() {
     return this.y;
   }
   
   public float getRenderOffsetY() {
     return this.renderOffsetY;
   }
   
   public void setRenderOffsetY(float renderOffsetY) {
     this.renderOffsetY = renderOffsetY;
   }
   
   public List<Module> getModules(Module.ModuleCategory category) {
     List<Module> modules = this.modulesByCategory.getOrDefault(category, List.of());
     if (this.searchText.isBlank()) {
       return modules;
     }
     
     String query = this.searchText.toLowerCase(Locale.ROOT);
     return modules.stream()
       .filter(module -> (module.getName().toLowerCase(Locale.ROOT).contains(query) || module.getDisplayName().toLowerCase(Locale.ROOT).contains(query) || module.getDisplayDescription().toLowerCase(Locale.ROOT).contains(query)))
 
       
       .toList();
   }
   
   public List<Module> getAllModules() {
     return this.allModules;
   }
   
   public String toEnglish(String text) {
     StringBuilder result = new StringBuilder();
     for (char c : text.toCharArray()) {
       result.append(RU_TO_EN.getOrDefault(Character.valueOf(c), Character.valueOf(c)));
     }
     return result.toString();
   }
   
   public float getSliderPos(FloatSetting setting) {
     float delta = setting.getMax() - setting.getMin();
     return (setting.get() - setting.getMin()) / delta;
   }
   
   public float getSliderValue(FloatSetting setting, float posX, double mouseX) {
     float delta = setting.getMax() - setting.getMin();
     float clickedX = (float)mouseX - posX;
     float value = Math.max(0.0F, Math.min(1.0F, clickedX / 79.0F));
     float outValue = setting.getMin() + delta * value;
     float increment = setting.getIncrement();
     outValue = Math.round(outValue / increment) * increment;
     return Math.max(setting.getMin(), Math.min(setting.getMax(), outValue));
   }
   
   public void beginSliderDrag(FloatSetting setting, double mouseX) {
     this.sliderDragMouseX.put(setting, Double.valueOf(mouseX));
     this.sliderDragRemainder.put(setting, Double.valueOf(0.0D));
   }
   
   public void endSliderDrag(FloatSetting setting) {
     this.sliderDragMouseX.remove(setting);
     this.sliderDragRemainder.remove(setting);
   }
   
   public float updateActiveSliderValue(FloatSetting setting, double mouseX) {
     double lastMouseX = ((Double)this.sliderDragMouseX.getOrDefault(setting, Double.valueOf(mouseX))).doubleValue();
     this.sliderDragMouseX.put(setting, Double.valueOf(mouseX));
     
     double deltaX = mouseX - lastMouseX;
     if (Math.abs(deltaX) < 1.0E-4D) {
       return setting.get();
     }
     
     float range = setting.getMax() - setting.getMin();
     float increment = setting.getIncrement();
     if (range <= 0.0F || increment <= 0.0F) {
       return setting.get();
     }
     
     double steps = (range / increment);
     if (steps <= 0.0D) {
       return setting.get();
     }
     
     double pixelsPerStep = 79.0D / steps;
     if (pixelsPerStep <= 0.0D) {
       return setting.get();
     }
     
     double accumulated = ((Double)this.sliderDragRemainder.getOrDefault(setting, Double.valueOf(0.0D))).doubleValue() + deltaX;
     int wholeSteps = (int)(accumulated / pixelsPerStep);
     if (wholeSteps == 0) {
       this.sliderDragRemainder.put(setting, Double.valueOf(accumulated));
       return setting.get();
     } 
     
     this.sliderDragRemainder.put(setting, Double.valueOf(accumulated - wholeSteps * pixelsPerStep));
     
     float value = setting.get() + wholeSteps * increment;
     value = Math.round(value / increment) * increment;
     return Math.max(setting.getMin(), Math.min(setting.getMax(), value));
   }
   
   public float getScroll(Module.ModuleCategory category) {
     AnimationUtils animation = this.categoryScrollAnimation.computeIfAbsent(category, key -> new AnimationUtils(0.0F, 8.0F, Easings.CUBIC_OUT));
     animation.update(((Float)this.categoryScrollTarget.getOrDefault(category, Float.valueOf(0.0F))).floatValue());
     return animation.getValue();
   }
   
   public void clampScroll(Module.ModuleCategory category, float contentHeight) {
     float totalHeight = getTotalModulesHeight(category);
     float maxScroll = Math.min(0.0F, contentHeight - totalHeight);
     float currentTarget = ((Float)this.categoryScrollTarget.getOrDefault(category, Float.valueOf(0.0F))).floatValue();
     if (currentTarget < maxScroll || currentTarget > 0.0F) {
       this.categoryScrollTarget.put(category, Float.valueOf(Math.max(maxScroll, Math.min(0.0F, currentTarget))));
     }
   }
   
   public void addScroll(Module.ModuleCategory category, double verticalAmount, float contentHeight) {
     float totalHeight = getTotalModulesHeight(category);
     float maxScroll = Math.min(0.0F, contentHeight - totalHeight);
     float currentTarget = ((Float)this.categoryScrollTarget.getOrDefault(category, Float.valueOf(0.0F))).floatValue();
     float newTarget = currentTarget + (float)(verticalAmount * 20.0D);
     this.categoryScrollTarget.put(category, Float.valueOf(Math.max(maxScroll, Math.min(0.0F, newTarget))));
   }
   
   public float getTotalModulesHeight(Module.ModuleCategory category) {
     float totalHeight = 0.0F;
     for (Module module : getModules(category)) {
       totalHeight += 4.0F + ClickGuiLayout.getModuleHeight(module, getOpenProgress(module));
     }
     return totalHeight;
   }
   
   public float getOpenProgress(Module module) {
     AnimationUtils animation = this.moduleOpenAnimation.computeIfAbsent(module, key -> new AnimationUtils(module.isOpen() ? 1.0F : 0.0F, 14.0F, Easings.CUBIC_OUT));
 
 
     
     animation.update(module.isOpen() ? 1.0F : 0.0F);
     return animation.getValue();
   }
   
   public float updateDotsRotation(Module module, float targetAngle) {
     float currentAngle = ((Float)this.dotsRotation.getOrDefault(module, Float.valueOf(targetAngle))).floatValue();
     currentAngle += (targetAngle - currentAngle) * 0.06F;
     if (Math.abs(targetAngle - currentAngle) < 0.001F) {
       currentAngle = targetAngle;
     }
     this.dotsRotation.put(module, Float.valueOf(currentAngle));
     return currentAngle;
   }
   
   public AnimationUtils getBooleanBackgroundAnimation(BooleanSetting setting) {
     return this.booleanBackgroundAnimation.computeIfAbsent(setting, key -> new AnimationUtils(setting.isState() ? 1.0F : 0.0F, 15.0F, Easings.CUBIC_OUT));
   }
 
 
 
   
   public AnimationUtils getBooleanCircleAnimation(BooleanSetting setting) {
     return this.booleanCircleAnimation.computeIfAbsent(setting, key -> new AnimationUtils(setting.isState() ? 1.0F : 0.0F, 8.2F, Easings.BACK_OUT));
   }
 
 
 
   
   public AnimationUtils getSliderAnimation(FloatSetting setting) {
     return this.sliderAnimation.computeIfAbsent(setting, key -> new AnimationUtils(getSliderPos(setting), 12.0F, Easings.CUBIC_OUT));
   }
 
 
 
   
   public AnimationUtils getModeAnimation(String key, boolean selected) {
     return this.modeAnimation.computeIfAbsent(key, unused -> new AnimationUtils(selected ? 1.0F : 0.0F, 10.0F, Easings.CUBIC_OUT));
   }
 
 
 
   
   public AnimationUtils getListAnimation(String key, boolean selected) {
     return this.listAnimation.computeIfAbsent(key, unused -> new AnimationUtils(selected ? 1.0F : 0.0F, 10.0F, Easings.CUBIC_OUT));
   }
 
 
 
   
   public AnimationUtils getBindAnimation(String key, boolean binding) {
     return this.bindAnimation.computeIfAbsent(key, unused -> new AnimationUtils(binding ? 1.0F : 0.0F, 10.0F, Easings.CUBIC_OUT));
   }
 
 
 
   
   public AnimationUtils getTextHoverAnimation(String key, boolean hovered) {
     return this.textHoverAnimation.computeIfAbsent(key, unused -> new AnimationUtils(hovered ? 1.0F : 0.0F, 9.0F, Easings.CUBIC_OUT));
   }
 
 
 
   
   public float advanceTextScrollPhase(String key, boolean hovered) {
     float phase = ((Float)this.textScrollPhase.getOrDefault(key, Float.valueOf(0.0F))).floatValue();
     boolean wasHovered = ((Boolean)this.textScrollHovered.getOrDefault(key, Boolean.valueOf(false))).booleanValue();
     boolean finishing = ((Boolean)this.textScrollFinishing.getOrDefault(key, Boolean.valueOf(false))).booleanValue();
     
     if (hovered) {
       phase += 0.004F;
       if (phase > 1.0F) {
         phase--;
       }
       finishing = false;
     } else {
       if (wasHovered && phase > 0.0F) {
         finishing = true;
       }
       if (finishing) {
         phase += 0.004F;
         if (phase >= 1.0F) {
           phase = 0.0F;
           finishing = false;
         } 
       } 
     } 
     
     this.textScrollHovered.put(key, Boolean.valueOf(hovered));
     this.textScrollFinishing.put(key, Boolean.valueOf(finishing));
     this.textScrollPhase.put(key, Float.valueOf(phase));
     return phase;
   }
   
   public boolean isTextScrollActive(String key, boolean hovered) {
     return (hovered || ((Boolean)this.textScrollFinishing.getOrDefault(key, Boolean.valueOf(false))).booleanValue());
   }
   
   public BindSetting getBindingSetting() {
     return this.bindingSetting;
   }
   
   public void setBindingSetting(BindSetting bindingSetting) {
     this.bindingSetting = bindingSetting;
   }
   
   public Module getBindingModule() {
     return this.bindingModule;
   }
   
   public void setBindingModule(Module bindingModule) {
     this.bindingModule = bindingModule;
   }
   
   public TextSetting getEditingTextSetting() {
     return this.editingTextSetting;
   }
   
   public void setEditingTextSetting(TextSetting editingTextSetting) {
     this.editingTextSetting = editingTextSetting;
   }
   
   public boolean isSearchActive() {
     return this.searchActive;
   }
   
   public void setSearchActive(boolean searchActive) {
     this.searchActive = searchActive;
   }
   
   public String getSearchText() {
     return this.searchText;
   }
   
   public void appendSearchChar(char chr) {
     if (Character.isISOControl(chr) || (this.searchText.length() >= 24 && !hasSearchSelection())) {
       return;
     }
     replaceSearchSelection(String.valueOf(chr));
   }
   
   public void removeLastSearchChar() {
     if (hasSearchSelection()) {
       replaceSearchSelection("");
       return;
     } 
     if (this.searchCursor > 0) {
       rememberSearchUndo();
       this.searchText = this.searchText.substring(0, this.searchCursor - 1) + this.searchText.substring(0, this.searchCursor - 1);
       this.searchCursor--;
       clearSearchSelection();
     } 
   }
   
   public void clearSearchText() {
     rememberSearchUndo();
     this.searchText = "";
     this.searchCursor = 0;
     clearSearchSelection();
   }
   
   public void setSearchText(String searchText) {
     rememberSearchUndo();
     this.searchText = sanitizeSearchText(searchText);
     this.searchCursor = this.searchText.length();
     clearSearchSelection();
   }
   
   public void restoreSearchUndo() {
     String current = this.searchText;
     this.searchText = (this.undoSearchText == null) ? "" : this.undoSearchText;
     this.undoSearchText = current;
     this.searchCursor = this.searchText.length();
     clearSearchSelection();
   }
   
   public int getSearchCursor() {
     return this.searchCursor;
   }
   
   public int getSearchSelectionStart() {
     return Math.min(this.searchSelectionAnchor, this.searchSelectionCursor);
   }
   
   public int getSearchSelectionEnd() {
     return Math.max(this.searchSelectionAnchor, this.searchSelectionCursor);
   }
   
   public boolean hasSearchSelection() {
     return (getSearchSelectionStart() != getSearchSelectionEnd());
   }
   
   public String getSelectedSearchText() {
     if (!hasSearchSelection()) {
       return "";
     }
     return this.searchText.substring(getSearchSelectionStart(), getSearchSelectionEnd());
   }
   
   public void selectAllSearchText() {
     this.searchSelectionAnchor = 0;
     this.searchSelectionCursor = this.searchText.length();
     this.searchCursor = this.searchText.length();
   }
   
   public void setSearchCursor(int cursor, boolean keepSelection) {
     this.searchCursor = clampSearchIndex(cursor);
     if (keepSelection) {
       this.searchSelectionCursor = this.searchCursor;
     } else {
       this.searchSelectionAnchor = this.searchCursor;
       this.searchSelectionCursor = this.searchCursor;
     } 
   }
   
   public void startSearchSelection(int index) {
     this.searchCursor = clampSearchIndex(index);
     this.searchSelectionAnchor = this.searchCursor;
     this.searchSelectionCursor = this.searchCursor;
     this.searchDragging = true;
   }
   
   public void updateSearchSelection(int index) {
     if (!this.searchDragging) {
       return;
     }
     this.searchCursor = clampSearchIndex(index);
     this.searchSelectionCursor = this.searchCursor;
   }
   
   public void stopSearchSelection() {
     this.searchDragging = false;
   }
   
   public boolean isSearchDragging() {
     return this.searchDragging;
   }
   
   public void replaceSearchSelection(String text) {
     rememberSearchUndo();
     String insert = sanitizeSearchText(text);
     int selectionStart = getSearchSelectionStart();
     int selectionEnd = getSearchSelectionEnd();
     if (!hasSearchSelection()) {
       selectionStart = this.searchCursor;
       selectionEnd = this.searchCursor;
     } 
     int available = Math.max(0, 24 - this.searchText.length() - selectionEnd - selectionStart);
     if (insert.length() > available) {
       insert = insert.substring(0, available);
     }
     this.searchText = this.searchText.substring(0, selectionStart) + this.searchText.substring(0, selectionStart) + insert;
     this.searchCursor = selectionStart + insert.length();
     clearSearchSelection();
   }
   
   private void clearSearchSelection() {
     this.searchSelectionAnchor = this.searchCursor;
     this.searchSelectionCursor = this.searchCursor;
     this.searchDragging = false;
   }
   
   private int clampSearchIndex(int index) {
     return Math.max(0, Math.min(this.searchText.length(), index));
   }
   
   private void rememberSearchUndo() {
     this.undoSearchText = this.searchText;
   }
   
   private String sanitizeSearchText(String text) {
     if (text == null || text.isEmpty()) {
       return "";
     }
     
     StringBuilder builder = new StringBuilder();
     for (int i = 0; i < text.length() && builder.length() < 24; i++) {
       char chr = text.charAt(i);
       if (!Character.isISOControl(chr)) {
         builder.append(chr);
       }
     } 
     return builder.toString();
   }
 }

