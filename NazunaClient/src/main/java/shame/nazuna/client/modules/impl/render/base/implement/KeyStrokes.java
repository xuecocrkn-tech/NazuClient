package shame.nazuna.client.modules.impl.render.base.implement;
 
 import java.util.ArrayList;
 import java.util.List;
 import net.minecraft.class_310;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.draggable.Draggable;
 import shame.nazuna.api.utils.render.RenderUtils;
 import shame.nazuna.api.utils.render.fonts.msdf.Font;
 import shame.nazuna.api.utils.render.fonts.msdf.Fonts;
 import shame.nazuna.client.modules.impl.render.base.InterfaceProcessing;
 
 public class KeyStrokes extends InterfaceProcessing {
   private final class_310 mc = class_310.method_1551();
   
   private final List<Long> leftClicks = new ArrayList<>();
   
   private boolean wasLmbPressed = false;
   
   private float animationProgress = 0.0F;
   private long lastUpdateTime = System.currentTimeMillis();
   private float pulseAnimation = 0.0F;
   private float breathingAnimation = 0.0F;
   
   public KeyStrokes(Draggable draggable) {
     super(draggable);
   }
 
   
   public void onRender(EventRender.Default eventRender) {
     float x = this.draggable.getX(), y = this.draggable.getY();
 
     
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
     
     Font font = Fonts.getFont("suisse", 15);
     Font smallFont = Fonts.getFont("suisse", 10);
     
     float keySize = 20.0F;
     float gap = 2.0F;
     
     boolean wPressed = this.mc.field_1690.field_1894.method_1434();
     boolean aPressed = this.mc.field_1690.field_1913.method_1434();
     boolean sPressed = this.mc.field_1690.field_1881.method_1434();
     boolean dPressed = this.mc.field_1690.field_1849.method_1434();
     boolean spacePressed = this.mc.field_1690.field_1903.method_1434();
     boolean lmbPressed = this.mc.field_1690.field_1886.method_1434();
     boolean rmbPressed = this.mc.field_1690.field_1904.method_1434();
     
     if (lmbPressed && !this.wasLmbPressed) {
       this.leftClicks.add(Long.valueOf(currentTime));
     }
     this.wasLmbPressed = lmbPressed;
     
     this.leftClicks.removeIf(time -> (currentTime - time.longValue() > 1000L));
     
     int lmbCps = this.leftClicks.size();
     
     int themeColor = ColorUtils.getThemeColor((int)this.animationProgress);
     int themeColor2 = ColorUtils.getThemeColor((int)(this.animationProgress + 90.0F) % 360);
     
     float wX = x + keySize + gap;
     float wY = y;
     drawKey(eventRender, wX, wY, keySize, keySize, "W", wPressed, font, pulseScale, breathingAlpha, themeColor);
     
     float aX = x;
     float aY = y + keySize + gap;
     drawKey(eventRender, aX, aY, keySize, keySize, "A", aPressed, font, pulseScale, breathingAlpha, themeColor);
     
     float sX = x + keySize + gap;
     float sY = y + keySize + gap;
     drawKey(eventRender, sX, sY, keySize, keySize, "S", sPressed, font, pulseScale, breathingAlpha, themeColor);
     
     float dX = x + (keySize + gap) * 2.0F;
     float dY = y + keySize + gap;
     drawKey(eventRender, dX, dY, keySize, keySize, "D", dPressed, font, pulseScale, breathingAlpha, themeColor);
     
     float spaceWidth = keySize * 3.0F + gap * 2.0F;
     float spaceHeight = 20.0F;
     float spaceX = x;
     float spaceY = y + (keySize + gap) * 2.0F;
     drawKey(eventRender, spaceX, spaceY, spaceWidth, spaceHeight, "Space", spacePressed, font, pulseScale, breathingAlpha, themeColor);
     
     float mouseWidth = (spaceWidth - gap) / 2.0F;
     float mouseHeight = 20.0F;
     float lmbX = x;
     float lmbY = y + (keySize + gap) * 2.0F + spaceHeight + gap;
     
     drawKeyWithCps(eventRender, lmbX, lmbY, mouseWidth, mouseHeight, "LMB", lmbPressed, font, smallFont, lmbCps, themeColor, themeColor2, pulseScale, breathingAlpha);
     
     float rmbX = x + mouseWidth + gap;
     float rmbY = y + (keySize + gap) * 2.0F + spaceHeight + gap;
     drawKey(eventRender, rmbX, rmbY, mouseWidth, mouseHeight, "RMB", rmbPressed, font, pulseScale, breathingAlpha, themeColor);
     
     float totalWidth = keySize * 3.0F + gap * 2.0F;
     float totalHeight = keySize * 2.0F + gap + spaceHeight + gap + mouseHeight + gap;
     
     this.draggable.setWidth(totalWidth);
     this.draggable.setHeight(totalHeight);
     
     super.onRender(eventRender);
   }
   
   private void drawKey(EventRender.Default eventRender, float x, float y, float width, float height, String text, boolean pressed, Object font, float pulseScale, float breathingAlpha, int themeColor) {
     int bgColor = pressed ? ColorUtils.rgba(180, 180, 180, 200) : ColorUtils.rgba(25, 25, 25, 150);
     int textColor = pressed ? ColorUtils.rgba(0, 0, 0, 255) : ColorUtils.rgba(255, 255, 255, 255);
 
     
     if (pressed) {
       int glowColor = ColorUtils.applyAlpha(themeColor, breathingAlpha * 0.4F);
       RenderUtils.drawShadow(eventRender.getContext().method_51448(), x - 1.0F, y - 1.0F, width + 2.0F, height + 2.0F, 2.0F, 5.0F * pulseScale, glowColor);
     } 
     
     RenderUtils.drawKeyStrokeRect(eventRender.getContext().method_51448(), x, y, width, height, 3.0F, bgColor);
     
     Font f = Fonts.getFont("suisse", 15);
     float textWidth = f.getWidth(text);
     float textHeight = 8.0F;
     
     float textX = x + (width - textWidth) / 2.0F;
     float textY = y + (height - textHeight) / 2.0F;
     
     f.draw(eventRender.getContext().method_51448(), text, textX - 0.5F, textY + 2.0F, textColor);
   }
   
   private void drawKeyWithCps(EventRender.Default eventRender, float x, float y, float width, float height, String text, boolean pressed, Object font, Object smallFont, int cps, int themeColor, int themeColor2, float pulseScale, float breathingAlpha) {
     int bgColor = pressed ? ColorUtils.rgba(180, 180, 180, 200) : ColorUtils.rgba(25, 25, 25, 150);
     int textColor = pressed ? ColorUtils.rgba(0, 0, 0, 255) : ColorUtils.rgba(255, 255, 255, 255);
 
     
     if (pressed) {
       int glowColor = ColorUtils.applyAlpha(themeColor, breathingAlpha * 0.5F);
       RenderUtils.drawShadow(eventRender.getContext().method_51448(), x - 1.0F, y - 1.0F, width + 2.0F, height + 2.0F, 2.0F, 6.0F * pulseScale, glowColor);
     } 
     
     RenderUtils.drawKeyStrokeRect(eventRender.getContext().method_51448(), x, y, width, height, 3.0F, bgColor);
     
     Font f = Fonts.getFont("suisse", 15);
     Font sf = Fonts.getFont("suisse", 12);
     
     float textWidth = f.getWidth(text);
     float textX = x + (width - textWidth) / 2.0F;
     float textHeight = 8.0F;
     float textY = y + (height - textHeight) / 2.0F;
     f.draw(eventRender.getContext().method_51448(), text, textX - 0.5F, textY + 2.0F, textColor);
     
     String cpsText = "cps: " + cps;
     float cpsWidth = sf.getWidth(cpsText);
     float cpsX = x + (width - cpsWidth) / 2.0F;
     float cpsY = textY + 12.0F;
 
     
     sf.drawGradientStringHorizontal(eventRender.getContext().method_51448(), cpsText, cpsX, cpsY - 3.0F, themeColor, themeColor2);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\base\implement\KeyStrokes.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */