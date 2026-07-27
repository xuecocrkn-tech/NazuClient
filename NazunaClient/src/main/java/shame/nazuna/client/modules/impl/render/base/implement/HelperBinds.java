package shame.nazuna.client.modules.impl.render.base.implement;
 
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.util.ArrayList;
 import java.util.List;
 import net.minecraft.class_1799;
 import net.minecraft.class_1935;
 import net.minecraft.class_332;
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
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.impl.misc.ServerHelper;
 import shame.nazuna.client.modules.impl.render.base.InterfaceProcessing;
 
 public class HelperBinds extends InterfaceProcessing {
   private final AnimationUtils widthAnimation = new AnimationUtils(80.0F, 10.5F, Easings.QUAD_OUT);
   
   public HelperBinds(Draggable draggable) {
     super(draggable);
   }
   
   private Font issue(int size) {
     return Fonts.getFont("suisse", size);
   }
 
   
   public void onRender(EventRender.Default eventRender) {
     List<ServerHelper.HelperBind> binds = getVisibleBinds();
     if (binds.isEmpty()) {
       this.widthAnimation.update(0.0F);
       this.draggable.setWidth(0.0F);
       this.draggable.setHeight(0.0F);
       
       return;
     } 
     if (ModuleClass.interfaceModule.style.is("Обычный")) {
       DefaultStyle(eventRender, binds);
     } else {
       WaveStyle(eventRender, binds);
     } 
     
     super.onRender(eventRender);
   }
   
   private List<ServerHelper.HelperBind> getVisibleBinds() {
     ServerHelper serverHelper = ServerHelper.INSTANCE;
     List<ServerHelper.HelperBind> binds = new ArrayList<>();
     if (serverHelper == null) return binds;
 
 
     
     List<ServerHelper.HelperBind> helperBinds = serverHelper.isLonyMode() ? serverHelper.getLonyHelperBinds() : serverHelper.getSpookyHelperBinds();
     
     for (ServerHelper.HelperBind bind : helperBinds) {
       if (bind.bind().getKey() != -1) {
         binds.add(bind);
       }
     } 
     
     return binds;
   }
   
   private void DefaultStyle(EventRender.Default eventRender, List<ServerHelper.HelperBind> binds) {
     class_4587 matrices = eventRender.getContext().method_51448();
     float x = this.draggable.getX();
     float y = this.draggable.getY();
     int colorTheme = getThemeColor();
     
     int fontSize = 13;
     Font keyFont = issue(fontSize);
     float height = 19.0F;
     float itemSize = 9.8F;
     float itemScale = 0.61F;
     float fontGap = 2.8F;
     float cellGap = 5.0F;
     float sidePadding = 6.0F;
     float width = getCompactWidth(binds, keyFont, itemSize, fontGap, cellGap, sidePadding, 60.0F);
     
     this.widthAnimation.update(width);
     float animatedWidth = this.widthAnimation.getValue();
     
     drawDefaultPanel(matrices, x, y, animatedWidth, height, colorTheme);
     
     if (binds.isEmpty()) {
       issue(12).draw(matrices, "Helper", x + 5.0F, y + 6.0F, ColorUtils.rgba(255, 255, 255, 230));
       this.draggable.setWidth(animatedWidth);
       this.draggable.setHeight(height);
       
       return;
     } 
     drawCompactBinds(eventRender.getContext(), binds, keyFont, x, y, height, itemSize, itemScale, fontGap, cellGap, sidePadding, 8.2F);
     
     this.draggable.setWidth(animatedWidth);
     this.draggable.setHeight(height);
   }
   
   private void WaveStyle(EventRender.Default eventRender, List<ServerHelper.HelperBind> binds) {
     class_4587 matrices = eventRender.getContext().method_51448();
     float x = this.draggable.getX();
     float y = this.draggable.getY();
     
     int time = (int)((float)(System.currentTimeMillis() % 2000L) / 2000.0F * 360.0F);
     int leftTop = ColorUtils.getThemeColor(time);
     int leftBottom = ColorUtils.getThemeColor(time + 30);
     int centerTop = ColorUtils.getThemeColor(time + 90);
     int centerBottom = ColorUtils.getThemeColor(time + 120);
     int rightTop = ColorUtils.getThemeColor(time + 180);
     int rightBottom = ColorUtils.getThemeColor(time + 210);
     
     Font keyFont = issue(14);
     float height = 22.0F;
     float itemSize = 11.0F;
     float itemScale = 0.69F;
     float fontGap = 3.5F;
     float cellGap = 6.0F;
     float sidePadding = 7.0F;
     float width = getCompactWidth(binds, keyFont, itemSize, fontGap, cellGap, sidePadding, 72.0F);
     
     this.widthAnimation.update(width);
     float animatedWidth = this.widthAnimation.getValue();
     
     if (binds.isEmpty()) {
       RenderUtils.drawWaveHudHeader(matrices, x, y, animatedWidth, 15.0F, 0.0F, 10.0F, 10.0F, leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);
       
       String title = "helper";
       float titleX = x + (animatedWidth - issue(15).getWidth(title)) / 2.0F;
       issue(15).drawStringWithShadow(matrices, title, titleX, y + 5.0F, -1);
       this.draggable.setWidth(animatedWidth);
       this.draggable.setHeight(18.0F);
       
       return;
     } 
     RenderUtils.drawWaveHudPanel(matrices, x, y, animatedWidth, height, ColorUtils.rgba(25, 25, 25, 150), 3.5F, 0.0F, 10.0F, 10.0F, leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);
 
 
     
     drawCompactBinds(eventRender.getContext(), binds, keyFont, x, y, height, itemSize, itemScale, fontGap, cellGap, sidePadding, 9.5F);
     
     this.draggable.setWidth(animatedWidth);
     this.draggable.setHeight(height);
   }
   
   private float getCompactWidth(List<ServerHelper.HelperBind> binds, Font keyFont, float itemSize, float fontGap, float cellGap, float sidePadding, float emptyWidth) {
     if (binds.isEmpty()) {
       return emptyWidth;
     }
     
     float width = sidePadding * 2.0F;
     for (int i = 0; i < binds.size(); i++) {
       String keyName = KeyBoardUtils.getBindName(((ServerHelper.HelperBind)binds.get(i)).bind().getKey());
       width += itemSize + fontGap + keyFont.getWidth(keyName);
       if (i < binds.size() - 1) {
         width += cellGap;
       }
     } 
     return width;
   }
 
   
   private void drawCompactBinds(class_332 context, List<ServerHelper.HelperBind> binds, Font keyFont, float x, float y, float height, float itemSize, float itemScale, float fontGap, float cellGap, float sidePadding, float textOffsetY) {
     class_4587 matrices = context.method_51448();
     float offsetX = x + sidePadding;
     float itemY = y + (height - itemSize) * 0.5F;
     float textY = y + textOffsetY;
     
     for (int i = 0; i < binds.size(); i++) {
       ServerHelper.HelperBind bind = binds.get(i);
       String keyName = KeyBoardUtils.getBindName(bind.bind().getKey());
       drawItemIcon(context, new class_1799((class_1935)bind.item()), offsetX, itemY, itemScale);
       keyFont.draw(matrices, keyName, offsetX + itemSize + fontGap, textY, ColorUtils.rgba(255, 255, 255, 240));
       
       offsetX += itemSize + fontGap + keyFont.getWidth(keyName);
       if (i < binds.size() - 1) {
         offsetX += cellGap;
       }
     } 
   }
   
   private void drawItemIcon(class_332 context, class_1799 stack, float x, float y, float scale) {
     class_4587 matrices = context.method_51448();
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableDepthTest();
     RenderSystem.depthMask(false);
     matrices.method_22903();
     matrices.method_46416(x, y, 0.0F);
     matrices.method_22905(scale, scale, 1.0F);
     context.method_51427(stack, 0, 0);
     matrices.method_22909();
     RenderSystem.depthMask(false);
     RenderSystem.disableDepthTest();
   }
   
   private void drawDefaultPanel(class_4587 matrices, float x, float y, float width, float height, int colorTheme) {
     RenderUtils.drawDefaultHudThemedPanel(matrices, x, y, width, height, 3.0F, 3.5F, colorTheme);
     if (isUnusualRectType()) {
       RenderUtils.drawHudSquarePattern(matrices, x, y, width, height, colorTheme);
     }
   }
   
   private int getThemeColor() {
     if (!astra.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
       return (astra.INSTANCE.themeStorage.getThemes().getTheme()).color[0];
     }
     return ColorUtils.getThemeColor();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\base\implement\HelperBinds.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */