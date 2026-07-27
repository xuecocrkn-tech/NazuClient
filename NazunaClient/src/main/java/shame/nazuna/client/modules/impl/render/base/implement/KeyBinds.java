package shame.nazuna.client.modules.impl.render.base.implement;
 
 import it.unimi.dsi.fastutil.objects.ObjectListIterator;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import net.minecraft.class_4587;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.animation.AnimationUtils;
 import shame.nazuna.api.utils.animation.Easings;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.draggable.Draggable;
 import shame.nazuna.api.utils.input.KeyBoardUtils;
 import shame.nazuna.api.utils.render.RenderUtils;
 import shame.nazuna.api.utils.render.fonts.msdf.Font;
 import shame.nazuna.api.utils.render.fonts.msdf.Fonts;
 import shame.nazuna.api.utils.scissor.ScissorUtils;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.impl.render.base.InterfaceProcessing;
 
 public class KeyBinds extends InterfaceProcessing {
   private final Map<Module, AnimationUtils> animations = new HashMap<>();
   private final AnimationUtils widthAnimation = new AnimationUtils(60.0F, 10.5F, Easings.QUAD_OUT);
   
   private static final Map<Character, Character> RU_TO_EN = new HashMap<>();
   static {
     String ru = "йцукенгшщзхъфывапролджэячсмитьбюЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ";
     String en = "qwertyuiop[]asdfghjkl;'zxcvbnm,.QWERTYUIOP[]ASDFGHJKL;'ZXCVBNM,.";
     int length = Math.min(ru.length(), en.length());
     for (int i = 0; i < length; i++) {
       RU_TO_EN.put(Character.valueOf(ru.charAt(i)), Character.valueOf(en.charAt(i)));
     }
   }
   
   public KeyBinds(Draggable draggable) {
     super(draggable);
   }
   
   private Font issue(int size) { return Fonts.getFont("suisse", size); } private Font icon(int size) {
     return Fonts.getFont("icon1", size);
   }
   private AnimationUtils getAnimation(Module module) {
     return this.animations.computeIfAbsent(module, m -> new AnimationUtils(0.0F, 10.5F, Easings.QUAD_OUT));
   }
   
   private String toEnglish(String text) {
     StringBuilder result = new StringBuilder();
     for (char c : text.toCharArray()) {
       result.append(RU_TO_EN.getOrDefault(Character.valueOf(c), Character.valueOf(c)));
     }
     return result.toString();
   }
   
   private int getStaticThemeColor() {
     int[] colors = astra.INSTANCE.themeStorage.getThemes().getTheme().getColor();
     if (colors == null || colors.length == 0) {
       return -1;
     }
     
     int color = colors[0];
     if ((color >> 24 & 0xFF) == 0) {
       color = color & 0xFFFFFF | 0xFF000000;
     }
     return color;
   }
 
   
   public void onRender(EventRender.Default eventRender) {
     if (ModuleClass.interfaceModule.style.is("Обычный")) { DefaultStyle(eventRender); }
     else { WaveStyle(eventRender); }
      super.onRender(eventRender);
   }
   public void DefaultStyle(EventRender.Default eventRender) {
     int colorTheme;
     float baseX = this.draggable.getX(), y = this.draggable.getY();
     
     if (!astra.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
       colorTheme = (astra.INSTANCE.themeStorage.getThemes().getTheme()).color[0];
     } else {
       colorTheme = ColorUtils.getThemeColor();
     } 
     int staticAccentColor = getStaticThemeColor();
     
     float targetWidth = 64.0F;
     float targetHeight = 16.0F;
     int visibleCount = 0;
     ObjectListIterator<Module> objectListIterator1;
     for (objectListIterator1 = ModuleClass.INSTANCE.getObject().iterator(); objectListIterator1.hasNext(); ) { Module module = objectListIterator1.next();
       if (module.getKey() != -1) {
         AnimationUtils anim = getAnimation(module);
         anim.update(module.isEnable() ? 1.0F : 0.0F);
       }  }
 
     
     for (objectListIterator1 = ModuleClass.INSTANCE.getObject().iterator(); objectListIterator1.hasNext(); ) { Module module = objectListIterator1.next();
       if (module.getKey() != -1) {
         AnimationUtils anim = getAnimation(module);
         float animValue = anim.getValue();
         
         if (animValue > 0.01F) {
           visibleCount++;
           String keyName = toEnglish(KeyBoardUtils.getKeyName(module.getKey()));
           float keyWidth = issue(10).getWidth(keyName);
           float moduleWidth = issue(12).getWidth(module.getDisplayName()) + keyWidth + 25.0F;
           if (moduleWidth > targetWidth) targetWidth = moduleWidth; 
           targetHeight += 12.0F * animValue;
         } 
       }  }
 
     
     if (visibleCount > 0) targetHeight += 2.0F;
     
     this.widthAnimation.update(targetWidth);
     float width = this.widthAnimation.getValue() + 7.0F;
     float height = targetHeight;
     float rightEdge = baseX + 60.0F;
     float x = rightEdge - width;
     
     RenderUtils.drawDefaultHudElementRects(eventRender.getContext().method_51448(), x, y, width, height, colorTheme, isUnusualRectType());
     issue(14).draw(eventRender.getContext().method_51448(), "Binds", x + 5.0F, y + 6.0F, -1);
     icon(13).draw(eventRender.getContext().method_51448(), "f", rightEdge - 13.0F, y + 7.5F, colorTheme);
     
     float offsetY = 18.0F;
     for (ObjectListIterator<Module> objectListIterator2 = ModuleClass.INSTANCE.getObject().iterator(); objectListIterator2.hasNext(); ) { Module module = objectListIterator2.next();
       if (module.getKey() != -1) {
         AnimationUtils anim = getAnimation(module);
         float animValue = anim.getValue();
         
         if (animValue > 0.01F) {
           ScissorUtils.push();
           ScissorUtils.setFromComponentCoordinates(x, y, width, height);
           String keyName = toEnglish(KeyBoardUtils.getBindName(module.getKey()));
           float keyBoxWidth = Math.max(issue(10).getWidth(keyName) + 4.0F, 9.0F);
           
           int alpha = (int)(255.0F * animValue);
           int textColor = ColorUtils.rgba(255, 255, 255, alpha);
           int accentColor = ColorUtils.setAlphaColor(getStableThemeColor(), alpha);
           int grayColor = ColorUtils.rgba(55, 55, 55, alpha);
           int darkColor = ColorUtils.rgba(35, 35, 35, alpha);
           
           issue(12).draw(eventRender.getContext().method_51448(), module.getDisplayName(), x + 12.0F, y + 2.0F + offsetY, textColor);
           RenderUtils.drawRoundedRect(eventRender.getContext().method_51448(), x + 5.2F, y + offsetY + 0.3F, 2.55F, 5.7F, 0.15F, accentColor);
           
           float keyBoxX = rightEdge - keyBoxWidth - 5.0F;
           RenderUtils.drawDefaultHudInfoBox(eventRender.getContext().method_51448(), keyBoxX, y + offsetY, keyBoxWidth, grayColor, darkColor);
           issue(10).drawCenteredString(eventRender.getContext().method_51448(), keyName, keyBoxX + keyBoxWidth / 2.0F, y + offsetY + 2.8F, textColor);
           
           offsetY += 12.0F * animValue;
           ScissorUtils.pop();
           ScissorUtils.unset();
         } 
       }  }
 
     
     this.draggable.setWidth(60.0F);
     this.draggable.setHeight(height);
   }
   private int getStableThemeColor() {
     if (!astra.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
       return (astra.INSTANCE.themeStorage.getThemes().getTheme()).color[0];
     }
     return ColorUtils.getThemeColor();
   }
   
   public void WaveStyle(EventRender.Default eventRender) {
     class_4587 context = eventRender.getContext().method_51448();
     float x = this.draggable.getX(), y = this.draggable.getY();
     
     int time = (int)((float)(System.currentTimeMillis() % 2000L) / 2000.0F * 360.0F);
     
     int leftTop = ColorUtils.getThemeColor(time);
     int leftBottom = ColorUtils.getThemeColor(time + 30);
     int centerTop = ColorUtils.getThemeColor(time + 90);
     int centerBottom = ColorUtils.getThemeColor(time + 120);
     int rightTop = ColorUtils.getThemeColor(time + 180);
     int rightBottom = ColorUtils.getThemeColor(time + 210);
     
     List<Module> activeModules = new ArrayList<>();
     for (ObjectListIterator<Module> objectListIterator = ModuleClass.INSTANCE.getObject().iterator(); objectListIterator.hasNext(); ) { Module module = objectListIterator.next();
       if (module.getKey() <= 0) {
         module.getAnimka().update(0.0F);
         continue;
       } 
       module.getAnimka().update(module.isEnable() ? 1.0F : 0.0F);
       if (module.getAnimka().getValue() > 0.01F) {
         activeModules.add(module);
       } }
 
     
     float targetWidth = 84.0F;
     float height = 18.0F;
     int visibleModules = 0;
     
     for (Module module : activeModules) {
       float animValue = module.getAnimka().getValue();
       if (animValue <= 0.01F)
         continue;  visibleModules++;
       
       String line = module.getDisplayName().toLowerCase() + " >> toggle";
       targetWidth = Math.max(targetWidth, issue(14).getWidth(line) + 7.0F);
       height += 12.0F * animValue;
     } 
     
     this.widthAnimation.update(targetWidth);
     float animatedWidth = this.widthAnimation.getValue();
     
     if (visibleModules == 0) {
       float headerHeight = 18.0F;
       RenderUtils.drawWaveHudHeader(context, x, y, animatedWidth, 15.0F, 0.0F, 10.0F, 10.0F, leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);
 
       
       String str = "keybinds";
       float f1 = x + (animatedWidth - issue(15).getWidth(str)) / 2.0F;
       issue(15).drawStringWithShadow(eventRender.getContext().method_51448(), str, f1, y + 5.0F, -1);
       
       this.draggable.setWidth(animatedWidth);
       this.draggable.setHeight(headerHeight);
       
       return;
     } 
     RenderUtils.drawWaveHudPanel(context, x, y, animatedWidth, height, ColorUtils.rgba(25, 25, 25, 150), 15.0F, 0.0F, 10.0F, 10.0F, leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);
 
 
     
     String title = "keybinds";
     float titleX = x + (animatedWidth - issue(15).getWidth(title)) / 2.0F;
     issue(15).drawStringWithShadow(eventRender.getContext().method_51448(), title, titleX, y + 5.0F, -1);
     
     float yOffset = 18.0F;
     for (Module module : activeModules) {
       float animValue = module.getAnimka().getValue();
       if (animValue <= 0.01F)
         continue; 
       ScissorUtils.push();
       ScissorUtils.setFromComponentCoordinates(x, y, animatedWidth, height);
       
       int alpha = (int)(255.0F * animValue);
       int textColor = ColorUtils.rgba(255, 255, 255, alpha);
       
       String text = module.getDisplayName().toLowerCase() + " >> toggle";
       float textX = x + 5.5F;
       
       issue(14).draw(context, text, textX, y + yOffset + 2.0F, textColor);
       
       yOffset += 12.0F * animValue;
       
       ScissorUtils.unset();
       ScissorUtils.pop();
     } 
     
     this.draggable.setWidth(animatedWidth);
     this.draggable.setHeight(height);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\base\implement\KeyBinds.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */