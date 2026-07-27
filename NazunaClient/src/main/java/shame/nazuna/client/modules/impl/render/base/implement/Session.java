package shame.nazuna.client.modules.impl.render.base.implement;
 
 import net.minecraft.ServerInfo;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.draggable.Draggable;
 import shame.nazuna.api.utils.render.RenderUtils;
 import shame.nazuna.api.utils.render.fonts.msdf.Font;
 import shame.nazuna.api.utils.render.fonts.msdf.Fonts;
 import shame.nazuna.client.modules.impl.render.base.InterfaceProcessing;
 
 public class Session extends InterfaceProcessing {
   private long sessionStartTime = System.currentTimeMillis();
   
   public Session(Draggable draggable) {
     super(draggable);
   }
 
   
   public void onRender(EventRender.Default eventRender) {
     float x = this.draggable.getX(), y = this.draggable.getY();
     long now = System.currentTimeMillis();
     
     float height = 18.0F;
     
     String serverName = "local";
     if (mc != null) {
       ServerInfo info = mc.method_1558();
       if (info != null && info.field_3761 != null && !info.field_3761.isEmpty()) {
         serverName = info.field_3761;
       }
     } 
     
     String playerName = "unknown";
     if (mc != null && mc.field_1724 != null) {
       playerName = mc.field_1724.method_5477().getString();
     } else if (mc != null && mc.method_1548() != null) {
       playerName = mc.method_1548().method_1676();
     } 
     
     long elapsed = now - this.sessionStartTime;
     long totalSeconds = elapsed / 1000L;
     long hours = totalSeconds / 3600L;
     long minutes = totalSeconds % 3600L / 60L;
     long seconds = totalSeconds % 60L;
     String playTime = "" + hours + "h " + hours + "m " + minutes + "s";
     
     String titleText = "sessioninfo";
     String serverText = "server: " + serverName;
     String nameText = "name: " + playerName;
     String playTimeText = "playtime: " + playTime;
     
     Font font = Fonts.getFont("suisse", 15);
     
     float titleWidth = font.getWidth(titleText);
     float serverWidth = font.getWidth(serverText);
     float nameWidth = font.getWidth(nameText);
     float playTimeWidth = font.getWidth(playTimeText);
     
     float maxTextWidth = Math.max(titleWidth, Math.max(serverWidth, Math.max(nameWidth, playTimeWidth)));
     float width = maxTextWidth + 10.0F;
     
     int time = (int)((float)(now % 2000L) / 2000.0F * 360.0F);
     
     int leftTop = ColorUtils.getThemeColor(time);
     int leftBottom = ColorUtils.getThemeColor(time + 30);
     int centerTop = ColorUtils.getThemeColor(time + 90);
     int centerBottom = ColorUtils.getThemeColor(time + 120);
     int rightTop = ColorUtils.getThemeColor(time + 180);
     int rightBottom = ColorUtils.getThemeColor(time + 210);
     
     RenderUtils.drawWaveHudPanel(eventRender.getContext().method_51448(), x, y, width, height + 25.0F, ColorUtils.rgba(25, 25, 25, 150), height - 3.0F, 0.0F, 10.0F, 10.0F, leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);
 
 
     
     font.drawStringWithShadow(eventRender.getContext().method_51448(), titleText, x + 3.0F, y + 5.0F, -1);
     font.drawStringWithShadow(eventRender.getContext().method_51448(), serverText, x + 3.0F, y + 18.0F, -1);
     font.drawStringWithShadow(eventRender.getContext().method_51448(), nameText, x + 3.0F, y + 25.5F, -1);
     font.drawStringWithShadow(eventRender.getContext().method_51448(), playTimeText, x + 3.0F, y + 33.5F, -1);
     
     this.draggable.setHeight(height + 25.0F);
     this.draggable.setWidth(width);
     super.onRender(eventRender);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\base\implement\Session.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */