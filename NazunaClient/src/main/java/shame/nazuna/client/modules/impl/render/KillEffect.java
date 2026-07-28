package shame.nazuna.client.modules.impl.render;
 import com.mojang.blaze3d.platform.GlStateManager;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.Map;
 import net.minecraft.Entity;
 import net.minecraft.Vec3d;
 import net.minecraft.BufferRenderer;
 import net.minecraft.BufferBuilder;
 import net.minecraft.Tessellator;
 import net.minecraft.VertexFormat;
 import net.minecraft.Identifier;
 import net.minecraft.MathHelper;
 import net.minecraft.MatrixStack;
 import net.minecraft.RotationAxis;
 import org.joml.Matrix4f;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.api.events.implement.EventAttackEntity;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.client.modules.Module;
 
 public class KillEffect extends Module {
   public static KillEffect INSTANCE = new KillEffect();
   private static final Identifier GLOW_TEX = Identifier.method_60655("astra", "textures/particle/bloom.png");
   private static final float DURATION = 1.5F;
   private static final float HEIGHT = 4.0F;
   private static final float MAX_RADIUS = 1.0F;
   private static final int SLICES = 40;
   private final Map<Entity, Vec3d> trackedEntities = new IdentityHashMap<>();
   private final List<ActiveEffect> effects = new ArrayList<>();
   
   public KillEffect() {
     super("KillEffect", "Эффект при исчезновении цели", Module.ModuleCategory.RENDER);
   }
 
   
   public void onDisable() {
     this.trackedEntities.clear();
     this.effects.clear();
     super.onDisable();
   }
   
   @EventLink
   public void onAttack(EventAttackEntity event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return;  Entity target = event.getTarget();
     if (target instanceof net.minecraft.LivingEntity && target != mc.field_1724) {
       this.trackedEntities.put(target, target.method_19538());
     }
   }
   
   @EventLink
   public void onRender3D(Event3DRender event) {
     if (mc.field_1687 == null || mc.field_1724 == null)
       return;  long currentTime = System.currentTimeMillis();
     Iterator<Map.Entry<Entity, Vec3d>> trackIterator = this.trackedEntities.entrySet().iterator();
     while (trackIterator.hasNext()) {
       Map.Entry<Entity, Vec3d> entry = trackIterator.next();
       Entity entity = entry.getKey();
       if (entity.method_31481() || !entity.method_5805()) {
         this.effects.add(new ActiveEffect(entry.getValue(), currentTime));
         trackIterator.remove(); continue;
       } 
       entry.setValue(entity.method_19538());
     } 
     
     RenderSystem.enableBlend();
     RenderSystem.disableDepthTest();
     RenderSystem.depthMask(false);
     RenderSystem.disableCull();
     RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE, GlStateManager.class_4535.ZERO, GlStateManager.class_4534.ONE);
     RenderSystem.setShader(ShaderProgramKeys.field_53880);
     RenderSystem.setShaderTexture(0, GLOW_TEX);
     Iterator<ActiveEffect> effectIterator = this.effects.iterator();
     while (effectIterator.hasNext()) {
       ActiveEffect effect = effectIterator.next();
       float progress = (float)(currentTime - effect.startTime) / 1500.0F;
       if (progress >= 1.0F) {
         effectIterator.remove();
         continue;
       } 
       renderEffect(event.getMatrices(), effect, mc.field_1773.method_19418().method_19326(), progress);
     } 
     RenderSystem.enableCull();
     RenderSystem.depthMask(true);
     RenderSystem.enableDepthTest();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableBlend();
   }
   
   private void renderEffect(MatrixStack matrices, ActiveEffect effect, Vec3d cameraPos, float progress) {
     int color = ColorUtils.getThemeColor();
     float r = (color >> 16 & 0xFF) / 255.0F;
     float g = (color >> 8 & 0xFF) / 255.0F;
     float b = (color & 0xFF) / 255.0F;
     float globalAlpha = (progress < 0.15F) ? (progress / 0.15F) : ((progress > 0.75F) ? ((1.0F - progress) / 0.25F) : 1.0F);
     float sliceHeight = 0.1F; int i;
     for (i = 0; i < 40; i++) {
       float t = i / 40.0F;
       float y = t * 4.0F;
       float radius = 1.0F * MathHelper.method_15374((float)(Math.PI * t));
       float sliceAlpha = (1.0F - Math.abs(2.0F * t - 1.0F) * 0.25F) * globalAlpha;
       Vec3d pos = effect.position.method_1031(0.0D, y, 0.0D);
       renderGlow(matrices, cameraPos, pos, radius * 2.1F, r, g, b, sliceAlpha * 0.22F);
       renderGlow(matrices, cameraPos, pos, radius * 1.15F, r, g, b, sliceAlpha * 0.48F);
       renderGlow(matrices, cameraPos, pos, radius * 0.55F, r, g, b, sliceAlpha * 0.85F);
     } 
     for (i = 0; i < 10; i++) {
       float t = i / 10.0F;
       float spread = 1.0F - t;
       float bottomRadius = 3.6F * spread;
       float bottomAlpha = spread * spread * globalAlpha * 0.38F;
       Vec3d bPos = effect.position.method_1031(0.0D, (t * 0.45F), 0.0D);
       renderGlow(matrices, cameraPos, bPos, bottomRadius, r, g, b, bottomAlpha);
       renderGlow(matrices, cameraPos, bPos, bottomRadius * 0.35F, r, g, b, bottomAlpha * 1.7F);
     } 
   }
   
   private void renderGlow(MatrixStack matrices, Vec3d cameraPos, Vec3d position, float size, float r, float g, float b, float a) {
     if (a <= 0.01F)
       return;  matrices.method_22903();
     matrices.method_22904(position.field_1352 - cameraPos.field_1352, position.field_1351 - cameraPos.field_1351, position.field_1350 - cameraPos.field_1350);
     matrices.method_22907(RotationAxis.field_40716.rotationDegrees(-mc.field_1773.method_19418().method_19330()));
     matrices.method_22907(RotationAxis.field_40714.rotationDegrees(mc.field_1773.method_19418().method_19329()));
     Matrix4f matrix = matrices.method_23760().method_23761();
     float half = size * 0.5F;
     int rInt = Math.min(255, (int)(r * 255.0F));
     int gInt = Math.min(255, (int)(g * 255.0F));
     int bInt = Math.min(255, (int)(b * 255.0F));
     int aInt = Math.min(255, (int)(a * 255.0F));
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     buffer.method_22918(matrix, -half, -half, 0.0F).method_22913(0.0F, 1.0F).method_1336(rInt, gInt, bInt, aInt);
     buffer.method_22918(matrix, -half, half, 0.0F).method_22913(0.0F, 0.0F).method_1336(rInt, gInt, bInt, aInt);
     buffer.method_22918(matrix, half, half, 0.0F).method_22913(1.0F, 0.0F).method_1336(rInt, gInt, bInt, aInt);
     buffer.method_22918(matrix, half, -half, 0.0F).method_22913(1.0F, 1.0F).method_1336(rInt, gInt, bInt, aInt);
     BufferRenderer.method_43433(buffer.method_60800());
     matrices.method_22909();
   }
   
   private static class ActiveEffect { final Vec3d position;
     final long startTime;
     
     ActiveEffect(Vec3d position, long startTime) {
       this.position = position;
       this.startTime = startTime;
     } }
 
 }

