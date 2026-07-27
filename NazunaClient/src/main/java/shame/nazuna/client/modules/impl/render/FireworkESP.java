package shame.nazuna.client.modules.impl.render;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import net.minecraft.Entity;
 import net.minecraft.ItemStack;
 import net.minecraft.Items;
 import net.minecraft.ItemConvertible;
 import net.minecraft.Vec3d;
 import net.minecraft.MathHelper;
 import net.minecraft.MatrixStack;
 import org.joml.Matrix4f;
 import org.joml.Matrix4fc;
 import org.joml.Quaternionf;
 import org.joml.Quaternionfc;
 import org.joml.Vector3f;
 import org.joml.Vector4f;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.utils.render.RenderUtils;
 import shame.nazuna.api.utils.render.fonts.msdf.Font;
 import shame.nazuna.api.utils.render.fonts.msdf.Fonts;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class FireworkESP extends Module {
   public static FireworkESP INSTANCE = new FireworkESP();
   
   private final FloatSetting interval = new FloatSetting("Интервал (мс)", 100.0F, 10.0F, 1000.0F, 10.0F);
   private final FloatSetting lifetime = new FloatSetting("Время жизни (мс)", 1000.0F, 100.0F, 5000.0F, 100.0F);
   
   private final Matrix4f lastProjectionMatrix = new Matrix4f();
   private final Quaternionf lastCameraRotation = new Quaternionf();
   private Vec3d lastCameraPos = Vec3d.field_1353;
   private float lastTickDelta;
   private final Map<Integer, FireworkData> fireworks = new HashMap<>();
   
   public FireworkESP() {
     super("FireworkESP", "Показывает теги и трейлы фейерверков", Module.ModuleCategory.RENDER);
     addSettings(new Setting[] { (Setting)this.interval, (Setting)this.lifetime });
   }
 
   
   public void onDisable() {
     super.onDisable();
     this.fireworks.clear();
   }
   
   @EventLink
   public void onRender3D(Event3DRender event) {
     this.lastProjectionMatrix.set((Matrix4fc)event.getProjectionMatrix());
     this.lastCameraPos = event.getCamera().method_19326();
     this.lastCameraRotation.set((Quaternionfc)event.getCamera().method_23767());
     this.lastTickDelta = event.getTickDelta();
     
     if (mc.field_1687 == null)
       return; 
     long currentTime = System.currentTimeMillis();
     
     this.fireworks.entrySet().removeIf(entry -> {
           Entity entity = mc.field_1687.method_8469(((Integer)entry.getKey()).intValue());
           boolean isDead = (entity == null || !entity.method_5805());
           ((FireworkData)entry.getValue()).points.removeIf(());
           return (isDead && ((FireworkData)entry.getValue()).points.isEmpty());
         });
     
     for (Entity entity : mc.field_1687.method_18112()) {
       if (entity instanceof net.minecraft.FireworkRocketEntity && entity.method_5805()) {
         FireworkData data = this.fireworks.computeIfAbsent(Integer.valueOf(entity.method_5628()), k -> new FireworkData());
         
         if ((float)(currentTime - data.lastSpawnTime) >= this.interval.get()) {
 
 
           
           Vec3d pos = new Vec3d(MathHelper.method_16436(this.lastTickDelta, entity.field_6038, entity.method_23317()), MathHelper.method_16436(this.lastTickDelta, entity.field_5971, entity.method_23318()) + 0.5D, MathHelper.method_16436(this.lastTickDelta, entity.field_5989, entity.method_23321()));
           
           float ageInSeconds = entity.field_6012 / 20.0F;
           data.points.add(new TrailPoint(pos, currentTime, ageInSeconds));
           data.lastSpawnTime = currentTime;
         } 
       } 
     } 
   }
   
   @EventLink
   public void onRender2D(EventRender.Default event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     MatrixStack matrices = event.getContext().method_51448();
     ItemStack icon = new ItemStack((ItemConvertible)Items.field_8639);
     Font font = Fonts.getFont("sf_regular", 14);
     long currentTime = System.currentTimeMillis();
     
     for (Map.Entry<Integer, FireworkData> entry : this.fireworks.entrySet()) {
       FireworkData data = entry.getValue();
       
       for (TrailPoint p : data.points) {
         Vec3d screen = worldToScreen(p.pos);
         if (screen == null)
           continue; 
         float progress = 1.0F - (float)(currentTime - p.timestamp) / this.lifetime.get();
         progress = MathHelper.method_15363(progress, 0.0F, 1.0F);
         String text = String.format("%.1fs", new Object[] { Float.valueOf(p.ageSec) });
         
         renderIconRect(event, matrices, font, icon, screen, progress, text);
       } 
       
       Entity entity = mc.field_1687.method_8469(((Integer)entry.getKey()).intValue());
       if (entity instanceof net.minecraft.FireworkRocketEntity && entity.method_5805()) {
 
 
         
         Vec3d currentPos = new Vec3d(MathHelper.method_16436(this.lastTickDelta, entity.field_6038, entity.method_23317()), MathHelper.method_16436(this.lastTickDelta, entity.field_5971, entity.method_23318()) + 0.5D, MathHelper.method_16436(this.lastTickDelta, entity.field_5989, entity.method_23321()));
         
         Vec3d screen = worldToScreen(currentPos);
         if (screen != null) {
           String text = String.format("%.1fs", new Object[] { Float.valueOf(entity.field_6012 / 20.0F) });
           renderIconRect(event, matrices, font, icon, screen, 1.0F, text);
         } 
       } 
     } 
   }
   
   private void renderIconRect(EventRender.Default event, MatrixStack matrices, Font font, ItemStack icon, Vec3d screen, float progress, String text) {
     float iconScale = 0.6F;
     float rectHeight = 12.0F;
     float padding = 2.5F;
     float gap = 2.0F;
     float textYOffset = 3.5F;
     
     float animScale = 0.35F + 0.65F * progress;
     int alpha = (int)(200.0F * progress);
     if (alpha <= 5)
       return; 
     int bgColor = alpha << 24 | 0xA0A0A;
     int textColor = alpha << 24 | 0xFFFFFF;
     
     float textWidth = (font != null) ? font.getStringWidth(text) : 0.0F;
     float iconWidth = 16.0F * iconScale;
     float totalWidth = padding + iconWidth + gap + textWidth + padding;
     
     matrices.method_22903();
     matrices.method_22904(screen.field_1352, screen.field_1351, 0.0D);
     matrices.method_22905(animScale, animScale, 1.0F);
     
     RenderUtils.drawRoundedRect(matrices, -totalWidth / 2.0F, -rectHeight / 2.0F, totalWidth, rectHeight, 0.0F, bgColor);
     
     float currentX = -totalWidth / 2.0F + padding;
     
     matrices.method_22903();
     matrices.method_46416(currentX, -(16.0F * iconScale) / 2.0F, 0.0F);
     matrices.method_22905(iconScale, iconScale, 1.0F);
     event.getContext().method_51427(icon, 0, 0);
     matrices.method_22909();
     
     currentX += iconWidth + gap;
     
     if (font != null) {
       font.drawString(matrices, text, currentX, -rectHeight / 2.0F + textYOffset + 0.5F, textColor);
     }
     
     matrices.method_22909();
   }
   
   private Vec3d worldToScreen(Vec3d worldPos) {
     Vector3f relative = new Vector3f((float)(worldPos.field_1352 - this.lastCameraPos.field_1352), (float)(worldPos.field_1351 - this.lastCameraPos.field_1351), (float)(worldPos.field_1350 - this.lastCameraPos.field_1350));
 
 
 
 
     
     relative.rotate((Quaternionfc)(new Quaternionf((Quaternionfc)this.lastCameraRotation)).conjugate());
     
     Vector4f clip = new Vector4f(relative.x, relative.y, relative.z, 1.0F);
     this.lastProjectionMatrix.transform(clip);
     
     float w = clip.w;
     if (w <= 1.0E-5F) return null;
     
     float ndcX = clip.x / w;
     float ndcY = clip.y / w;
     float ndcZ = clip.z / w;
     
     float screenX = (ndcX * 0.5F + 0.5F) * mc.method_22683().method_4486();
     float screenY = (1.0F - ndcY * 0.5F + 0.5F) * mc.method_22683().method_4502();
     
     if (Float.isNaN(screenX) || Float.isNaN(screenY) || Float.isInfinite(screenX) || Float.isInfinite(screenY)) {
       return null;
     }
     
     return new Vec3d(screenX, screenY, ndcZ);
   }
   
   private static class FireworkData {
     long lastSpawnTime;
     final List<FireworkESP.TrailPoint> points = new ArrayList<>();
   }
   
   private static class TrailPoint {
     final Vec3d pos;
     final long timestamp;
     final float ageSec;
     
     TrailPoint(Vec3d pos, long timestamp, float ageSec) {
       this.pos = pos;
       this.timestamp = timestamp;
       this.ageSec = ageSec;
     }
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\FireworkESP.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */