package shame.nazuna.client.modules.impl.render;
 import com.mojang.blaze3d.platform.GlStateManager;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.util.ArrayList;
 import java.util.Random;
 import net.minecraft.ShaderProgramKeys;
 import net.minecraft.Vec3d;
 import net.minecraft.BufferRenderer;
 import net.minecraft.BufferBuilder;
 import net.minecraft.Tessellator;
 import net.minecraft.VertexFormats;
 import net.minecraft.VertexFormat;
 import net.minecraft.Identifier;
 import net.minecraft.MathHelper;
 import net.minecraft.Camera;
 import net.minecraft.MatrixStack;
 import net.minecraft.RotationAxis;
 import org.joml.Matrix4f;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class Cubes extends Module {
   public static Cubes INSTANCE = new Cubes();
   
   private static final Identifier GLOW_TEX = Identifier.method_60655("astra", "textures/particle/bloom.png");
   
   private static final float SPAWN_RADIUS = 12.0F;
   private static final float PARTICLE_SIZE = 0.18F;
   private static final float PARTICLE_SPEED = 0.25F;
   private static final float GLOW_INTENSITY = 1.7F;
   private static final float MAX_RENDER_DISTANCE_SQ = 900.0F;
   private static final byte[][] CUBE_EDGES = new byte[][] { { -1, -1, -1, 1, -1, -1 }, { 1, -1, -1, 1, -1, 1 }, { 1, -1, 1, -1, -1, 1 }, { -1, -1, 1, -1, -1, -1 }, { -1, 1, -1, 1, 1, -1 }, { 1, 1, -1, 1, 1, 1 }, { 1, 1, 1, -1, 1, 1 }, { -1, 1, 1, -1, 1, -1 }, { -1, -1, -1, -1, 1, -1 }, { 1, -1, -1, 1, 1, -1 }, { 1, -1, 1, 1, 1, 1 }, { -1, -1, 1, -1, 1, 1 } };
 
 
 
   
   private static final byte[][] TRIANGLE_EDGES = new byte[][] { { 0, 1 }, { 0, 2 }, { 0, 3 }, { 0, 4 }, { 1, 2 }, { 2, 3 }, { 3, 4 }, { 4, 1 } };
 
 
   
   private static final float[] GLOW_SCALES = new float[] { 10.0F, 6.0F, 3.5F };
   private static final float[] GLOW_ALPHA_SCALES = new float[] { 0.06F, 0.14F, 0.25F };
   
   private final ModeSetting animation = new ModeSetting("Анимация", "Разлет", new String[] { "Разлет", "Падение" });
   private final ModeSetting shape = new ModeSetting("Форма", "Кубы", new String[] { "Кубы", "Треугольники" });
   private final FloatSetting count = new FloatSetting("Количество", 30.0F, 5.0F, 100.0F, 1.0F);
   private final FloatSetting size = new FloatSetting("Размер", 1.0F, 0.1F, 3.0F, 0.1F);
   private final FloatSetting speed = new FloatSetting("Скорость", 1.0F, 0.1F, 5.0F, 0.1F);
   
   private final List<CubeParticle> cubes = new ArrayList<>();
   private final List<CubeParticle> visibleCubes = new ArrayList<>();
   private final Random random = new Random(); private boolean lastAttackPressed; private float cr;
   private float cg;
   private float cb;
   private int updateCounter = 0;
   
   public Cubes() {
     super("Cubes", "3D Кубы по миру", Module.ModuleCategory.RENDER);
     addSettings(new Setting[] { (Setting)this.animation, (Setting)this.shape, (Setting)this.count, (Setting)this.size, (Setting)this.speed });
   }
 
   
   public void onEnable() {
     super.onEnable();
     this.cubes.clear();
   }
 
   
   public void onDisable() {
     super.onDisable();
     this.cubes.clear();
   }
   
   @EventLink
   public void onRender3D(Event3DRender event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     boolean attackPressed = mc.field_1690.field_1886.method_1434();
     if (attackPressed && !this.lastAttackPressed) applyHitImpulseFromCrosshair(event.getCamera()); 
     this.lastAttackPressed = attackPressed;
     
     this.updateCounter++;
     if (this.updateCounter % 2 == 0) updateCubes();
     
     renderCubes(event);
   }
   
   @EventLink
   public void onAttack(EventAttackEntity event) {
     if (mc.field_1773 != null && mc.field_1773.method_19418() != null) {
       applyHitImpulseFromCrosshair(mc.field_1773.method_19418());
     }
   }
   
   private void applyHitImpulseFromCrosshair(Camera camera) {
     if (this.cubes.isEmpty() || camera == null)
       return; 
     Vec3d origin = camera.method_19326();
     float yaw = (float)Math.toRadians(camera.method_19330());
     float pitch = (float)Math.toRadians(camera.method_19329());
     
     double dirX = (-MathHelper.method_15374(yaw) * MathHelper.method_15362(pitch));
     double dirY = -MathHelper.method_15374(pitch);
     double dirZ = (MathHelper.method_15362(yaw) * MathHelper.method_15362(pitch));
     
     CubeParticle best = null;
     double bestT = Double.MAX_VALUE;
     
     for (int i = 0, sz = this.cubes.size(); i < sz; i++) {
       CubeParticle p = this.cubes.get(i);
       double opX = p.x - origin.field_1352;
       double opY = p.y - origin.field_1351;
       double opZ = p.z - origin.field_1350;
       double t = opX * dirX + opY * dirY + opZ * dirZ;
       
       if (t >= 0.0D && t <= 128.0D) {
         
         double closestX = origin.field_1352 + dirX * t;
         double closestY = origin.field_1351 + dirY * t;
         double closestZ = origin.field_1350 + dirZ * t;
         double dx = p.x - closestX;
         double dy = p.y - closestY;
         double dz = p.z - closestZ;
         double distSq = dx * dx + dy * dy + dz * dz;
         
         if (distSq <= 1.32D && t < bestT) {
           bestT = t;
           best = p;
         } 
       } 
     }  if (best != null) {
       double force = 0.08D * this.speed.get();
       best.vx = (float)(best.vx + dirX * force);
       best.vy = (float)(best.vy + dirY * force + 0.02D);
       best.vz = (float)(best.vz + dirZ * force);
     } 
   }
   
   private void updateCubes() {
     int target = (int)this.count.get();
     int currentSize = this.cubes.size();
     
     if (currentSize < target) {
       int toAdd = Math.min(target - currentSize, 5);
       for (int j = 0; j < toAdd; ) { this.cubes.add(spawnCube()); j++; } 
     } else if (currentSize > target) {
       this.cubes.subList(target, currentSize).clear();
     } 
     
     float spd = 0.25F * this.speed.get();
     float maxR = 12.0F;
     boolean falling = this.animation.is("Падение");
     Vec3d playerPos = mc.field_1724.method_19538();
     double maxRSq = (maxR * maxR) * 6.25D;
     
     for (int i = this.cubes.size() - 1; i >= 0; i--) {
       CubeParticle p = this.cubes.get(i);
       
       if (falling) {
         p.wobblePhase += 0.06F * spd;
         p.x += (p.vx * spd) + Math.sin((p.wobblePhase + p.wobbleOffset)) * 0.002400000113993883D * spd;
         p.y += (p.vy * spd);
         p.z += (p.vz * spd) + Math.cos((p.wobblePhase * 0.8F + p.wobbleOffset)) * 0.0020000000949949026D * spd;
         p.vy = Math.max(p.vy - 8.0E-5F * spd, -0.032F);
         p.rotX += p.rotSpeedX * 0.2F * spd;
         p.rotY += p.rotSpeedY * 0.2F * spd;
         p.rotZ += p.rotSpeedZ * 0.2F * spd;
       } else {
         p.x += (p.vx * spd);
         p.y += (p.vy * spd);
         p.z += (p.vz * spd);
         p.rotX += p.rotSpeedX * spd;
         p.rotY += p.rotSpeedY * spd;
         p.rotZ += p.rotSpeedZ * spd;
         p.vx *= 0.995F;
         p.vy *= 0.995F;
         p.vz *= 0.995F;
       } 
       
       p.life--;
       
       double dx = p.x - playerPos.field_1352;
       double dy = p.y - playerPos.field_1351;
       double dz = p.z - playerPos.field_1350;
       double distSq = dx * dx + dy * dy + dz * dz;
       
       if (p.life <= 0 || distSq > maxRSq || (falling && p.y < playerPos.field_1351 - 2.5D)) {
         this.cubes.remove(i);
         this.cubes.add(spawnCube());
       } 
     } 
   }
   
   private void renderCubes(Event3DRender e) {
     if (mc.field_1724 == null)
       return; 
     MatrixStack ms = e.getMatrices();
     Vec3d cam = e.getCamera().method_19326();
     Camera camera = e.getCamera();
     float s = 0.18F * this.size.get();
     float glow = 1.7F;
     
     int baseRGB = ColorUtils.getThemeColor();
     this.cr = (baseRGB >> 16 & 0xFF) / 255.0F;
     this.cg = (baseRGB >> 8 & 0xFF) / 255.0F;
     this.cb = (baseRGB & 0xFF) / 255.0F;
     
     this.visibleCubes.clear();
     float yaw = (float)Math.toRadians(camera.method_19330());
     float pitch = (float)Math.toRadians(camera.method_19329());
     double lookX = (-MathHelper.method_15374(yaw) * MathHelper.method_15362(pitch));
     double lookY = -MathHelper.method_15374(pitch);
     double lookZ = (MathHelper.method_15362(yaw) * MathHelper.method_15362(pitch));
     
     for (int i = 0, sz = this.cubes.size(); i < sz; i++) {
       CubeParticle p = this.cubes.get(i);
       double dx = p.x - cam.field_1352;
       double dy = p.y - cam.field_1351;
       double dz = p.z - cam.field_1350;
       double distSq = dx * dx + dy * dy + dz * dz;
       
       if (distSq <= 900.0D && 
         dx * lookX + dy * lookY + dz * lookZ >= -1.0D) {
         p.renderAlpha = getAlpha(p);
         if (p.renderAlpha >= 0.01F)
         {
           this.visibleCubes.add(p); } 
       } 
     } 
     if (this.visibleCubes.isEmpty())
       return; 
     RenderSystem.enableBlend();
     RenderSystem.disableCull();
     RenderSystem.disableDepthTest();
     RenderSystem.depthMask(false);
     RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE, GlStateManager.class_4535.ZERO, GlStateManager.class_4534.ONE);
     RenderSystem.setShader(ShaderProgramKeys.field_53880);
     RenderSystem.setShaderTexture(0, GLOW_TEX);
     drawGlowBatch(ms, camera, cam, s, glow);
     
     RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA);
     RenderSystem.setShader(ShaderProgramKeys.field_53876);
     
     boolean isCubes = this.shape.is("Кубы");
     boolean isTriangles = this.shape.is("Треугольники");
     
     if (isCubes) {
       drawCubeFacesBatch(ms, cam, s);
     }
     
     if (isTriangles) {
       drawTriangleFacesBatch(ms, cam, s);
     }
     
     RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE);
     
     if (isCubes) {
       drawCubeDashedEdgesBatch(ms, cam, s);
     } else if (isTriangles) {
       drawTriangleDashedEdgesBatch(ms, cam, s);
     } 
     
     RenderSystem.depthMask(true);
     RenderSystem.enableCull();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableBlend();
   }
   
   private void drawGlowBatch(MatrixStack ms, Camera camera, Vec3d cam, float s, float glow) {
     BufferBuilder builder = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     
     for (int particleIndex = 0, sz = this.visibleCubes.size(); particleIndex < sz; particleIndex++) {
       CubeParticle p = this.visibleCubes.get(particleIndex);
       float alpha = p.renderAlpha;
       
       ms.method_22903();
       ms.method_22904(p.x - cam.field_1352, p.y - cam.field_1351, p.z - cam.field_1350);
       ms.method_22907(RotationAxis.field_40716.rotationDegrees(-camera.method_19330()));
       ms.method_22907(RotationAxis.field_40714.rotationDegrees(camera.method_19329()));
       
       Matrix4f matrix = ms.method_23760().method_23761();
       for (int i = 0; i < 3; i++) {
         float scale = s * GLOW_SCALES[i] * glow;
         float a = alpha * GLOW_ALPHA_SCALES[i] * glow;
         float hs = scale * 0.5F;
         
         builder.method_22918(matrix, -hs, hs, 0.0F).method_22913(0.0F, 1.0F).method_22915(this.cr, this.cg, this.cb, a);
         builder.method_22918(matrix, hs, hs, 0.0F).method_22913(1.0F, 1.0F).method_22915(this.cr, this.cg, this.cb, a);
         builder.method_22918(matrix, hs, -hs, 0.0F).method_22913(1.0F, 0.0F).method_22915(this.cr, this.cg, this.cb, a);
         builder.method_22918(matrix, -hs, -hs, 0.0F).method_22913(0.0F, 0.0F).method_22915(this.cr, this.cg, this.cb, a);
       } 
       ms.method_22909();
     } 
     
     BufferRenderer.method_43433(builder.method_60800());
   }
   
   private float getAlpha(CubeParticle p) {
     float lifePct = MathHelper.method_15363(p.life / p.maxLife, 0.0F, 1.0F);
     float fadeIn = Math.min(1.0F, (p.maxLife - p.life) / 20.0F);
     return lifePct * fadeIn;
   }
   
   private void drawCubeFacesBatch(MatrixStack ms, Vec3d cam, float s) {
     if (!hasFaceRenderableParticles())
       return;  BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1576);
     for (int i = 0, sz = this.visibleCubes.size(); i < sz; i++) {
       CubeParticle p = this.visibleCubes.get(i);
       float alpha = p.renderAlpha * 0.4F;
       if (alpha >= 0.01F) {
         
         ms.method_22903();
         ms.method_22904(p.x - cam.field_1352, p.y - cam.field_1351, p.z - cam.field_1350);
         ms.method_22907(RotationAxis.field_40714.rotationDegrees(p.rotX));
         ms.method_22907(RotationAxis.field_40716.rotationDegrees(p.rotY));
         ms.method_22907(RotationAxis.field_40718.rotationDegrees(p.rotZ));
         appendCubeFaces(buffer, ms.method_23760().method_23761(), s, alpha);
         ms.method_22909();
       } 
     }  BufferRenderer.method_43433(buffer.method_60800());
   }
   
   private void drawTriangleFacesBatch(MatrixStack ms, Vec3d cam, float s) {
     if (!hasFaceRenderableParticles())
       return;  BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27379, VertexFormats.field_1576);
     for (int i = 0, sz = this.visibleCubes.size(); i < sz; i++) {
       CubeParticle p = this.visibleCubes.get(i);
       float alpha = p.renderAlpha * 0.4F;
       if (alpha >= 0.01F) {
         
         ms.method_22903();
         ms.method_22904(p.x - cam.field_1352, p.y - cam.field_1351, p.z - cam.field_1350);
         ms.method_22907(RotationAxis.field_40714.rotationDegrees(p.rotX));
         ms.method_22907(RotationAxis.field_40716.rotationDegrees(p.rotY));
         ms.method_22907(RotationAxis.field_40718.rotationDegrees(p.rotZ));
         appendTriangleFaces(buffer, ms.method_23760().method_23761(), s, alpha);
         ms.method_22909();
       } 
     }  BufferRenderer.method_43433(buffer.method_60800());
   }
   
   private boolean hasFaceRenderableParticles() {
     for (int i = 0, sz = this.visibleCubes.size(); i < sz; i++) {
       if (((CubeParticle)this.visibleCubes.get(i)).renderAlpha >= 0.025F) return true; 
     } 
     return false;
   }
   
   private void appendCubeFaces(BufferBuilder buffer, Matrix4f m, float s, float a) {
     buffer.method_22918(m, -s, -s, s).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, s, -s, s).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, s, s, s).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, -s, s, s).method_22915(this.cr, this.cg, this.cb, a);
     
     buffer.method_22918(m, s, -s, -s).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, -s, -s, -s).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, -s, s, -s).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, s, s, -s).method_22915(this.cr, this.cg, this.cb, a);
     
     buffer.method_22918(m, -s, s, s).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, s, s, s).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, s, s, -s).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, -s, s, -s).method_22915(this.cr, this.cg, this.cb, a);
     
     buffer.method_22918(m, -s, -s, -s).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, s, -s, -s).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, s, -s, s).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, -s, -s, s).method_22915(this.cr, this.cg, this.cb, a);
     
     buffer.method_22918(m, s, -s, s).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, s, -s, -s).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, s, s, -s).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, s, s, s).method_22915(this.cr, this.cg, this.cb, a);
     
     buffer.method_22918(m, -s, -s, -s).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, -s, -s, s).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, -s, s, s).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, -s, s, -s).method_22915(this.cr, this.cg, this.cb, a);
   }
   
   private void appendTriangleFaces(BufferBuilder buffer, Matrix4f m, float s, float a) {
     float top = s;
     float bottom = -s;
     float halfBase = s * 0.866F;
     
     buffer.method_22918(m, 0.0F, top, 0.0F).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, -halfBase, bottom, halfBase).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, halfBase, bottom, halfBase).method_22915(this.cr, this.cg, this.cb, a);
     
     buffer.method_22918(m, 0.0F, top, 0.0F).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, halfBase, bottom, halfBase).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, halfBase, bottom, -halfBase).method_22915(this.cr, this.cg, this.cb, a);
     
     buffer.method_22918(m, 0.0F, top, 0.0F).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, halfBase, bottom, -halfBase).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, -halfBase, bottom, -halfBase).method_22915(this.cr, this.cg, this.cb, a);
     
     buffer.method_22918(m, 0.0F, top, 0.0F).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, -halfBase, bottom, -halfBase).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, -halfBase, bottom, halfBase).method_22915(this.cr, this.cg, this.cb, a);
     
     buffer.method_22918(m, -halfBase, bottom, halfBase).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, halfBase, bottom, halfBase).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, halfBase, bottom, -halfBase).method_22915(this.cr, this.cg, this.cb, a);
     
     buffer.method_22918(m, -halfBase, bottom, halfBase).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, halfBase, bottom, -halfBase).method_22915(this.cr, this.cg, this.cb, a);
     buffer.method_22918(m, -halfBase, bottom, -halfBase).method_22915(this.cr, this.cg, this.cb, a);
   }
   
   private void drawCubeDashedEdgesBatch(MatrixStack ms, Vec3d cam, float s) {
     BufferBuilder buf = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_29344, VertexFormats.field_1576);
     int lineCount = 0;
     for (int i = 0, sz = this.visibleCubes.size(); i < sz; i++) {
       CubeParticle p = this.visibleCubes.get(i);
       float alpha = p.renderAlpha;
       
       ms.method_22903();
       ms.method_22904(p.x - cam.field_1352, p.y - cam.field_1351, p.z - cam.field_1350);
       ms.method_22907(RotationAxis.field_40714.rotationDegrees(p.rotX));
       ms.method_22907(RotationAxis.field_40716.rotationDegrees(p.rotY));
       ms.method_22907(RotationAxis.field_40718.rotationDegrees(p.rotZ));
       lineCount += appendCubeDashedEdges(buf, ms.method_23760().method_23761(), s, alpha);
       ms.method_22909();
     } 
     
     if (lineCount > 0) {
       BufferRenderer.method_43433(buf.method_60800());
     }
   }
   
   private void drawTriangleDashedEdgesBatch(MatrixStack ms, Vec3d cam, float s) {
     BufferBuilder buf = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_29344, VertexFormats.field_1576);
     int lineCount = 0;
     for (int i = 0, sz = this.visibleCubes.size(); i < sz; i++) {
       CubeParticle p = this.visibleCubes.get(i);
       float alpha = p.renderAlpha;
       
       ms.method_22903();
       ms.method_22904(p.x - cam.field_1352, p.y - cam.field_1351, p.z - cam.field_1350);
       ms.method_22907(RotationAxis.field_40714.rotationDegrees(p.rotX));
       ms.method_22907(RotationAxis.field_40716.rotationDegrees(p.rotY));
       ms.method_22907(RotationAxis.field_40718.rotationDegrees(p.rotZ));
       lineCount += appendTriangleDashedEdges(buf, ms.method_23760().method_23761(), s, alpha);
       ms.method_22909();
     } 
     
     if (lineCount > 0) {
       BufferRenderer.method_43433(buf.method_60800());
     }
   }
   
   private int appendCubeDashedEdges(BufferBuilder buf, Matrix4f mat, float s, float alpha) {
     int color = colorToInt(Math.min(1.0F, this.cr * 1.5F), Math.min(1.0F, this.cg * 1.5F), Math.min(1.0F, this.cb * 1.5F), alpha);
     
     float dashLen = s * 0.3F;
     float gapLen = s * 0.25F;
     
     int lineCount = 0;
     
     for (byte[] edge : CUBE_EDGES) {
       float x1 = edge[0] * s;
       float y1 = edge[1] * s;
       float z1 = edge[2] * s;
       float x2 = edge[3] * s;
       float y2 = edge[4] * s;
       float z2 = edge[5] * s;
       
       float dx = x2 - x1;
       float dy = y2 - y1;
       float dz = z2 - z1;
       float len = MathHelper.method_15355(dx * dx + dy * dy + dz * dz);
       
       if (len >= 0.001F) {
         
         float nx = dx / len;
         float ny = dy / len;
         float nz = dz / len;
         
         float pos = 0.0F;
         boolean drawing = true;
         
         while (pos < len) {
           float segLen = drawing ? dashLen : gapLen;
           float end = Math.min(pos + segLen, len);
           
           if (drawing) {
             buf.method_22918(mat, x1 + nx * pos, y1 + ny * pos, z1 + nz * pos).method_39415(color);
             buf.method_22918(mat, x1 + nx * end, y1 + ny * end, z1 + nz * end).method_39415(color);
             lineCount++;
           } 
           
           pos = end;
           drawing = !drawing;
         } 
       } 
     } 
     return lineCount;
   }
   
   private int appendTriangleDashedEdges(BufferBuilder buf, Matrix4f mat, float s, float alpha) {
     int color = colorToInt(Math.min(1.0F, this.cr * 1.5F), Math.min(1.0F, this.cg * 1.5F), Math.min(1.0F, this.cb * 1.5F), alpha);
     
     float dashLen = s * 0.3F;
     float gapLen = s * 0.25F;
     
     int lineCount = 0;
     
     float top = s;
     float bottom = -s;
     float halfBase = s * 0.866F;
     
     for (byte[] edge : TRIANGLE_EDGES) {
       float x1 = trianglePointX(edge[0], halfBase);
       float y1 = (edge[0] == 0) ? top : bottom;
       float z1 = trianglePointZ(edge[0], halfBase);
       float x2 = trianglePointX(edge[1], halfBase);
       float y2 = (edge[1] == 0) ? top : bottom;
       float z2 = trianglePointZ(edge[1], halfBase);
       
       float dx = x2 - x1;
       float dy = y2 - y1;
       float dz = z2 - z1;
       float len = MathHelper.method_15355(dx * dx + dy * dy + dz * dz);
       
       if (len >= 0.001F) {
         
         float nx = dx / len;
         float ny = dy / len;
         float nz = dz / len;
         
         float pos = 0.0F;
         boolean drawing = true;
         
         while (pos < len) {
           float segLen = drawing ? dashLen : gapLen;
           float end = Math.min(pos + segLen, len);
           
           if (drawing) {
             buf.method_22918(mat, x1 + nx * pos, y1 + ny * pos, z1 + nz * pos).method_39415(color);
             buf.method_22918(mat, x1 + nx * end, y1 + ny * end, z1 + nz * end).method_39415(color);
             lineCount++;
           } 
           
           pos = end;
           drawing = !drawing;
         } 
       } 
     } 
     return lineCount;
   }
   
   private float trianglePointX(int index, float halfBase) {
     switch (index) { case 1: case 4: case 2: case 3:  }  return 
 
       
       0.0F;
   }
 
   
   private float trianglePointZ(int index, float halfBase) {
     switch (index) { case 1: case 2: case 3: case 4:  }  return 
 
       
       0.0F;
   }
 
   
   private CubeParticle spawnCube() {
     float vx, vy, vz, r = 12.0F;
     boolean falling = this.animation.is("Падение");
     int life = falling ? (260 + this.random.nextInt(220)) : (420 + this.random.nextInt(420));
     
     double x = mc.field_1724.method_23317() + (this.random.nextDouble() * 2.0D - 1.0D) * r;
     double y = falling ? (mc.field_1724.method_23318() + 4.0D + this.random.nextDouble() * r * 0.55D) : (mc.field_1724.method_23318() + 2.0D + this.random.nextDouble() * r * 0.8D);
     double z = mc.field_1724.method_23321() + (this.random.nextDouble() * 2.0D - 1.0D) * r;
     
     float speedMult = this.speed.get();
 
     
     if (falling) {
       vx = (this.random.nextFloat() - 0.5F) * 0.008F * speedMult;
       vy = (-0.012F - this.random.nextFloat() * 0.012F) * speedMult;
       vz = (this.random.nextFloat() - 0.5F) * 0.008F * speedMult;
     } else {
       float yaw = this.random.nextFloat() * 360.0F;
       float vel = (0.01F + this.random.nextFloat() * 0.02F) * speedMult;
       vx = -MathHelper.method_15374((float)Math.toRadians(yaw)) * vel;
       vz = MathHelper.method_15362((float)Math.toRadians(yaw)) * vel;
       vy = (this.random.nextFloat() - 0.5F) * 0.01F * speedMult;
     } 
     
     return new CubeParticle(x, y, z, vx, vy, vz, this.random
         .nextFloat() * 360.0F, this.random.nextFloat() * 360.0F, this.random.nextFloat() * 360.0F, (this.random
         .nextFloat() - 0.5F) * 1.5F, (this.random.nextFloat() - 0.5F) * 1.5F, (this.random.nextFloat() - 0.5F) * 1.5F, life, 
         (float)(this.random.nextDouble() * Math.PI * 2.0D), this.random.nextFloat() * 10.0F);
   }
   
   private static int colorToInt(float r, float g, float b, float a) {
     return (int)(a * 255.0F) << 24 | (int)(r * 255.0F) << 16 | (int)(g * 255.0F) << 8 | (int)(b * 255.0F);
   }
   private static class CubeParticle { double x; double y;
     double z;
     float vx;
     float vy;
     float vz;
     float rotX;
     float rotY;
     
     CubeParticle(double x, double y, double z, float vx, float vy, float vz, float rotX, float rotY, float rotZ, float rotSpeedX, float rotSpeedY, float rotSpeedZ, int life, float wobblePhase, float wobbleOffset) {
       this.x = x; this.y = y; this.z = z; this.vx = vx; this.vy = vy; this.vz = vz;
       this.rotX = rotX; this.rotY = rotY; this.rotZ = rotZ;
       this.rotSpeedX = rotSpeedX; this.rotSpeedY = rotSpeedY; this.rotSpeedZ = rotSpeedZ;
       this.life = this.maxLife = life;
       this.wobblePhase = wobblePhase; this.wobbleOffset = wobbleOffset;
     }
     
     float rotZ;
     float rotSpeedX;
     float rotSpeedY;
     float rotSpeedZ;
     float wobblePhase;
     float wobbleOffset;
     float renderAlpha;
     int life;
     int maxLife; }
 
 }

