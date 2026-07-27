package shame.nazuna.client.modules.impl.render;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.util.ArrayList;
 import java.util.Optional;
 import net.minecraft.Entity;
 import net.minecraft.Vec3d;
 import net.minecraft.BufferRenderer;
 import net.minecraft.BufferBuilder;
 import net.minecraft.Tessellator;
 import net.minecraft.VertexFormats;
 import net.minecraft.VertexFormat;
 import net.minecraft.Identifier;
 import net.minecraft.MatrixStack;
 import net.minecraft.RotationAxis;
 import org.joml.Matrix4f;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.api.events.implement.EventAttackEntity;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class HitMarker extends Module {
   public static HitMarker INSTANCE = new HitMarker();
   
   private final FloatSetting size = new FloatSetting("Размер", 0.5F, 0.1F, 2.0F, 0.05F);
   private final FloatSetting fadeInTime = new FloatSetting("Время появления", 100.0F, 50.0F, 500.0F, 10.0F);
   private final FloatSetting displayTime = new FloatSetting("Время показа", 300.0F, 100.0F, 1000.0F, 50.0F);
   private final FloatSetting fadeOutTime = new FloatSetting("Время исчезновения", 200.0F, 50.0F, 500.0F, 10.0F);
   private final BooleanSetting glow = new BooleanSetting("Свечение", true);
   private final BooleanSetting scale = new BooleanSetting("Анимация масштаба", true);
   
   private final ArrayList<HitMarkerData> hitMarkers = new ArrayList<>();
   
   public HitMarker() {
     super("HitMarker", "Показывает маркер при ударе", Module.ModuleCategory.RENDER);
     addSettings(new Setting[] { (Setting)this.size, (Setting)this.fadeInTime, (Setting)this.displayTime, (Setting)this.fadeOutTime, (Setting)this.glow, (Setting)this.scale });
   }
 
   
   public void onDisable() {
     this.hitMarkers.clear();
     super.onDisable();
   }
   
   private Identifier getTexture() {
     return Identifier.method_60655("astra", "textures/cross/cross.png");
   }
   
   @EventLink
   public void onAttack(EventAttackEntity event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     Entity target = event.getTarget();
     if (target != null) {
       synchronized (this.hitMarkers) {
         this.hitMarkers.add(new HitMarkerData(
               resolveHitPosition((Entity)event.getPlayer(), target), 
               System.currentTimeMillis(), 
               (long)this.fadeInTime.get(), 
               (long)this.displayTime.get(), 
               (long)this.fadeOutTime.get()));
       } 
     }
   }
 
 
 
 
   
   private Vec3d resolveHitPosition(Entity attacker, Entity target) {
     Vec3d fallback = new Vec3d(target.method_23317(), target.method_23318() + target.method_17682() / 2.0D, target.method_23321());
     
     if (attacker == null) return fallback;
     
     Vec3d eyePos = attacker.method_5836(1.0F);
     Vec3d lookVec = attacker.method_5828(1.0F);
     Vec3d targetCenter = target.method_5829().method_1005();
     double distance = Math.max(eyePos.method_1022(targetCenter) + 1.0D, 6.0D);
     Vec3d reachPos = eyePos.method_1019(lookVec.method_1021(distance));
     
     Optional<Vec3d> hitPos = target.method_5829().method_992(eyePos, reachPos);
     if (hitPos.isPresent()) {
       return hitPos.get();
     }
     
     return eyePos.method_1019(lookVec.method_1021(eyePos.method_1022(targetCenter)));
   }
   @EventLink
   public void onRender3D(Event3DRender e) {
     ArrayList<HitMarkerData> renderList;
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     synchronized (this.hitMarkers) {
       this.hitMarkers.removeIf(HitMarkerData::isDead);
     } 
     
     if (this.hitMarkers.isEmpty())
       return; 
     MatrixStack matrices = e.getMatrices();
     Vec3d camera = mc.field_1773.method_19418().method_19326();
     Identifier texture = getTexture();
     
     RenderSystem.enableBlend();
     RenderSystem.disableDepthTest();
     RenderSystem.depthMask(false);
     RenderSystem.disableCull();
     
     if (this.glow.isState()) {
       RenderSystem.blendFunc(770, 1);
     } else {
       RenderSystem.defaultBlendFunc();
     } 
     
     RenderSystem.setShaderTexture(0, texture);
     RenderSystem.setShader(ShaderProgramKeys.field_53880);
 
     
     synchronized (this.hitMarkers) {
       renderList = new ArrayList<>(this.hitMarkers);
     } 
     
     int color = ColorUtils.getThemeColor();
     int r = color >> 16 & 0xFF;
     int g = color >> 8 & 0xFF;
     int b = color & 0xFF;
     
     for (HitMarkerData marker : renderList) {
       float alpha = marker.getAlpha();
       if (alpha <= 0.0F)
         continue; 
       double x = marker.position.field_1352 - camera.field_1352;
       double y = marker.position.field_1351 - camera.field_1351;
       double z = marker.position.field_1350 - camera.field_1350;
       
       matrices.method_22903();
       matrices.method_46416((float)x, (float)y, (float)z);
       matrices.method_22907(RotationAxis.field_40716.rotationDegrees(-mc.field_1773.method_19418().method_19330()));
       matrices.method_22907(RotationAxis.field_40714.rotationDegrees(mc.field_1773.method_19418().method_19329()));
       
       float currentSize = this.size.get();
       if (this.scale.isState()) {
         float scaleMultiplier = marker.getScaleMultiplier();
         currentSize *= scaleMultiplier;
       } 
       
       Matrix4f matrix = matrices.method_23760().method_23761();
       
       float half = currentSize / 2.0F;
       int alphaInt = (int)(alpha * 255.0F);
       
       BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
       
       buffer.method_22918(matrix, -half, -half, 0.0F).method_22913(0.0F, 1.0F).method_1336(r, g, b, alphaInt);
       buffer.method_22918(matrix, -half, half, 0.0F).method_22913(0.0F, 0.0F).method_1336(r, g, b, alphaInt);
       buffer.method_22918(matrix, half, half, 0.0F).method_22913(1.0F, 0.0F).method_1336(r, g, b, alphaInt);
       buffer.method_22918(matrix, half, -half, 0.0F).method_22913(1.0F, 1.0F).method_1336(r, g, b, alphaInt);
       
       BufferRenderer.method_43433(buffer.method_60800());
       
       matrices.method_22909();
     } 
     
     RenderSystem.enableCull();
     RenderSystem.depthMask(true);
     RenderSystem.enableDepthTest();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableBlend();
   }
   
   static class HitMarkerData {
     Vec3d position;
     long birthTime;
     long fadeInTime;
     long displayTime;
     long fadeOutTime;
     
     HitMarkerData(Vec3d position, long birthTime, long fadeInTime, long displayTime, long fadeOutTime) {
       this.position = position;
       this.birthTime = birthTime;
       this.fadeInTime = fadeInTime;
       this.displayTime = displayTime;
       this.fadeOutTime = fadeOutTime;
     }
     
     boolean isDead() {
       return (System.currentTimeMillis() - this.birthTime >= this.fadeInTime + this.displayTime + this.fadeOutTime);
     }
     
     float getAlpha() {
       long elapsed = System.currentTimeMillis() - this.birthTime;
       
       if (elapsed < this.fadeInTime) {
         float f = (float)elapsed / (float)this.fadeInTime;
         return easeOutCubic(f);
       }  if (elapsed < this.fadeInTime + this.displayTime) {
         return 1.0F;
       }
       long fadeOutElapsed = elapsed - this.fadeInTime - this.displayTime;
       float progress = Math.min(1.0F, (float)fadeOutElapsed / (float)this.fadeOutTime);
       return 1.0F - easeInCubic(progress);
     }
 
     
     float getScaleMultiplier() {
       long elapsed = System.currentTimeMillis() - this.birthTime;
       
       if (elapsed < this.fadeInTime) {
         float f = (float)elapsed / (float)this.fadeInTime;
         return 0.5F + 0.5F * easeOutBack(f);
       }  if (elapsed < this.fadeInTime + this.displayTime) {
         return 1.0F;
       }
       long fadeOutElapsed = elapsed - this.fadeInTime - this.displayTime;
       float progress = Math.min(1.0F, (float)fadeOutElapsed / (float)this.fadeOutTime);
       return 1.0F - 0.3F * easeInCubic(progress);
     }
 
     
     private float easeOutCubic(float x) {
       return 1.0F - (float)Math.pow(1.0D - x, 3.0D);
     }
     
     private float easeInCubic(float x) {
       return x * x * x;
     }
     
     private float easeOutBack(float x) {
       float c1 = 1.70158F;
       float c3 = c1 + 1.0F;
       return 1.0F + c3 * (float)Math.pow(x - 1.0D, 3.0D) + c1 * (float)Math.pow(x - 1.0D, 2.0D);
     }
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\HitMarker.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */