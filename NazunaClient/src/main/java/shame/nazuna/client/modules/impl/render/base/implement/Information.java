package shame.nazuna.client.modules.impl.render.base.implement;
 
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.draggable.Draggable;
 import shame.nazuna.api.utils.math.MathUtils;
 import shame.nazuna.api.utils.render.RenderUtils;
 import shame.nazuna.api.utils.render.fonts.msdf.Font;
 import shame.nazuna.api.utils.render.fonts.msdf.Fonts;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.impl.render.base.InterfaceProcessing;
 
 public class Information
   extends InterfaceProcessing {
   public Information(Draggable draggable) {
     super(draggable);
   }
 
   
   public void onRender(EventRender.Default eventRender) {
     if (!ModuleClass.interfaceModule.style.is("Wave")) { DefaultStyle(eventRender); }
     else { WaveStyle(eventRender); }
      super.onRender(eventRender);
   }
   public void DefaultStyle(EventRender.Default eventRender) {
     int colorTheme;
     float x = this.draggable.getX(), y = this.draggable.getY();
     Font font = Fonts.getFont("suisse", 13);
     Font iconFont = Fonts.getFont("icon", 16);
     Font smallIconFont = Fonts.getFont("icon", 15);
 
     
     if (!NazunaClient.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
       colorTheme = (NazunaClient.INSTANCE.themeStorage.getThemes().getTheme()).color[0];
     } else {
       colorTheme = ColorUtils.getThemeColor();
     } 
     boolean drawSquares = isUnusualRectType();
     
     int px = (int)Math.floor(mc.field_1724.method_23317());
     int py = (int)Math.floor(mc.field_1724.method_23318());
     int pz = (int)Math.floor(mc.field_1724.method_23321());
     
     float height = 16.0F;
     double bps = MathUtils.calculateBPS();
     String xValue = String.valueOf(px);
     String yValue = String.valueOf(py);
     String zValue = String.valueOf(pz);
     String coordsText = xValue + "x " + xValue + "y " + yValue + "z";
     String bpsValue = formatTwoDecimals(bps);
     String bpsSuffix = " b/s";
     float widthbps = font.getWidth(bpsValue + bpsValue);
     float xbps = x + 17.0F + widthbps;
     float widthCords = font.getWidth(coordsText);
     float totalWidth = 13.0F + widthCords + widthbps + 2.0F + 13.8F;
     
     RenderUtils.drawDefaultHudThemedPanel(eventRender.getContext().method_51448(), x, y, totalWidth, height, 3.0F, 3.5F, colorTheme);
     if (drawSquares) {
       RenderUtils.drawHudSquarePattern(eventRender.getContext().method_51448(), x, y, totalWidth, height, colorTheme);
     }
     
     float speedTextX = x + 13.5F;
     float bpsValueWidth = font.getWidth(bpsValue);
     font.draw(eventRender.getContext().method_51448(), bpsValue, speedTextX, y + 6.6D, -1);
     font.draw(eventRender.getContext().method_51448(), bpsSuffix, (speedTextX + bpsValueWidth - 2.0F), y + 6.6D, colorTheme);
     float coordsX = xbps + 9.0F;
     font.draw(eventRender.getContext().method_51448(), xValue, coordsX, y + 6.6D, -1);
     coordsX += font.getWidth(xValue);
     font.draw(eventRender.getContext().method_51448(), "x", (coordsX - 1.0F), y + 6.6D, colorTheme);
     coordsX += font.getWidth("x ");
     font.draw(eventRender.getContext().method_51448(), yValue, coordsX, y + 6.6D, -1);
     coordsX += font.getWidth(yValue);
     font.draw(eventRender.getContext().method_51448(), "y", (coordsX - 1.0F), y + 6.6D, colorTheme);
     coordsX += font.getWidth("y ");
     font.draw(eventRender.getContext().method_51448(), zValue, coordsX, y + 6.6D, -1);
     coordsX += font.getWidth(zValue);
     font.draw(eventRender.getContext().method_51448(), "z", (coordsX - 1.0F), y + 6.6D, colorTheme);
     iconFont.draw(eventRender.getContext().method_51448(), "c", x + 3.25D, y + 6.6D, colorTheme);
     smallIconFont.draw(eventRender.getContext().method_51448(), "x", (xbps - 1.0F), y + 6.85D, colorTheme);
 
     
     this.draggable.setHeight(height);
     this.draggable.setWidth(totalWidth);
   }
   
   public void WaveStyle(EventRender.Default eventRender) {
     float x = this.draggable.getX(), y = this.draggable.getY();
     
     float time = (float)(System.currentTimeMillis() % 2000L) / 2000.0F * 360.0F;
     
     int leftTop1 = ColorUtils.getThemeColor((int)time);
     int leftBottom1 = ColorUtils.getThemeColor((int)(time + 30.0F));
     int centerTop1 = ColorUtils.getThemeColor((int)(time + 90.0F));
     int centerBottom1 = ColorUtils.getThemeColor((int)(time + 120.0F));
     int rightTop1 = ColorUtils.getThemeColor((int)(time + 180.0F));
     int rightBottom1 = ColorUtils.getThemeColor((int)(time + 210.0F));
     
     String title = "coords";
     String xText = "x: " + (int)mc.field_1724.method_19538().method_10216();
     String yText = "y: " + (int)mc.field_1724.method_19538().method_10214();
     String zText = "z: " + (int)mc.field_1724.method_19538().method_10215();
     
     Font font = Fonts.getFont("suisse", 15);
     
     float xWidth = font.getWidth(xText);
     float yWidth = font.getWidth(yText);
     float zWidth = font.getWidth(zText);
     float titleWidth = font.getWidth(title);
     
     float maxCoordWidth = Math.max(xWidth, Math.max(yWidth, zWidth));
     
     float padding = 9.0F;
     float rectWidth = maxCoordWidth + padding;
     float rectHeight = 40.0F;
     
     rectWidth = Math.max(rectWidth, 35.0F);
     
     float centerX = x + rectWidth / 2.0F;
     
     RenderUtils.drawWaveHudPanel(eventRender.getContext().method_51448(), x, y, rectWidth, rectHeight, ColorUtils.rgba(25, 25, 25, 150), 3.5F, 0.0F, 10.0F, 10.0F, leftTop1, leftBottom1, centerTop1, centerBottom1, rightTop1, rightBottom1);
 
 
     
     float barPadding = 5.0F;
     RenderUtils.drawWaveHudHeader(eventRender.getContext().method_51448(), x + barPadding, y + 12.0F, rectWidth - barPadding * 2.0F, 2.5F, 0.0F, 10.0F, 10.0F, leftTop1, leftBottom1, centerTop1, centerBottom1, rightTop1, rightBottom1);
 
     
     font.drawStringWithShadow(eventRender.getContext().method_51448(), title, centerX - titleWidth / 2.0F, y + 5.0F, -1);
     font.drawStringWithShadow(eventRender.getContext().method_51448(), xText, x + 4.5F, y + 17.0F, -1);
     font.drawStringWithShadow(eventRender.getContext().method_51448(), yText, x + 4.5F, y + 24.0F, -1);
     font.drawStringWithShadow(eventRender.getContext().method_51448(), zText, x + 4.5F, y + 31.0F, -1);
     
     float bpsX = x + rectWidth + 5.0F;
     float bpsY = y;
     
     double bps = MathUtils.calculateBPS();
     
     String bpsTitle = "bps";
     String bpsText = String.valueOf((int)bps);
     
     float bpsTitleWidth = font.getWidth(bpsTitle);
     float bpsTextWidth = font.getWidth(bpsText);
     
     float bpsRectWidth = Math.max(bpsTitleWidth, bpsTextWidth) + 10.0F;
     float bpsRectHeight = 25.0F;
     
     bpsRectWidth = Math.max(bpsRectWidth, 30.0F);
     
     float bpsCenterX = bpsX + bpsRectWidth / 2.0F;
     
     RenderUtils.drawWaveHudPanel(eventRender.getContext().method_51448(), bpsX, bpsY, bpsRectWidth, bpsRectHeight, ColorUtils.rgba(25, 25, 25, 150), 3.5F, 0.0F, 10.0F, 10.0F, leftTop1, leftBottom1, centerTop1, centerBottom1, rightTop1, rightBottom1);
 
 
     
     RenderUtils.drawWaveHudHeader(eventRender.getContext().method_51448(), bpsX + barPadding, bpsY + 12.0F, bpsRectWidth - barPadding * 2.0F, 2.5F, 0.0F, 10.0F, 10.0F, leftTop1, leftBottom1, centerTop1, centerBottom1, rightTop1, rightBottom1);
 
     
     font.drawStringWithShadow(eventRender.getContext().method_51448(), bpsTitle, bpsCenterX - bpsTitleWidth / 2.0F, bpsY + 5.0F, -1);
     font.drawStringWithShadow(eventRender.getContext().method_51448(), bpsText, bpsCenterX - bpsTextWidth / 2.0F, bpsY + 17.0F, -1);
     
     float totalWidth = rectWidth + 5.0F + bpsRectWidth;
     this.draggable.setWidth(totalWidth);
     this.draggable.setHeight(rectHeight);
   }
   
   private String formatTwoDecimals(double value) {
     int scaled = (int)Math.round(value * 100.0D);
     int fraction = Math.abs(scaled % 100);
     return "" + scaled / 100 + "." + scaled / 100 + ((fraction < 10) ? "0" : "");
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\base\implement\Information.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */