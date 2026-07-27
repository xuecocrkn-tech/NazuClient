package shame.nazuna.client.modules.impl.render.base.implement;
 
 import java.awt.Color;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;
 import java.util.Random;
 import net.minecraft.class_4587;
 import net.minecraft.class_640;
 import net.minecraft.class_642;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.draggable.Draggable;
 import shame.nazuna.api.utils.render.RenderUtils;
 import shame.nazuna.api.utils.render.fonts.msdf.Font;
 import shame.nazuna.api.utils.render.fonts.msdf.Fonts;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.impl.render.base.InterfaceProcessing;
 
 public class WaterMark extends InterfaceProcessing {
   private boolean showFps = true;
   private boolean showMs = true;
   private float animationProgress = 0.0F; private boolean showServer = true; private boolean showTps = true;
   private long lastUpdateTime = System.currentTimeMillis();
   private float pulseAnimation = 0.0F;
   private float breathingAnimation = 0.0F;
 
   
   private List<Particle> particles = new ArrayList<>();
   private Random random = new Random();
   private static class Particle { float x;
     float y;
     float velX;
     float velY;
     float life;
     float maxLife;
     float size;
     
     Particle(float x, float y) {
       this.x = x;
       this.y = y;
       Random r = new Random();
       this.velX = (r.nextFloat() - 0.5F) * 0.5F;
       this.velY = -r.nextFloat() * 1.5F - 0.5F;
       this.maxLife = r.nextFloat() * 2.0F + 1.0F;
       this.life = this.maxLife;
       this.size = r.nextFloat() * 1.5F + 0.5F;
     }
     
     void update(float deltaTime) {
       this.x += this.velX * deltaTime * 10.0F;
       this.y += this.velY * deltaTime * 10.0F;
       this.life -= deltaTime;
     }
     
     boolean isDead() {
       return (this.life <= 0.0F);
     }
     
     float getAlpha() {
       return Math.min(1.0F, this.life / this.maxLife);
     } }
 
   
   public static String getUsername() {
     return "Shame";
   }
   
   public static String getUID() {
     return "-1";
   }
   
   public WaterMark(Draggable draggable) {
     super(draggable);
   }
   
   public boolean isShowFps() {
     return this.showFps;
   }
   
   public void setShowFps(boolean showFps) {
     this.showFps = showFps;
   }
   
   public boolean isShowMs() {
     return this.showMs;
   }
   
   public void setShowMs(boolean showMs) {
     this.showMs = showMs;
   }
   
   public boolean isShowServer() {
     return this.showServer;
   }
   
   public void setShowServer(boolean showServer) {
     this.showServer = showServer;
   }
   
   public boolean isShowTps() {
     return this.showTps;
   }
   
   public void setShowTps(boolean showTps) {
     this.showTps = showTps;
   }
 
   
   public void onRender(EventRender.Default eventRender) {
     if (shouldUseYouGameStyle()) { YouGameStyle(eventRender); }
     else if (ModuleClass.interfaceModule.style.is("Wave")) { WaveStyle(eventRender); }
     else { DefaultStyle(eventRender); }
      super.onRender(eventRender);
   }
 
 
 
   
   private boolean shouldUseYouGameStyle() {
     return (!ModuleClass.interfaceModule.style.is("Wave") || ModuleClass.interfaceModule.youGameWatermark
       .isState());
   }
 
   
   public void YouGameStyle(EventRender.Default eventRender) {
     class_4587 matrices = eventRender.getContext().method_51448();
     float x = this.draggable.getX();
     float y = this.draggable.getY();
     Font text = Fonts.getFont("suisse", 12);
     Font icon = Fonts.getFont("iconnew", 12);
     
     int themeColor = astra.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow") ? ColorUtils.getThemeColor() : (astra.INSTANCE.themeStorage.getThemes().getTheme()).color[0];
     int panel = ColorUtils.rgba(23, 24, 29, 218);
     int cell = ColorUtils.rgba(35, 36, 43, 228);
     int white = ColorUtils.rgba(245, 246, 250, 255);
     int muted = ColorUtils.rgba(185, 187, 195, 255);
     
     int fps = (mc != null) ? mc.method_47599() : 0;
     int ping = 0;
     if (mc != null && mc.field_1724 != null && mc.method_1562() != null) {
       class_640 entry = mc.method_1562().method_2871(mc.field_1724.method_5667());
       if (entry != null) ping = entry.method_2959();
     
     } 
     float bps = (mc != null && mc.field_1724 != null) ? (float)(Math.hypot(mc.field_1724.method_23317() - mc.field_1724.field_6014, mc.field_1724.method_23321() - mc.field_1724.field_5969) * 20.0D) : 0.0F;
     String fpsText = "" + fps + " fps";
     String pingText = "" + ping + " ms";
 
     
     String coordinates = (mc != null && mc.field_1724 != null) ? ("x: " + (int)Math.floor(mc.field_1724.method_23317()) + " y: " + (int)Math.floor(mc.field_1724.method_23318()) + " z: " + (int)Math.floor(mc.field_1724.method_23321())) : "x: 0 y: 0 z: 0";
     String tpsText = formatOneDecimal(getServerTps()) + " tps";
     String bpsText = formatOneDecimal(bps) + " bps";
     
     float h = 16.0F, gap = 2.0F, logoW = 18.0F;
     float titleW = Math.max(46.0F, text.getStringWidth("astra") + 18.0F);
     float fpsW = Math.max(37.0F, text.getStringWidth(fpsText) + 13.0F);
     float pingW = Math.max(35.0F, text.getStringWidth(pingText) + 13.0F);
     float topW = logoW + gap + titleW + gap + fpsW + gap + pingW;
     drawYouGameCell(matrices, x, y, logoW, h, panel, themeColor);
     icon.draw(matrices, "A", x + 5.4F, y + 5.3F, themeColor);
     drawYouGameCell(matrices, x + logoW + gap, y, titleW, h, cell, themeColor);
     text.draw(matrices, "astra", x + logoW + gap + 7.0F, y + 5.2F, white);
     drawYouGameCell(matrices, x + logoW + gap + titleW + gap, y, fpsW, h, cell, themeColor);
     text.draw(matrices, fpsText, x + logoW + gap + titleW + gap + 6.0F, y + 5.2F, white);
     drawYouGameCell(matrices, x + topW - pingW, y, pingW, h, cell, themeColor);
     text.draw(matrices, pingText, x + topW - pingW + 6.0F, y + 5.2F, white);
     
     float bottomY = y + h + gap;
     float coordinatesW = Math.max(94.0F, text.getStringWidth(coordinates) + 12.0F);
     float tpsW = Math.max(42.0F, text.getStringWidth(tpsText) + 12.0F);
     float bpsW = Math.max(42.0F, text.getStringWidth(bpsText) + 12.0F);
     float bottomW = coordinatesW + gap + tpsW + gap + bpsW;
     drawYouGameCell(matrices, x, bottomY, coordinatesW, h, cell, themeColor);
     text.draw(matrices, coordinates, x + 6.0F, bottomY + 5.2F, muted);
     drawYouGameCell(matrices, x + coordinatesW + gap, bottomY, tpsW, h, cell, themeColor);
     text.draw(matrices, tpsText, x + coordinatesW + gap + 6.0F, bottomY + 5.2F, white);
     drawYouGameCell(matrices, x + coordinatesW + gap + tpsW + gap, bottomY, bpsW, h, cell, themeColor);
     text.draw(matrices, bpsText, x + coordinatesW + gap + tpsW + gap + 6.0F, bottomY + 5.2F, white);
     this.draggable.setWidth(Math.max(topW, bottomW));
     this.draggable.setHeight(h * 2.0F + gap);
   }
   
   private void drawYouGameCell(class_4587 matrices, float x, float y, float width, float height, int fill, int accent) {
     RenderUtils.drawShadow(matrices, x, y, width, height, 5.0F, 7.0F, ColorUtils.applyAlpha(accent, 0.18F));
     RenderUtils.drawRoundedRect(matrices, x, y, width, height, 3.0F, fill);
     RenderUtils.drawRoundedRect(matrices, x, y, width, 0.8F, 3.0F, ColorUtils.applyAlpha(accent, 0.78F));
   }
   public void DefaultStyle(EventRender.Default eventRender) {
     int iconTop, iconBottom, iconMid;
     class_4587 matrices = eventRender.getContext().method_51448();
     float x = this.draggable.getX();
     float y = this.draggable.getY();
 
     
     long currentTime = System.currentTimeMillis();
     float deltaTime = (float)(currentTime - this.lastUpdateTime) / 1000.0F;
     this.lastUpdateTime = currentTime;
     this.animationProgress += deltaTime * 2.0F;
     if (this.animationProgress > 360.0F) this.animationProgress -= 360.0F;
 
     
     this.pulseAnimation += deltaTime * 3.0F;
     if (this.pulseAnimation > 360.0F) this.pulseAnimation -= 360.0F; 
     float pulseScale = 1.0F + (float)Math.sin(this.pulseAnimation) * 0.15F;
 
     
     this.breathingAnimation += deltaTime * 2.5F;
     if (this.breathingAnimation > 360.0F) this.breathingAnimation -= 360.0F; 
     float breathingAlpha = 0.7F + (float)Math.sin(this.breathingAnimation) * 0.3F;
 
     
     this.particles.removeIf(Particle::isDead);
     for (Particle p : this.particles) {
       p.update(deltaTime);
     }
 
     
     if (this.random.nextFloat() < 0.3F) {
       this.particles.add(new Particle(x + this.random.nextFloat() * 120.0F, y + 15.0F));
     }
     
     Font logoFont = Fonts.getFont("logo", 17);
     Font iconNew14 = Fonts.getFont("iconnew", 14);
     Font iconNew15 = Fonts.getFont("iconnew", 15);
     Font icon14 = Fonts.getFont("icon", 14);
     Font statsIconFont = Fonts.getFont("astra", 14);
     if (statsIconFont == null) statsIconFont = (iconNew14 != null) ? iconNew14 : icon14; 
     Font suisse13 = Fonts.getFont("suisse", 13);
     
     float astraRectH = 16.0F;
     int iconSize = 17;
     String iconGlyph = "A";
     float iconW = logoFont.getStringWidth(iconGlyph);
     float iconX = x + (17.0F - iconW) / 2.0F;
     float iconY = y + 5.5F;
 
 
 
 
     
     if (!astra.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
       iconTop = (astra.INSTANCE.themeStorage.getThemes().getTheme()).color[0];
       iconBottom = ColorUtils.gradient(iconTop, iconTop, (float)Math.sin((this.animationProgress / 180.0F) * Math.PI) * 0.5F + 0.5F);
       iconMid = ColorUtils.gradient(iconTop, iconTop, (float)Math.cos((this.animationProgress / 180.0F) * Math.PI) * 0.5F + 0.5F);
     } else {
       iconTop = ColorUtils.getThemeColor((int)this.animationProgress);
       iconBottom = ColorUtils.getThemeColor((int)(this.animationProgress + 180.0F) % 360);
       iconMid = ColorUtils.getThemeColor((int)(this.animationProgress + 90.0F) % 360);
     } 
     
     boolean drawSquares = isUnusualRectType();
     float rect2Pad = 3.0F;
     String username = getUsername();
     String UID = getUID();
     int whiteColor = (new Color(255, 255, 255, 255)).getRGB();
     float textY = y + 6.8F;
     
     String brandText = "";
     float brandTextX = iconX + iconW + 2.5F;
     float brandTextW = suisse13.getStringWidth(brandText);
     
     float astraRectX = x;
     float astraRectY = y;
     float astraRectW = brandTextX + brandTextW + 1.5F - x;
 
     
     RenderUtils.drawDefaultHudThemedPanel(matrices, astraRectX, astraRectY, astraRectW, astraRectH, 2.8F, 3.3F, iconTop);
 
     
     float borderAlpha = 0.3F + (float)Math.sin((this.pulseAnimation * 2.0F)) * 0.2F;
     int borderColor = ColorUtils.applyAlpha(iconTop, borderAlpha);
     RenderUtils.drawShadow(matrices, astraRectX, astraRectY - 0.5F, astraRectW, 1.0F, 1.0F, 1.5F, borderColor);
     
     if (drawSquares);
 
 
     
     int logoShadow = ColorUtils.applyAlpha(iconTop, breathingAlpha * 0.6F);
     int logoShadow2 = ColorUtils.applyAlpha(iconBottom, breathingAlpha * 0.4F);
     int logoShadow3 = ColorUtils.applyAlpha(iconMid, breathingAlpha * 0.5F);
 
     
     RenderUtils.drawShadow(matrices, iconX - 1.5F, iconY - 3.0F, iconW + 1.0F, (iconSize - 7), 5.0F, 12.0F * pulseScale, logoShadow);
     
     RenderUtils.drawShadow(matrices, iconX - 0.5F, iconY - 2.0F, iconW, (iconSize - 9), 4.0F, 8.0F, logoShadow3);
     
     RenderUtils.drawShadow(matrices, iconX + 0.3F, iconY - 1.25F, iconW - 1.0F, (iconSize - 11), 3.0F, 5.0F, logoShadow2);
 
     
     logoFont.drawGradientStringHorizontal(matrices, iconGlyph, iconX - 0.25F, iconY, iconTop, iconBottom);
     suisse13.drawString(matrices, brandText, brandTextX, textY, whiteColor);
     
     float rect2X = astraRectX + astraRectW + 2.5F;
     
     float rect2H = 15.85F;
     int icon2Size = 14;
     String iconGlyph2 = "e";
     float icon2Y = y + 7.45F;
     
     int icon3Size = 14;
     String fpsIconGlyph = "j";
     String pingIconGlyph = "f";
     float icon3Y = y + 7.25F;
     
     int fps = (mc != null) ? mc.method_47599() : 0;
     String fpsValue = String.valueOf(fps);
     String fpsSuffix = "fps";
     String fpsText = fpsValue + fpsValue;
     
     int ping = 0;
     if (mc != null && mc.field_1724 != null && mc.method_1562() != null) {
       class_640 entry = mc.method_1562().method_2871(mc.field_1724.method_5667());
       if (entry != null) ping = entry.method_2959(); 
     } 
     String pingValue = String.valueOf(ping);
     String pingSuffix = "ms";
     String pingText = pingValue + pingValue;
     
     float contentW = rect2Pad;
     contentW += iconNew14.getStringWidth(iconGlyph2) + 1.0F;
     if (!username.isEmpty()) {
       contentW += suisse13.getStringWidth(username) + 2.0F;
     }
     if (this.showFps) {
       contentW += (statsIconFont != null) ? (statsIconFont.getStringWidth(fpsIconGlyph) + 2.0F) : 0.0F;
       contentW += suisse13.getStringWidth(fpsText) + 2.0F;
     } 
     if (this.showMs) {
       contentW += (statsIconFont != null) ? (statsIconFont.getStringWidth(pingIconGlyph) + 2.0F) : 0.0F;
       contentW += suisse13.getStringWidth(pingText) + 2.0F;
     } 
     contentW += rect2Pad;
     
     float rect2W = contentW - 1.05F;
 
     
     RenderUtils.drawDefaultHudThemedPanel(matrices, rect2X, astraRectY, rect2W, rect2H, 2.8F, 3.3F, iconTop);
 
     
     float border2Alpha = 0.3F + (float)Math.sin((this.pulseAnimation * 2.0F + 1.0F)) * 0.2F;
     int border2Color = ColorUtils.applyAlpha(iconBottom, border2Alpha);
     RenderUtils.drawShadow(matrices, rect2X, astraRectY - 0.5F, rect2W, 1.0F, 1.0F, 1.5F, border2Color);
 
     
     int panelGlow = ColorUtils.applyAlpha(iconTop, breathingAlpha * 0.15F);
     RenderUtils.drawShadow(matrices, rect2X - 1.0F, astraRectY - 1.0F, rect2W + 2.0F, rect2H + 2.0F, 2.0F, 4.0F, panelGlow);
     
     if (drawSquares) {
       RenderUtils.drawHudSquarePattern(matrices, rect2X, astraRectY, rect2W, rect2H, iconTop);
     }
     
     float drawX = rect2X + rect2Pad + 1.5F;
 
     
     int iconGlow = ColorUtils.applyAlpha(iconTop, breathingAlpha * 0.4F);
     RenderUtils.drawShadow(matrices, drawX - 2.0F, icon2Y - 1.0F, iconNew14.getStringWidth(iconGlyph2) + 2.0F, 8.0F, 2.0F, 3.0F, iconGlow);
     iconNew14.drawGradientStringHorizontal(matrices, iconGlyph2, drawX - 1.0F, icon2Y, iconTop, iconBottom);
     drawX += iconNew14.getStringWidth(iconGlyph2) + 1.0F;
     
     if (!username.isEmpty()) {
       
       int nameGlow = ColorUtils.applyAlpha(iconMid, breathingAlpha * 0.2F);
       RenderUtils.drawShadow(matrices, drawX - 1.0F, textY - 1.0F, suisse13.getStringWidth(username) + 2.0F, 10.0F, 1.0F, 2.0F, nameGlow);
       suisse13.drawString(matrices, username, drawX, textY, whiteColor);
       drawX += suisse13.getStringWidth(username) + 2.0F;
     } 
     
     if (this.showFps) {
       if (statsIconFont != null) {
         
         int fpsIconGlow = ColorUtils.applyAlpha(iconTop, breathingAlpha * 0.35F);
         RenderUtils.drawShadow(matrices, drawX - 1.0F, icon3Y - 1.0F, statsIconFont.getStringWidth(fpsIconGlyph) + 2.0F, 8.0F, 2.0F, 3.0F, fpsIconGlow);
         statsIconFont.drawGradientStringHorizontal(matrices, fpsIconGlyph, drawX, icon3Y, iconTop, iconBottom);
         drawX += statsIconFont.getStringWidth(fpsIconGlyph) + 2.0F;
       } 
       
       suisse13.drawString(matrices, fpsValue, drawX, textY, whiteColor);
       suisse13.drawGradientStringHorizontal(matrices, fpsSuffix, drawX + suisse13.getStringWidth(fpsValue) - 1.0F, textY, iconTop, iconBottom);
       drawX += suisse13.getStringWidth(fpsText) + 2.0F;
     } 
     
     if (this.showMs) {
       if (statsIconFont != null) {
         
         int pingIconGlow = ColorUtils.applyAlpha(iconBottom, breathingAlpha * 0.35F);
         RenderUtils.drawShadow(matrices, drawX - 1.0F, icon3Y - 1.0F, statsIconFont.getStringWidth(pingIconGlyph) + 2.0F, 8.0F, 2.0F, 3.0F, pingIconGlow);
         statsIconFont.drawGradientStringHorizontal(matrices, pingIconGlyph, drawX, icon3Y, iconTop, iconBottom);
         drawX += statsIconFont.getStringWidth(pingIconGlyph) + 2.0F;
       } 
       
       suisse13.drawString(matrices, pingValue, drawX, textY, whiteColor);
       suisse13.drawGradientStringHorizontal(matrices, pingSuffix, drawX + suisse13.getStringWidth(pingValue) - 0.5F, textY, iconTop, iconBottom);
     } 
     
     String serverName = "Singleplayer";
     if (mc != null) {
       class_642 info = mc.method_1558();
       if (info != null && info.field_3761 != null && !info.field_3761.isEmpty()) {
         serverName = info.field_3761;
       }
     } 
     
     boolean showBottom = (this.showServer || this.showTps);
     float rectBtmY = astraRectY + astraRectH + 2.0F;
     float rectBtmH = 15.85F;
     
     int iconSmallSize = 15;
     float iconSmallW = iconNew15.getStringWidth(iconGlyph);
     float iconSmallY = rectBtmY + (rectBtmH - iconSmallSize) / 2.0F + 6.5F;
     float serverTextY = rectBtmY + (rectBtmH - 12.0F) / 2.0F + 4.8F;
     String serverDisplayName = formatServerNameForDisplay(serverName);
     float serverTextW = suisse13.getStringWidth(serverDisplayName);
     int extraIconSize = 15;
     String extraIconGlyph = "y";
     float extraIconW = iconNew15.getStringWidth(extraIconGlyph);
     float extraIconY = rectBtmY + (rectBtmH - extraIconSize) / 2.0F + 6.4F;
     String tpsValue = formatOneDecimal(getServerTps());
     String tpsSuffix = "tps";
     String tpsText = tpsValue + tpsValue;
     float tpsTextW = suisse13.getStringWidth(tpsText);
     float rectBtmW = 0.0F;
     if (showBottom) {
       float bottomX = x + rect2Pad + 8.5F;
       if (this.showServer) {
         bottomX += iconSmallW + 3.0F + serverTextW;
       }
       if (this.showTps) {
         if (this.showServer) bottomX += 3.0F; 
         bottomX += extraIconW + 3.0F + tpsTextW;
       } 
       rectBtmW = Math.max(40.0F, bottomX + rect2Pad - x);
 
       
       RenderUtils.drawDefaultHudThemedPanel(matrices, x, rectBtmY, rectBtmW - 2.85F, rectBtmH, 2.8F, 3.3F, iconTop);
 
       
       float borderBtmAlpha = 0.3F + (float)Math.sin((this.pulseAnimation * 2.0F + 2.0F)) * 0.2F;
       int borderBtmColor = ColorUtils.applyAlpha(iconMid, borderBtmAlpha);
       RenderUtils.drawShadow(matrices, x, rectBtmY - 0.5F, rectBtmW - 2.85F, 1.0F, 1.0F, 1.5F, borderBtmColor);
 
       
       int bottomPanelGlow = ColorUtils.applyAlpha(iconBottom, breathingAlpha * 0.15F);
       RenderUtils.drawShadow(matrices, x - 1.0F, rectBtmY - 1.0F, rectBtmW - 1.85F, rectBtmH + 2.0F, 2.0F, 4.0F, bottomPanelGlow);
       
       if (drawSquares) {
         RenderUtils.drawHudSquarePattern(matrices, x, rectBtmY, rectBtmW, rectBtmH, iconTop);
       }
       
       float drawBottomX = x + rect2Pad + 7.0F;
       if (this.showServer) {
         
         int serverIconGlow = ColorUtils.applyAlpha(iconMid, breathingAlpha * 0.4F);
         RenderUtils.drawShadow(matrices, drawBottomX - 7.5F, iconSmallY - 1.0F, iconSmallW + 2.0F, 8.0F, 2.0F, 3.0F, serverIconGlow);
         iconNew15.drawGradientStringHorizontal(matrices, "n", drawBottomX - 6.5F, iconSmallY, iconTop, iconBottom);
         drawBottomX += iconSmallW + 3.0F;
         drawServerNameWithThemeParts(matrices, serverDisplayName, drawBottomX, serverTextY, iconTop, iconBottom, whiteColor);
         drawBottomX += serverTextW;
       } 
       if (this.showTps) {
         if (this.showServer) drawBottomX += 3.0F;
         
         int tpsIconGlow = ColorUtils.applyAlpha(iconBottom, breathingAlpha * 0.4F);
         RenderUtils.drawShadow(matrices, drawBottomX - 2.5F, extraIconY - 1.0F, extraIconW + 2.0F, 8.0F, 2.0F, 3.0F, tpsIconGlow);
         iconNew15.drawGradientStringHorizontal(matrices, extraIconGlyph, drawBottomX - 1.5F, extraIconY, iconTop, iconBottom);
         drawBottomX += extraIconW + 3.0F;
         suisse13.drawString(matrices, tpsValue, drawBottomX - 1.75F, serverTextY, whiteColor);
         suisse13.drawGradientStringHorizontal(matrices, tpsSuffix, drawBottomX + suisse13.getStringWidth(tpsValue) - 2.5F, serverTextY, iconTop, iconBottom);
       } 
     } 
     
     float totalW = Math.max(astraRectW + 2.0F + rect2W, rectBtmW);
     this.draggable.setWidth(totalW);
     this.draggable.setHeight(showBottom ? (astraRectH + 1.0F + rectBtmH) : astraRectH);
 
     
     for (Particle particle : this.particles) {
       float particleAlpha = particle.getAlpha() * breathingAlpha;
       int particleColor = ColorUtils.applyAlpha(iconMid, particleAlpha * 0.6F);
       RenderUtils.drawShadow(matrices, particle.x, particle.y, particle.size, particle.size, 1.0F, particle.size * 2.0F, particleColor);
     } 
   }
   
   public void WaveStyle(EventRender.Default eventRender) {
     float x = this.draggable.getX(), y = this.draggable.getY();
     class_4587 matrices = eventRender.getContext().method_51448();
     Font waveFont = Fonts.getFont("wave", 30);
     String watermarkText = "astra";
 
     
     long currentTime = System.currentTimeMillis();
     float deltaTime = (float)(currentTime - this.lastUpdateTime) / 1000.0F;
     this.lastUpdateTime = currentTime;
     this.animationProgress += deltaTime * 2.0F;
     if (this.animationProgress > 360.0F) this.animationProgress -= 360.0F;
 
     
     this.pulseAnimation += deltaTime * 3.0F;
     if (this.pulseAnimation > 360.0F) this.pulseAnimation -= 360.0F; 
     float pulseScale = 1.0F + (float)Math.sin(this.pulseAnimation) * 0.2F;
 
     
     this.breathingAnimation += deltaTime * 2.5F;
     if (this.breathingAnimation > 360.0F) this.breathingAnimation -= 360.0F; 
     float breathingAlpha = 0.8F + (float)Math.sin(this.breathingAnimation) * 0.2F;
 
     
     int indexColor = ColorUtils.getThemeColor((int)(90.0F + this.animationProgress) % 360);
     int indexColor2 = ColorUtils.getThemeColor((int)(180.0F + this.animationProgress) % 360);
     int indexColor3 = ColorUtils.getThemeColor((int)(270.0F + this.animationProgress) % 360);
     int indexColor4 = ColorUtils.getColor((int)(360.0F + this.animationProgress) % 360);
     int indexColor5 = ColorUtils.getThemeColor((int)(45.0F + this.animationProgress) % 360);
     float glowWidth = 95.0F + waveFont.getStringWidth("ful");
 
     
     int shadow1 = ColorUtils.applyAlpha(indexColor4, breathingAlpha * 0.6F);
     int shadow2 = ColorUtils.applyAlpha(indexColor2, breathingAlpha * 0.5F);
     int shadow3 = ColorUtils.applyAlpha(indexColor, breathingAlpha * 0.4F);
     int shadow4 = ColorUtils.applyAlpha(indexColor3, breathingAlpha * 0.7F);
     int shadow5 = ColorUtils.applyAlpha(indexColor5, breathingAlpha * 0.3F);
 
     
     RenderUtils.drawShadow(matrices, x - 4.0F, y - 4.0F, glowWidth + 8.0F, 20.0F, 14.0F, 25.0F * pulseScale, shadow1, shadow2, shadow3, shadow4);
     
     RenderUtils.drawShadow(matrices, x - 2.0F, y - 2.0F, glowWidth + 4.0F, 16.0F, 12.0F, 20.0F, shadow4, shadow2, shadow5, shadow3);
     
     RenderUtils.drawShadow(matrices, x, y, glowWidth, 12.0F, 10.0F, 15.0F, shadow4, shadow2, shadow1, shadow3);
 
     
     waveFont.drawGradientStringHorizontal(matrices, watermarkText, x, y, indexColor, indexColor2);
     
     this.draggable.setWidth(Math.max(glowWidth, waveFont.getStringWidth(watermarkText)));
     this.draggable.setHeight(12.0F);
 
     
     this.particles.removeIf(Particle::isDead);
     for (Particle p : this.particles) {
       p.update(deltaTime);
     }
 
     
     if (this.random.nextFloat() < 0.4F) {
       this.particles.add(new Particle(x + this.random.nextFloat() * glowWidth, y + 10.0F));
     }
 
     
     for (Particle particle : this.particles) {
       float particleAlpha = particle.getAlpha() * breathingAlpha;
       int particleColor = ColorUtils.applyAlpha(indexColor5, particleAlpha * 0.8F);
       RenderUtils.drawShadow(matrices, particle.x, particle.y, particle.size, particle.size, 1.0F, particle.size * 3.0F, particleColor);
     } 
   }
   
   private void drawServerNameWithThemeParts(class_4587 matrices, String serverName, float x, float y, int themeColor, int themeColor2, int whiteColor) {
     Font font = Fonts.getFont("suisse", 13);
     String[] parts = serverName.split("\\.");
     if (parts.length < 2) {
       font.drawString(matrices, serverName, x, y, whiteColor);
       
       return;
     } 
     String mainPart = String.join(".", Arrays.<CharSequence>copyOf((CharSequence[])parts, parts.length - 1));
     String suffixPart = "." + parts[parts.length - 1];
     
     font.drawString(matrices, mainPart, x, y, whiteColor);
     float suffixX = x + font.getStringWidth(mainPart) - 2.0F;
     
     font.drawGradientStringHorizontal(matrices, suffixPart, suffixX, y, themeColor, themeColor2);
   }
   
   private String formatServerNameForDisplay(String serverName) {
     if (serverName == null || serverName.isEmpty()) {
       return "";
     }
     
     String host = serverName;
     int portIndex = host.indexOf(':');
     if (portIndex > 0) {
       host = host.substring(0, portIndex);
     }
     
     String[] parts = host.split("\\.");
     if (parts.length >= 3) {
       return String.join(".", Arrays.<CharSequence>copyOfRange((CharSequence[])parts, 1, parts.length));
     }
     return host;
   }
   
   private float getServerTps() {
     if (astra.INSTANCE == null || astra.INSTANCE.tpsCalc == null) {
       return 20.0F;
     }
     return Math.max(0.0F, Math.min(20.0F, astra.INSTANCE.tpsCalc.getTPS()));
   }
   
   private String formatOneDecimal(float value) {
     int scaled = Math.round(value * 10.0F);
     return "" + scaled / 10 + "." + scaled / 10;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\base\implement\WaterMark.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */