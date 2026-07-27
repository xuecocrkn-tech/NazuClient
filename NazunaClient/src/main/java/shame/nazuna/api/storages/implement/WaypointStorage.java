package shame.nazuna.api.storages.implement;
 
 import net.minecraft.class_2960;
 import net.minecraft.class_3532;
 import net.minecraft.class_7833;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.events.EventInvoker;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.utils.animation.AnimationUtils;
 import shame.nazuna.api.utils.animation.Easings;
 import shame.nazuna.api.utils.cmd.waypoint.Waypoint;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.math.MathUtils;
 import shame.nazuna.api.utils.render.RenderUtils;
 import shame.nazuna.api.utils.render.fonts.msdf.Font;
 import shame.nazuna.api.utils.render.fonts.msdf.Fonts;
 
 public class WaypointStorage
   implements QClient {
   private static final class_2960 ARROW_TEXTURE = class_2960.method_60655("astra", "textures/arrows/gps.png");
   
   private final AnimationUtils alphaAnimation = new AnimationUtils(0.0F, 8.5F, Easings.CUBIC_OUT);
   
   private float animatedYaw;
   
   private Waypoint activeWaypoint;
   
   public WaypointStorage() {
 
   
   public void set(Waypoint waypoint) {
     this.activeWaypoint = waypoint;
   }
   
   public void remove(Waypoint waypoint) {
     if (this.activeWaypoint != null && this.activeWaypoint.equals(waypoint)) {
       this.activeWaypoint = null;
     }
   }
   
   public void clear() {
     this.activeWaypoint = null;
   }
   
   public boolean isEmpty() {
     return (this.activeWaypoint == null);
   }
   
   @EventLink
   public void onRender2D(EventRender.Default event) {
     if (mc.field_1724 == null || mc.field_1687 == null) {
       return;
     }
     
     this.alphaAnimation.update((this.activeWaypoint == null) ? 0.0F : 1.0F);
     float alpha = class_3532.method_15363(this.alphaAnimation.getValue(), 0.0F, 1.0F);
     if (this.activeWaypoint == null || alpha <= 0.02F) {
       return;
     }
     
     float centerX = mc.method_22683().method_4486() * 0.5F;
     float centerY = mc.method_22683().method_4502() * 0.25F;
     float size = 40.0F;
     
     double deltaX = this.activeWaypoint.getX() - mc.field_1724.method_23317();
     double deltaZ = this.activeWaypoint.getZ() - mc.field_1724.method_23321();
     int distance = (int)MathUtils.round(class_3532.method_15355((float)(deltaX * deltaX + deltaZ * deltaZ)));
     
     float targetYaw = (float)-Math.toDegrees(Math.atan2(deltaX, deltaZ)) - mc.field_1773.method_19418().method_19330();
     this.animatedYaw = interpolateAngle(this.animatedYaw, targetYaw, 0.18F);
     
     int color = ColorUtils.applyAlpha(ColorUtils.getThemeColor(), alpha);
     
     Font font = Fonts.getFont("sf_regular", 12);
     if (font != null) {
       String distanceText = "" + distance + "m.";
       font.draw(event.getContext().method_51448(), distanceText, centerX - font
           .getWidth(distanceText) * 0.5F + 1.5F, centerY + 7.5F, 
           
           ColorUtils.applyAlpha(-1, alpha));
     } 
     
     event.getContext().method_51448().method_22903();
     event.getContext().method_51448().method_46416(centerX, centerY, 0.0F);
     event.getContext().method_51448().method_22907(class_7833.field_40718.rotationDegrees(this.animatedYaw));
     event.getContext().method_51448().method_46416(-centerX, -centerY, 0.0F);
     
     float drawX = centerX - size * 0.5F;
     float drawY = centerY - size * 0.5F;
     
     RenderUtils.drawImage(event.getContext().method_51448(), ARROW_TEXTURE, drawX, drawY, size, size, color);
     event.getContext().method_51448().method_22909();
   }
   
   private float interpolateAngle(float current, float target, float factor) {
     float delta = class_3532.method_15393(target - current);
     return current + delta * factor;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\storages\implement\WaypointStorage.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */