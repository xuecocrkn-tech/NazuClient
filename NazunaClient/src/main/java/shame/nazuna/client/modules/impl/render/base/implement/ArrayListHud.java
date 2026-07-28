package shame.nazuna.client.modules.impl.render.base.implement;
 import it.unimi.dsi.fastutil.objects.ObjectArrayList;
 import java.util.ArrayList;
 import java.util.Comparator;
 import java.util.List;
 import net.minecraft.MatrixStack;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.draggable.Draggable;
 import shame.nazuna.api.utils.render.RenderUtils;
 import shame.nazuna.api.utils.render.fonts.msdf.Font;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.impl.render.base.InterfaceProcessing;
 
 public class ArrayListHud extends InterfaceProcessing {
   private static final float LINE_HEIGHT = 9.5F;
   private static final float FLOW_SPEED = 1000.0F;
   private static final Comparator<ModuleEntry> MODULE_WIDTH_COMPARATOR;
   
   static {
     MODULE_WIDTH_COMPARATOR = Comparator.<ModuleEntry>comparingDouble(entry -> entry.width).reversed();
   }
   private final List<ModuleEntry> visibleModules = new ArrayList<>();
 
   
   private float animationProgress = 0.0F;
   private long lastUpdateTime = System.currentTimeMillis();
   private float pulseAnimation = 0.0F;
   private float breathingAnimation = 0.0F;
   
   public ArrayListHud(Draggable draggable) {
     super(draggable);
   }
   
   private Font font() {
     return Fonts.getFont("suisse", 14);
   }
   
   private void drawFlowingText(MatrixStack matrices, Font font, String text, float x, float y, int color, float alphaMul) {
     int textColor = ColorUtils.setAlphaColor(color, (int)(255.0F * alphaMul));
     font.draw(matrices, text, x, y, textColor);
   }
 
   
   public void onRender(EventRender.Default eventRender) {
     MatrixStack matrices = eventRender.getContext().method_51448();
     Font font = font();
     ObjectArrayList objectArrayList = ModuleClass.INSTANCE.getObject();
 
     
     long currentTime = System.currentTimeMillis();
     float deltaTime = (float)(currentTime - this.lastUpdateTime) / 1000.0F;
     this.lastUpdateTime = currentTime;
     this.animationProgress += deltaTime * 2.0F;
     if (this.animationProgress > 360.0F) this.animationProgress -= 360.0F;
 
     
     this.pulseAnimation += deltaTime * 3.0F;
     if (this.pulseAnimation > 360.0F) this.pulseAnimation -= 360.0F; 
     float pulseScale = 1.0F + (float)Math.sin(this.pulseAnimation) * 0.1F;
 
     
     this.breathingAnimation += deltaTime * 2.5F;
     if (this.breathingAnimation > 360.0F) this.breathingAnimation -= 360.0F; 
     float breathingAlpha = 0.8F + (float)Math.sin(this.breathingAnimation) * 0.2F;
     
     this.visibleModules.clear();
     for (Module module : objectArrayList) {
       module.getArrayAnimka().update(module.isEnable() ? 1.0F : 0.0F);
       float anim = module.getArrayAnimka().getValue();
       if (anim <= 0.03F)
         continue; 
       String displayName = module.getDisplayName();
       this.visibleModules.add(new ModuleEntry(displayName.toLowerCase(), font.getWidth(displayName), anim));
     } 
     this.visibleModules.sort(MODULE_WIDTH_COMPARATOR);
     long now = System.currentTimeMillis();
     
     float x = this.draggable.getX();
     float y = this.draggable.getY();
     float maxWidth = 0.0F;
     boolean leftHalf = (x <= mc.method_22683().method_4486() * 0.5F);
     
     for (ModuleEntry entry : this.visibleModules) {
       maxWidth = Math.max(maxWidth, entry.width);
     }
     
     float yOffset = 0.0F;
     for (int i = 0; i < this.visibleModules.size(); i++) {
       float drawX; ModuleEntry entry = this.visibleModules.get(i);
       float anim = entry.anim;
       float lineStep = 9.5F * anim;
       
       int indexShift = (int)((float)now * 1000.0F / 10.0F) + i * 42;
       int rowColor = ColorUtils.getThemeColor(indexShift);
       int rowColor2 = ColorUtils.getThemeColor(indexShift + 90);
       int rowColor3 = ColorUtils.getThemeColor(indexShift + 45);
 
       
       int glowAlpha = (int)((leftHalf ? '' : 'ª') * anim * breathingAlpha);
       int glow1 = ColorUtils.setAlphaColor(rowColor, glowAlpha);
       int glow2 = ColorUtils.setAlphaColor(rowColor2, glowAlpha);
       int glow3 = ColorUtils.setAlphaColor(rowColor3, (int)(glowAlpha * 0.7F));
       
       float textWidth = entry.width;
       
       if (leftHalf) {
         drawX = x - 3.0F;
       } else {
         drawX = x + maxWidth - textWidth - 3.0F;
       } 
       float drawY = y + yOffset + (1.0F - anim) * 7.0F;
 
       
       float shadowX = leftHalf ? (drawX - 0.6F) : (drawX - 1.5F);
       float shadowW = leftHalf ? (textWidth - 4.0F) : textWidth;
 
       
       RenderUtils.drawShadow(matrices, shadowX - 1.0F, drawY - 1.0F, shadowW + 2.0F, 8.0F, 6.0F, 14.0F * pulseScale, glow3, glow3, glow1, glow1);
       
       RenderUtils.drawShadow(matrices, shadowX, drawY, shadowW, 6.0F, 5.0F, 11.0F, glow2, glow2, glow1, glow1);
 
       
       float textX = leftHalf ? (drawX - 0.8F) : (drawX - 2.0F);
       int textColor = ColorUtils.setAlphaColor(rowColor, (int)(255.0F * anim));
       int textColor2 = ColorUtils.setAlphaColor(rowColor2, (int)(255.0F * anim * 0.8F));
 
       
       font.drawGradientStringHorizontal(matrices, entry.lowerName, textX, drawY + 1.5F, textColor, textColor2);
       
       yOffset += lineStep;
     } 
     
     if (yOffset > 0.5F) {
       float lineX = leftHalf ? (x - 6.5F) : (x + maxWidth - 7.0F);
       float lineWidth = 2.5F;
 
       
       int topLineColor = ColorUtils.setAlphaColor(ColorUtils.getThemeColor((int)this.animationProgress), (int)(220.0F * breathingAlpha));
       int bottomLineColor = ColorUtils.setAlphaColor(ColorUtils.getThemeColor((int)(this.animationProgress + 180.0F) % 360), (int)(220.0F * breathingAlpha));
       int midLineColor = ColorUtils.setAlphaColor(ColorUtils.getThemeColor((int)(this.animationProgress + 90.0F) % 360), (int)(200.0F * breathingAlpha));
 
       
       int lineGlow = ColorUtils.setAlphaColor(topLineColor, (int)(100.0F * breathingAlpha));
       RenderUtils.drawShadow(matrices, lineX - 1.0F, y, lineWidth + 2.0F, yOffset - 2.0F, 2.0F, 5.0F * pulseScale, lineGlow);
 
       
       RenderUtils.drawGradientRect(matrices, lineX, y, lineWidth, yOffset - 2.0F, 0.0F, topLineColor, bottomLineColor);
     } 
     
     this.draggable.setWidth(maxWidth + 4.0F);
     this.draggable.setHeight(yOffset);
     
     super.onRender(eventRender);
   }
 }

