package shame.nazuna.client.modules.impl.render;
 
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.util.ArrayList;
 import java.util.concurrent.CopyOnWriteArrayList;
 import net.minecraft.ShaderProgramKeys;
 import net.minecraft.LivingEntity;
 import net.minecraft.Vec3d;
 import net.minecraft.BufferRenderer;
 import net.minecraft.BufferBuilder;
 import net.minecraft.Tessellator;
 import net.minecraft.VertexFormats;
 import net.minecraft.VertexFormat;
 import net.minecraft.Identifier;
 import net.minecraft.MathHelper;
 import net.minecraft.MatrixStack;
 import net.minecraft.RotationAxis;
 import org.joml.Matrix4f;
 import org.joml.Quaternionf;
 import org.joml.Vector3f;
 import org.joml.Vector3fc;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.animation.Easings;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.impl.combat.Aura;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 
 public class TargetESP
   extends Module
 {
   public static TargetESP INSTANCE = new TargetESP();
   private static final float GHOST_ALPHA_MULT = 0.6F;
   private static final float CELKA_SPEED_MULT = 1.2F;
   private static final float SCALE_FACTOR = 0.007F;
   static final long CUBE_ATTACH_LIFE_MS = 560L;
   static final long CUBE_FADE_LIFE_MS = 320L;
   static final int MAX_CUBE_PARTICLES = 72;
   static final byte[][] CUBE_EDGES = new byte[][] { { -1, -1, -1, 1, -1, -1 }, { 1, -1, -1, 1, -1, 1 }, { 1, -1, 1, -1, -1, 1 }, { -1, -1, 1, -1, -1, -1 }, { -1, 1, -1, 1, 1, -1 }, { 1, 1, -1, 1, 1, 1 }, { 1, 1, 1, -1, 1, 1 }, { -1, 1, 1, -1, 1, -1 }, { -1, -1, -1, -1, 1, -1 }, { 1, -1, -1, 1, 1, -1 }, { 1, -1, 1, 1, 1, 1 }, { -1, -1, 1, -1, 1, 1 } };
 
 
 
 
   
   private final ModeSetting mode = new ModeSetting("Режим", "Картинка 1", new String[] { "Картинка 1", "Картинка 2", "Кольцо", "Души", "Кубы", "Кристаллы" });
   private final FloatSetting size = new FloatSetting("Размер", 1.15F, 0.6F, 2.5F, 0.05F);
   private final FloatSetting ringRadius = new FloatSetting("Радиус кольца", 0.5F, 0.3F, 1.5F, 0.05F);
   private final FloatSetting ringSpeed = new FloatSetting("Скорость кольца", 1.0F, 0.3F, 3.0F, 0.1F);
   private final FloatSetting rotateSpeed = new FloatSetting("Скорость вращения", 1.2F, 0.2F, 4.0F, 0.05F);
   private final BooleanSetting hurtColor = new BooleanSetting("Окрашивание при ударе", true);
   private final FloatSetting bmwGhostCount = new FloatSetting("Кол-во призраков", 3.0F, 2.0F, 5.0F, 1.0F);
   private final FloatSetting bmwGhostLife = new FloatSetting("Время жизни (мс)", 350.0F, 150.0F, 500.0F, 25.0F);
   private final FloatSetting bmwStrengthXZ = new FloatSetting("Цикл XZ", 2000.0F, 1000.0F, 5000.0F, 100.0F);
   private final FloatSetting bmwStrengthY = new FloatSetting("Цикл Y", 1700.0F, 1000.0F, 5000.0F, 100.0F);
   private float appearValue = 0.0F;
   private float scaleValue = 0.0F;
   private float rotProgress = 0.0F;
   private float rotFrom = -280.0F;
   private float rotTo = 280.0F;
   private long lastRotateUpdate = System.currentTimeMillis();
   private LivingEntity lastTarget = null;
   private LivingEntity lastHandledTarget = null;
   private Vec3d lastTargetPos = null;
   private float lastTargetHeight = 1.8F;
   private float lastTargetWidth = 0.6F;
   private final CopyOnWriteArrayList<GlowPoint> bmwPoints = new CopyOnWriteArrayList<>();
   private float crystalRotationAngle = 0.0F;
   private float crystalAnimation = 0.0F;
   private float spawnAccumulator = 0.0F;
   private long lastCubeTime = 0L;
   private final ArrayList<CubeParticle> cubeParticles = new ArrayList<>();
   private final ArrayList<CubeParticle> renderCubeParticles = new ArrayList<>();
   private static final float SPAWN_INTERVAL = 0.022F;
   private static final int PARTICLES_PER_SPAWN = 1;
   
   public TargetESP() {
     super("TargetESP", "Отображения таргета", Module.ModuleCategory.RENDER);
     this.size.visible(this::isImageMode);
     this.rotateSpeed.visible(this::isImageMode);
     this.bmwGhostCount.visible(() -> Boolean.valueOf(this.mode.is("Райдер")));
     this.bmwGhostLife.visible(() -> Boolean.valueOf(this.mode.is("Райдер")));
     this.bmwStrengthXZ.visible(() -> Boolean.valueOf(this.mode.is("Райдер")));
     this.bmwStrengthY.visible(() -> Boolean.valueOf(this.mode.is("Райдер")));
     this.ringRadius.visible(() -> Boolean.valueOf(this.mode.is("Кольцо")));
     this.ringSpeed.visible(() -> Boolean.valueOf(this.mode.is("Кольцо")));
     addSettings(new Setting[] { (Setting)this.mode, (Setting)this.size, (Setting)this.rotateSpeed, (Setting)this.hurtColor, (Setting)this.ringRadius, (Setting)this.ringSpeed, (Setting)this.bmwGhostCount, (Setting)this.bmwGhostLife, (Setting)this.bmwStrengthXZ, (Setting)this.bmwStrengthY });
   }
 
   
   public void onDisable() {
     this.appearValue = 0.0F;
     this.scaleValue = 0.0F;
     this.lastTarget = null;
     this.lastHandledTarget = null;
     this.lastTargetPos = null;
     this.rotProgress = 0.0F;
     this.rotFrom = -280.0F;
     this.rotTo = 280.0F;
     this.bmwPoints.clear();
     this.crystalRotationAngle = 0.0F;
     this.crystalAnimation = 0.0F;
     this.spawnAccumulator = 0.0F;
     this.lastCubeTime = 0L;
     this.cubeParticles.clear();
     this.renderCubeParticles.clear();
     super.onDisable();
   }
   
   private boolean isImageMode() {
     return (this.mode.is("Картинка 1") || this.mode.is("Картинка 2"));
   }
   
   private Identifier getCaptureTexture() {
     if (this.mode.is("Картинка 2")) {
       return Identifier.method_60655("astra", "textures/targetesp/targetesp_3.png");
     }
     return Identifier.method_60655("astra", "textures/targetesp/targetesp_2.png");
   }
   
   private Identifier getBloomTexture() {
     return Identifier.method_60655("astra", "textures/targetesp/bloom.png");
   }
   
   private int getESPColor() {
     int color = ColorUtils.getThemeColor();
     if ((color >> 24 & 0xFF) == 0) {
       color |= 0xFF000000;
     }
     return color;
   }
   
   private float animateTo(float current, float target, float delta) {
     if (current < target) {
       current = Math.min(current + delta, target);
     } else if (current > target) {
       current = Math.max(current - delta, target);
     } 
     return current;
   }
   
   private float getDistanceScale(Vec3d cameraPos, double worldX, double worldY, double worldZ) {
     double dx = worldX - cameraPos.field_1352;
     double dy = worldY - cameraPos.field_1351;
     double dz = worldZ - cameraPos.field_1350;
     double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
     return (float)Math.max(0.1D, distance * 0.007000000216066837D);
   }
   
   @EventLink(priority = -100)
   public void onRender3D(Event3DRender event) {
     if (mc == null || mc.field_1724 == null || mc.field_1687 == null)
       return; 
     Aura aura = ModuleClass.aura;
     boolean auraEnabled = (aura != null && aura.isEnable());
     LivingEntity target = auraEnabled ? aura.getTarget() : null;
     boolean hasTarget = (target != null && target.method_5805());
     float speed = 0.05F;
     this.appearValue = animateTo(this.appearValue, hasTarget ? 1.0F : 0.0F, speed);
     this.scaleValue = animateTo(this.scaleValue, hasTarget ? 1.0F : 0.5F, speed);
     
     if (hasTarget) {
       this.lastTarget = target;
       this.lastHandledTarget = target;
     } 
     if (this.mode.is("Кристаллы")) {
       float crystalSpeed = hasTarget ? 0.07F : 0.045F;
       this.crystalAnimation = animateTo(this.crystalAnimation, hasTarget ? 1.0F : 0.0F, crystalSpeed);
       if (hasTarget) {
         this.crystalRotationAngle += 0.8F;
       }
     } 
     
     if (this.appearValue <= 0.001F && !hasTarget && (
       !this.mode.is("Кристаллы") || this.crystalAnimation <= 0.001F)) {
       this.lastTarget = null;
       this.lastTargetPos = null;
       
       return;
     } 
     if (hasTarget && target != null) {
       float td = event.getTickDelta();
       this
 
         
         .lastTargetPos = new Vec3d(MathHelper.method_16436(td, target.field_6038, target.method_23317()), MathHelper.method_16436(td, target.field_5971, target.method_23318()), MathHelper.method_16436(td, target.field_5989, target.method_23321()));
       
       this.lastTargetHeight = target.method_17682();
       this.lastTargetWidth = target.method_17681();
     } 
     if (this.lastTargetPos == null)
       return; 
     if (this.mode.is("Райдер")) {
       if (hasTarget && target != null) {
         addBMWGhosts(target, event.getTickDelta(), 
             Math.max(1, Math.round(this.bmwGhostCount.getValue().floatValue())), 
             Math.max(1, Math.round(this.bmwGhostLife.getValue().floatValue())), 
             getESPColor());
       }
       this.bmwPoints.removeIf(GlowPoint::shouldRemove);
       drawBMW3D(event);
       return;
     } 
     if (this.mode.is("Кристаллы")) {
       LivingEntity crystalTarget = hasTarget ? target : this.lastTarget;
       if ((crystalTarget != null || this.lastTargetPos != null) && this.crystalAnimation > 0.01F) {
         renderCrystals3D(event.getMatrices(), crystalTarget, event.getTickDelta());
       }
       return;
     } 
     if (isImageMode()) {
       renderMarker3D(event);
     }
     if (this.mode.is("Души")) {
       drawSouls3D(event);
     }
     if (this.mode.is("Призраки")) {
       drawCelka3D(event);
     }
     if (this.mode.is("Кольцо")) {
       drawRing3D(event);
     }
     if (this.mode.is("Кубы")) {
       renderCubes(event, target, hasTarget);
     }
   }
   
   private void renderCubes(Event3DRender event, LivingEntity target, boolean hasTarget) {
     long now = System.currentTimeMillis();
     if (this.lastCubeTime == 0L) this.lastCubeTime = now; 
     float dt = Math.min((float)(now - this.lastCubeTime) / 1000.0F, 0.1F);
     this.lastCubeTime = now;
     if (!Float.isFinite(dt) || mc.field_1773 == null || mc.field_1773.method_19418() == null) {
       return;
     }
     
     if (hasTarget && target != null) {
       this.lastTarget = target;
       this.spawnAccumulator += dt;
       while (this.spawnAccumulator >= 0.022F) {
         this.spawnAccumulator -= 0.022F;
         if (this.cubeParticles.size() >= 72) {
           break;
         }
         for (int i2 = 0; i2 < 1; i2++) {
           double rand = Math.random() * 360.0D;
           double px = Math.cos(Math.toRadians(rand)) * 0.7D;
           double py = 0.02D + Math.random() * 0.1D;
           double pz = Math.sin(Math.toRadians(rand)) * 0.7D;
           this.cubeParticles.add(new CubeParticle(target, px, py, pz));
         } 
       } 
     } else {
       this.spawnAccumulator = 0.0F;
     } 
     
     this.renderCubeParticles.clear();
     for (int i = this.cubeParticles.size() - 1; i >= 0; i--) {
       CubeParticle particle = this.cubeParticles.get(i);
       try {
         particle.update(dt, now, hasTarget ? target : null);
         if (particle.shouldRemove(now)) {
           this.cubeParticles.remove(i);
         } else {
           this.renderCubeParticles.add(particle);
         } 
       } catch (Throwable ignored) {
         this.cubeParticles.remove(i);
       } 
     } 
     
     if (this.renderCubeParticles.isEmpty()) {
       return;
     }
     
     float partialTicks = event.getTickDelta();
     MatrixStack matrices = event.getMatrices();
     Vec3d camPos = mc.field_1773.method_19418().method_19326();
     LivingEntity colorTarget = hasTarget ? target : this.lastTarget;
     float hurtPC = getHurtPC(colorTarget);
     int baseColor = getESPColor();
     int redColor = ColorUtils.rgb(255, 3, 3);
     
     RenderSystem.enableBlend();
     RenderSystem.enableDepthTest();
     RenderSystem.disableCull();
     RenderSystem.depthMask(false);
     RenderSystem.blendFunc(770, 1);
     RenderSystem.setShader(ShaderProgramKeys.field_53876);
     
     BufferBuilder faceBuilder = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1576);
     boolean hasFaces = false;
     for (int j = 0, size = this.renderCubeParticles.size(); j < size; j++) {
       CubeParticle particle = this.renderCubeParticles.get(j);
       try {
         int particleColor = particle.getRenderColor(baseColor, redColor, hurtPC, now);
         if ((particleColor >> 24 & 0xFF) > 0 && 
           particle.appendCubeFaces(faceBuilder, matrices, camPos, partialTicks, particleColor)) {
           hasFaces = true;
         }
       } catch (Throwable throwable) {}
     } 
     
     if (hasFaces) BufferRenderer.method_43433(faceBuilder.method_60800());
     
     BufferBuilder lineBuilder = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_29344, VertexFormats.field_1576);
     boolean hasLines = false;
     for (int k = 0, m = this.renderCubeParticles.size(); k < m; k++) {
       CubeParticle particle = this.renderCubeParticles.get(k);
       try {
         int particleColor = particle.getRenderColor(baseColor, redColor, hurtPC, now);
         if ((particleColor >> 24 & 0xFF) > 0 && 
           particle.appendCubeLines(lineBuilder, matrices, camPos, partialTicks, particleColor)) {
           hasLines = true;
         }
       } catch (Throwable throwable) {}
     } 
     
     if (hasLines) BufferRenderer.method_43433(lineBuilder.method_60800());
     
     RenderSystem.setShader(ShaderProgramKeys.field_53880);
     RenderSystem.setShaderTexture(0, getBloomTexture());
     BufferBuilder bloomBuilder = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     boolean hasBloom = false;
     float camYaw = mc.field_1773.method_19418().method_19330();
     float camPitch = mc.field_1773.method_19418().method_19329();
     for (int n = 0, i1 = this.renderCubeParticles.size(); n < i1; n++) {
       CubeParticle particle = this.renderCubeParticles.get(n);
       try {
         int particleColor = particle.getRenderColor(baseColor, redColor, hurtPC, now);
         if (particle.appendBloom(bloomBuilder, matrices, camPos, camYaw, camPitch, partialTicks, particleColor, now)) {
           hasBloom = true;
         }
       } catch (Throwable throwable) {}
     } 
     
     if (hasBloom) BufferRenderer.method_43433(bloomBuilder.method_60800());
     
     RenderSystem.depthMask(true);
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableBlend();
     RenderSystem.enableCull();
     RenderSystem.enableDepthTest();
   } private void drawRing3D(Event3DRender event) {
     Vec3d vec;
     float entityHeight;
     if (this.appearValue <= 0.001F || this.lastTargetPos == null)
       return; 
     float partialTicks = mc.method_61966().method_60637(true);
 
     
     LivingEntity target = this.lastTarget;
     
     if (target != null && target.method_5805()) {
 
 
       
       vec = new Vec3d(MathHelper.method_16436(partialTicks, target.field_6038, target.method_23317()), MathHelper.method_16436(partialTicks, target.field_5971, target.method_23318()), MathHelper.method_16436(partialTicks, target.field_5989, target.method_23321()));
       
       entityHeight = target.method_17682();
     } else {
       vec = this.lastTargetPos;
       entityHeight = this.lastTargetHeight;
     } 
     
     Vec3d cam = mc.field_1773.method_19418().method_19326();
     double x = vec.field_1352 - cam.field_1352;
     double y = vec.field_1351 - cam.field_1351;
     double z = vec.field_1350 - cam.field_1350;
     
     double duration = 2000.0D / this.ringSpeed.get();
     double elapsed = (System.currentTimeMillis() % (long)duration);
     boolean side = (elapsed > duration / 2.0D);
     double progress = elapsed / duration / 2.0D;
     
     if (side) {
       progress--;
     } else {
       progress = 1.0D - progress;
     } 
     
     progress = (progress < 0.5D) ? (2.0D * progress * progress) : (1.0D - Math.pow(-2.0D * progress + 2.0D, 2.0D) / 2.0D);
     double eased = entityHeight / 1.2D * ((progress > 0.5D) ? (1.0D - progress) : progress) * (side ? -1 : true);
     
     int baseCol = getESPColor();
     float hurtPC = getHurtPC(target);
     int redCol = ColorUtils.rgb(255, 3, 3);
     int mainColor = overCol(baseCol, redCol, hurtPC);
     
     int colorWithAlpha = setAlpha(mainColor, 0.88235295F * this.appearValue);
     int colorTransparent = setAlpha(mainColor, 0.003921569F * this.appearValue);
     int colorFull = setAlpha(mainColor, this.appearValue);
     double radius = this.ringRadius.get();
     
     MatrixStack matrices = event.getMatrices();
     Matrix4f matrix = matrices.method_23760().method_23761();
     
     RenderSystem.depthMask(false);
     RenderSystem.disableDepthTest();
     RenderSystem.enableBlend();
     RenderSystem.blendFunc(770, 1);
     RenderSystem.disableCull();
     RenderSystem.setShader(ShaderProgramKeys.field_53876);
     
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27380, VertexFormats.field_1576);
     for (int i = 0; i <= 360; i++) {
       double rad = Math.toRadians(i);
       float px = (float)(x + Math.cos(rad) * radius);
       float pz = (float)(z + Math.sin(rad) * radius);
       float py1 = (float)(y + entityHeight * progress);
       float py2 = (float)(y + entityHeight * progress + eased);
       
       buffer.method_22918(matrix, px, py1, pz).method_39415(colorWithAlpha);
       buffer.method_22918(matrix, px, py2, pz).method_39415(colorTransparent);
     } 
     BufferRenderer.method_43433(buffer.method_60800());
     
     RenderSystem.lineWidth(1.5F);
     BufferBuilder lineBuffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_29345, VertexFormats.field_1576);
     for (int j = 0; j <= 360; j++) {
       double rad = Math.toRadians(j);
       float px = (float)(x + Math.cos(rad) * radius);
       float pz = (float)(z + Math.sin(rad) * radius);
       float py = (float)(y + entityHeight * progress);
       
       lineBuffer.method_22918(matrix, px, py, pz).method_39415(colorFull);
     } 
     BufferRenderer.method_43433(lineBuffer.method_60800());
     
     RenderSystem.enableCull();
     RenderSystem.disableBlend();
     RenderSystem.depthMask(true);
     RenderSystem.enableDepthTest();
   }
   
   private int setAlpha(int color, float alpha) {
     alpha = Math.max(0.0F, Math.min(1.0F, alpha));
     return color & 0xFFFFFF | (int)(alpha * 255.0F) << 24;
   }
 
   
   @EventLink(priority = -100)
   public void onRender2D(EventRender.Default event) {
     if (!this.mode.is("Кристаллы") || this.crystalAnimation <= 0.001F || this.lastTargetPos == null)
       return;  LivingEntity crystalTarget = (this.lastTarget != null && this.lastTarget.method_5805()) ? this.lastTarget : null;
     drawCrystalGlow2D(event.getContext().method_51448(), crystalTarget);
   }
   
   private int multAlpha(int color, float mult) {
     int a = (int)((color >> 24 & 0xFF) * mult);
     a = Math.max(0, Math.min(255, a));
     return a << 24 | color & 0xFFFFFF;
   }
   
   private int replAlpha(int color, int alpha) {
     alpha = Math.max(0, Math.min(255, alpha));
     return alpha << 24 | color & 0xFFFFFF;
   }
   
   int overCol(int color1, int color2, float factor) {
     factor = Math.max(0.0F, Math.min(1.0F, factor));
     int r1 = color1 >> 16 & 0xFF, g1 = color1 >> 8 & 0xFF, b1 = color1 & 0xFF, a1 = color1 >> 24 & 0xFF;
     int r2 = color2 >> 16 & 0xFF, g2 = color2 >> 8 & 0xFF, b2 = color2 & 0xFF, a2 = color2 >> 24 & 0xFF;
     int r = (int)(r1 + (r2 - r1) * factor);
     int g = (int)(g1 + (g2 - g1) * factor);
     int b = (int)(b1 + (b2 - b1) * factor);
     int a = (int)(a1 + (a2 - a1) * factor);
     return a << 24 | r << 16 | g << 8 | b;
   }
   
   private float getHurtPC(LivingEntity target) {
     if (!this.hurtColor.isState() || target == null) return 0.0F; 
     float partialTicks = (mc != null) ? mc.method_61966().method_60637(true) : 0.0F;
     float hurtTicks = MathHelper.method_15363(target.field_6235 - partialTicks, 0.0F, 10.0F);
     float progress = hurtTicks / 10.0F;
     return progress * progress * (3.0F - 2.0F * progress);
   }
   
   private void drawBillboard(MatrixStack matrices, Vec3d cameraPos, double worldX, double worldY, double worldZ, float baseScreenSize, int color, float rotation) {
     float distScale = getDistanceScale(cameraPos, worldX, worldY, worldZ);
     float half = baseScreenSize * distScale * 0.5F;
     drawBillboardInternal(matrices, cameraPos, worldX, worldY, worldZ, half, color, rotation);
   }
   
   private void drawStaticBillboard(MatrixStack matrices, Vec3d cameraPos, double worldX, double worldY, double worldZ, float worldSize, int color, float rotation) {
     float half = worldSize * 0.5F;
     drawBillboardInternal(matrices, cameraPos, worldX, worldY, worldZ, half, color, rotation);
   }
   
   private void drawBillboardInternal(MatrixStack matrices, Vec3d cameraPos, double worldX, double worldY, double worldZ, float half, int color, float rotation) {
     int r = color >> 16 & 0xFF;
     int g = color >> 8 & 0xFF;
     int b = color & 0xFF;
     int a = color >> 24 & 0xFF;
     if (a <= 0)
       return; 
     matrices.method_22903();
     matrices.method_22904(worldX - cameraPos.field_1352, worldY - cameraPos.field_1351, worldZ - cameraPos.field_1350);
     matrices.method_22907(RotationAxis.field_40716.rotationDegrees(-mc.field_1773.method_19418().method_19330()));
     matrices.method_22907(RotationAxis.field_40714.rotationDegrees(mc.field_1773.method_19418().method_19329()));
     if (rotation != 0.0F) {
       matrices.method_22907(RotationAxis.field_40718.rotationDegrees(rotation));
     }
     
     Matrix4f matrix = matrices.method_23760().method_23761();
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     buffer.method_22918(matrix, -half, -half, 0.0F).method_22913(0.0F, 1.0F).method_1336(r, g, b, a);
     buffer.method_22918(matrix, -half, half, 0.0F).method_22913(0.0F, 0.0F).method_1336(r, g, b, a);
     buffer.method_22918(matrix, half, half, 0.0F).method_22913(1.0F, 0.0F).method_1336(r, g, b, a);
     buffer.method_22918(matrix, half, -half, 0.0F).method_22913(1.0F, 1.0F).method_1336(r, g, b, a);
     BufferRenderer.method_43433(buffer.method_60800());
     matrices.method_22909();
   }
   
   private void renderMarker3D(Event3DRender event) {
     if (this.lastTargetPos == null || this.appearValue <= 0.001F)
       return; 
     Vec3d cam = mc.field_1773.method_19418().method_19326();
     double worldX = this.lastTargetPos.field_1352;
     double worldY = this.lastTargetPos.field_1351 + ((this.lastTargetHeight + 0.4F) * 0.5F);
     double worldZ = this.lastTargetPos.field_1350;
     
     float baseSize = this.size.getValue().floatValue() * 12.0F;
     float renderSize = baseSize * this.scaleValue;
     
     long now = System.currentTimeMillis();
     float dt = Math.max(0.001F, (float)(now - this.lastRotateUpdate) / 1000.0F);
     this.lastRotateUpdate = now;
     float cycleDuration = Math.max(0.35F, 2.2F / this.rotateSpeed.getValue().floatValue());
     this.rotProgress += dt / cycleDuration;
     while (this.rotProgress >= 1.0F) {
       this.rotProgress--;
       this.rotFrom = this.rotTo;
       this.rotTo = (this.rotTo > 0.0F) ? -280.0F : 280.0F;
     } 
     
     float accel = (float)Easings.SINE_IN_OUT.ease(this.rotProgress);
     float rotation = MathHelper.method_16439(accel, this.rotFrom, this.rotTo);
     
     float hurtPC = getHurtPC(this.lastTarget);
     int baseColor = multAlpha(getESPColor(), this.appearValue);
     int redColor = multAlpha(ColorUtils.rgb(255, 3, 3), this.appearValue);
     int color = overCol(baseColor, redColor, hurtPC);
     
     RenderSystem.enableBlend();
     RenderSystem.disableDepthTest();
     RenderSystem.depthMask(false);
     RenderSystem.disableCull();
     RenderSystem.blendFunc(770, 1);
     RenderSystem.setShader(ShaderProgramKeys.field_53880);
     RenderSystem.setShaderTexture(0, getCaptureTexture());
     
     drawBillboard(event.getMatrices(), cam, worldX, worldY, worldZ, renderSize, color, rotation);
     
     RenderSystem.enableCull();
     RenderSystem.depthMask(true);
     RenderSystem.enableDepthTest();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableBlend();
   } private void drawSouls3D(Event3DRender event) {
     Vec3d vec;
     float height;
     if (this.appearValue <= 0.001F || this.lastTargetPos == null)
       return; 
     float partialTicks = mc.method_61966().method_60637(true);
 
     
     LivingEntity target = this.lastTarget;
     if (target != null && target.method_5805()) {
 
 
       
       vec = new Vec3d(MathHelper.method_16436(partialTicks, target.field_6038, target.method_23317()), MathHelper.method_16436(partialTicks, target.field_5971, target.method_23318()), MathHelper.method_16436(partialTicks, target.field_5989, target.method_23321()));
       
       height = target.method_17682();
     } else {
       vec = this.lastTargetPos;
       height = this.lastTargetHeight;
     } 
     
     Vec3d cam = mc.field_1773.method_19418().method_19326();
     double baseX = vec.field_1352;
     double baseY = vec.field_1351 + (height / 2.0F);
     double baseZ = vec.field_1350;
     double radius = 0.7D;
     float fixedSize = 4.0F;
     long time = System.currentTimeMillis();
     float hurtPC = getHurtPC(target);
     int baseCol = getESPColor();
     int redCol = ColorUtils.rgb(255, 3, 3);
     
     RenderSystem.disableDepthTest();
     RenderSystem.enableBlend();
     RenderSystem.depthMask(false);
     RenderSystem.disableCull();
     RenderSystem.blendFunc(770, 1);
     RenderSystem.setShader(ShaderProgramKeys.field_53880);
     RenderSystem.setShaderTexture(0, getBloomTexture());
     
     MatrixStack matrices = event.getMatrices();
     int i;
     for (i = 0; i < 20; i++) {
       float trailFactor = 1.0F - i / 20.0F * 0.7F;
       double angle = 0.15D * (time - i * 10.0D) / 25.0D;
       double s = Math.sin(angle) * radius;
       double c = Math.cos(angle) * radius;
       double worldX = baseX + s;
       double worldY = baseY + c;
       double worldZ = baseZ - c;
       
       float sz = fixedSize * trailFactor;
       float alphaTrail = this.appearValue * 0.6F;
       int col = multAlpha(baseCol, alphaTrail * this.appearValue);
       int red = multAlpha(redCol, alphaTrail * this.appearValue);
       int color = overCol(col, red, hurtPC);
       
       drawStaticBillboard(matrices, cam, worldX, worldY, worldZ, sz * 0.12F, color, 0.0F);
       int glowColor = multAlpha(color, 0.45F);
       drawStaticBillboard(matrices, cam, worldX, worldY, worldZ, sz * 0.21F, glowColor, 0.0F);
     } 
     
     for (i = 0; i < 20; i++) {
       float trailFactor = 1.0F - i / 20.0F * 0.7F;
       double angle = 0.15D * (time - i * 10.0D) / 25.0D;
       double s = Math.sin(angle) * radius;
       double c = Math.cos(angle) * radius;
       double worldX = baseX - s;
       double worldY = baseY + s;
       double worldZ = baseZ - c;
       
       float sz = fixedSize * trailFactor;
       float alphaTrail = this.appearValue * 0.6F;
       int col = multAlpha(baseCol, alphaTrail * this.appearValue);
       int red = multAlpha(ColorUtils.rgb(235, 7, 7), alphaTrail * this.appearValue);
       int color = overCol(col, red, hurtPC);
       
       drawStaticBillboard(matrices, cam, worldX, worldY, worldZ, sz * 0.12F, color, 0.0F);
       int glowColor = multAlpha(color, 0.45F);
       drawStaticBillboard(matrices, cam, worldX, worldY, worldZ, sz * 0.21F, glowColor, 0.0F);
     } 
     
     for (i = 0; i < 20; i++) {
       float trailFactor = 1.0F - i / 20.0F * 0.7F;
       double angle = 0.15D * (time - i * 10.0D) / 25.0D;
       double s = Math.sin(angle) * radius;
       double c = Math.cos(angle) * radius;
       double worldX = baseX - s;
       double worldY = baseY - s;
       double worldZ = baseZ + c;
       
       float sz = fixedSize * trailFactor;
       float alphaTrail = this.appearValue * 0.6F;
       int col = multAlpha(baseCol, alphaTrail * this.appearValue);
       int red = multAlpha(redCol, alphaTrail * this.appearValue);
       int color = overCol(col, red, hurtPC);
       
       drawStaticBillboard(matrices, cam, worldX, worldY, worldZ, sz * 0.12F, color, 0.0F);
       int glowColor = multAlpha(color, 0.45F);
       drawStaticBillboard(matrices, cam, worldX, worldY, worldZ, sz * 0.21F, glowColor, 0.0F);
     } 
     
     RenderSystem.enableCull();
     RenderSystem.enableDepthTest();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableBlend();
     RenderSystem.depthMask(true);
   }
   
   private void addBMWGhosts(LivingEntity entity, float partialTicks, int cornersCount, int maxTime, int colorBase) {
     float xzRange = 0.7F;
     float yRange = entity.method_17682();
     int delayXZ = (int)this.bmwStrengthXZ.getValue().floatValue();
     int delayY = (int)this.bmwStrengthY.getValue().floatValue();
     long time = System.currentTimeMillis();
     float rotateProgress = (float)(time % delayXZ) / delayXZ;
     float xzRotate = rotateProgress * 360.0F;
     float yProgress = (float)(time % delayY) / delayY;
     float yLrpPC = 0.5F - 0.5F * MathHelper.method_15362(yProgress * 6.2831855F);
     
     for (int corner = 0; corner < cornersCount; corner++) {
       float cornersPC = corner / cornersCount;
       double yawRad = Math.toRadians(MathHelper.method_15393(cornersPC * 360.0F + xzRotate));
       float offsetX = -((float)Math.sin(yawRad)) * xzRange;
       float offsetY = yRange * yLrpPC;
       float offsetZ = (float)Math.cos(yawRad) * xzRange;
       this.bmwPoints.add(new GlowPoint(offsetX, offsetY, offsetZ, maxTime, colorBase));
     } 
   }
   private void drawBMW3D(Event3DRender event) {
     Vec3d basePos;
     if (this.bmwPoints.isEmpty() || this.appearValue <= 0.001F)
       return; 
     LivingEntity renderTarget = (this.lastTarget != null) ? this.lastTarget : this.lastHandledTarget;
     if (renderTarget == null && this.lastTargetPos == null)
       return; 
     float partialTicks = mc.method_61966().method_60637(true);
     
     if (renderTarget != null && renderTarget.method_5805()) {
 
 
       
       basePos = new Vec3d(MathHelper.method_16436(partialTicks, renderTarget.field_6038, renderTarget.method_23317()), MathHelper.method_16436(partialTicks, renderTarget.field_5971, renderTarget.method_23318()), MathHelper.method_16436(partialTicks, renderTarget.field_5989, renderTarget.method_23321()));
     } else {
       
       basePos = this.lastTargetPos;
     } 
     
     if (basePos == null)
       return; 
     Vec3d cam = mc.field_1773.method_19418().method_19326();
     float hurtPC = getHurtPC(renderTarget);
     float fixedScreenSize = 6.0F;
     
     RenderSystem.disableDepthTest();
     RenderSystem.enableBlend();
     RenderSystem.depthMask(false);
     RenderSystem.disableCull();
     RenderSystem.blendFunc(770, 1);
     RenderSystem.setShader(ShaderProgramKeys.field_53880);
     RenderSystem.setShaderTexture(0, getBloomTexture());
     
     MatrixStack matrices = event.getMatrices();
     
     for (GlowPoint point : this.bmwPoints) {
       float timePC = point.getTimeProgress();
       float trailFactor = 1.0F - timePC * 0.6F;
       
       double worldX = basePos.field_1352 + point.x;
       double worldY = basePos.field_1351 + point.y;
       double worldZ = basePos.field_1350 + point.z;
       
       float sz = fixedScreenSize * trailFactor;
       int alpha = (int)(255.0F * this.appearValue * trailFactor * 0.8F);
       alpha = Math.max(0, Math.min(255, alpha));
       int col = replAlpha(point.baseColor, alpha);
       int red = replAlpha(ColorUtils.rgb(255, 3, 3), alpha);
       int finalColor = overCol(col, red, hurtPC);
       
       drawBillboard(matrices, cam, worldX, worldY, worldZ, sz, finalColor, 0.0F);
     } 
     
     RenderSystem.enableCull();
     RenderSystem.enableDepthTest();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableBlend();
     RenderSystem.depthMask(true);
   }
   private void drawCelka3D(Event3DRender event) {
     Vec3d vec;
     if (this.appearValue <= 0.001F || this.lastTargetPos == null)
       return; 
     float partialTicks = mc.method_61966().method_60637(true);
 
     
     LivingEntity target = this.lastTarget;
     if (target != null && target.method_5805()) {
 
 
       
       vec = new Vec3d(MathHelper.method_16436(partialTicks, target.field_6038, target.method_23317()), MathHelper.method_16436(partialTicks, target.field_5971, target.method_23318()), MathHelper.method_16436(partialTicks, target.field_5989, target.method_23321()));
       
       float entityHeight = target.method_17682();
     } else {
       vec = this.lastTargetPos;
       float entityHeight = this.lastTargetHeight;
     } 
     
     Vec3d cam = mc.field_1773.method_19418().method_19326();
     double bx = vec.field_1352;
     double by = vec.field_1351;
     double bz = vec.field_1350;
     double t = System.currentTimeMillis() / 384.61539872299335D * 1.2000000476837158D;
     double tv = System.currentTimeMillis() / 666.6666666666666D * 1.2000000476837158D;
     int baseCol = getESPColor();
     float fixedSize = 4.0F;
     
     RenderSystem.disableDepthTest();
     RenderSystem.enableBlend();
     RenderSystem.depthMask(false);
     RenderSystem.disableCull();
     RenderSystem.blendFunc(770, 1);
     RenderSystem.setShader(ShaderProgramKeys.field_53880);
     RenderSystem.setShaderTexture(0, getBloomTexture());
     
     MatrixStack matrices = event.getMatrices();
     
     float radius = 0.65F;
     for (int k = 0; k < 4; k++) {
       for (int j = 0; j < 20; j++) {
         float kf = j / 20.0F;
         float sizeFactor = 1.0F - kf * 0.55F;
         
         double tj = t - j * 0.05D;
         double tvj = tv - j * 0.05D;
         double cyc = (Math.sin(tvj) + 1.0D) * 0.5D;
         
         double baseAngle = Math.toRadians(k * 90.0D + tj * 50.0D % 360.0D);
         double offX = Math.cos(baseAngle) * radius;
         double offZ = Math.sin(baseAngle) * radius;
 
         
         double offY = (k % 2 == 0) ? (0.1D + 1.7D * cyc) : (1.8D - 1.7D * cyc);
         
         double worldX = bx + offX;
         double worldY = by + offY;
         double worldZ = bz + offZ;
         
         float sz = fixedSize * sizeFactor;
         int finalAlpha = (int)(255.0F * this.appearValue * 0.6F);
         int color = replAlpha(baseCol, finalAlpha);
         
         drawBillboard(matrices, cam, worldX, worldY, worldZ, sz, color, 0.0F);
         int glowColor = multAlpha(color, 0.45F);
         drawBillboard(matrices, cam, worldX, worldY, worldZ, sz * 1.75F, glowColor, 0.0F);
       } 
       radius *= -1.0F;
     } 
     
     RenderSystem.enableCull();
     RenderSystem.enableDepthTest();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableBlend();
     RenderSystem.depthMask(true);
   }
   
   private void renderCrystals3D(MatrixStack ms, LivingEntity target, float partialTicks) {
     Vec3d renderPos;
     if (this.lastTargetPos == null || this.crystalAnimation <= 0.01F)
       return; 
     Vec3d cameraPos = mc.field_1773.method_19418().method_19326();
     int baseColor = ColorUtils.getThemeColor();
     int color = multAlpha(baseColor, this.crystalAnimation);
     int glowColor = multAlpha(baseColor, this.crystalAnimation * 0.28F);
     float hurtPC = getHurtPC(target);
     if (hurtPC > 0.0F) {
       int hurtColor = multAlpha(ColorUtils.rgb(255, 3, 3), this.crystalAnimation);
       color = overCol(color, hurtColor, hurtPC);
       glowColor = overCol(glowColor, multAlpha(hurtColor, 0.65F), hurtPC);
     } 
     
     float entityWidth = (target != null) ? target.method_17681() : this.lastTargetWidth;
     float entityHeight = (target != null) ? target.method_17682() : this.lastTargetHeight;
     float width = entityWidth * 1.5F;
 
     
     if (target != null && target.method_5805()) {
 
 
       
       renderPos = new Vec3d(MathHelper.method_16436(partialTicks, target.field_6038, target.method_23317()), MathHelper.method_16436(partialTicks, target.field_5971, target.method_23318()), MathHelper.method_16436(partialTicks, target.field_5989, target.method_23321()));
     } else {
       
       renderPos = this.lastTargetPos;
     } 
     
     RenderSystem.disableDepthTest();
     RenderSystem.enableBlend();
     RenderSystem.depthMask(false);
     RenderSystem.disableCull();
     
     float orbitScale = 1.2F - 0.5F * this.crystalAnimation;
     ms.method_22903();
     ms.method_22904(renderPos.field_1352 - cameraPos.field_1352, renderPos.field_1351 - cameraPos.field_1351, renderPos.field_1350 - cameraPos.field_1350);
     
     RenderSystem.defaultBlendFunc();
     RenderSystem.setShader(ShaderProgramKeys.field_53876);
     
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27379, VertexFormats.field_1576);
     
     for (int i = 0; i < 360; i += 20) {
       float angleRad = (float)Math.toRadians((i + this.crystalRotationAngle));
       float sin = (float)(Math.sin(angleRad) * width * orbitScale);
       float cos = (float)(Math.cos(angleRad) * width * orbitScale);
       float crystalSize = 0.1F;
       float yOffset = 0.1F + entityHeight * Math.abs(MathHelper.method_15374(i));
       
       float offsetX = sin;
       float offsetY = yOffset;
       float offsetZ = cos;
       float targetCenterY = entityHeight / 2.0F;
       float dirX = -offsetX;
       float dirY = targetCenterY - offsetY;
       float dirZ = -offsetZ;
       
       float length = (float)Math.sqrt((dirX * dirX + dirY * dirY + dirZ * dirZ));
       if (length >= 0.001F) {
         
         dirX /= length;
         dirY /= length;
         dirZ /= length;
         ms.method_22903();
         ms.method_46416(offsetX, offsetY, offsetZ);
         Vector3f initial = new Vector3f(0.0F, 1.0F, 0.0F);
         Vector3f dir = new Vector3f(dirX, dirY, dirZ);
         Vector3f axis = new Vector3f();
         initial.cross((Vector3fc)dir, axis);
         float axisLen = axis.length();
         if (axisLen >= 0.001F) {
           axis.div(axisLen);
           float dot = Math.max(-1.0F, Math.min(1.0F, initial.dot((Vector3fc)dir)));
           float angle = (float)Math.acos(dot);
           ms.method_22907((new Quaternionf()).setAngleAxis(angle, axis.x, axis.y, axis.z));
         } 
         renderCrystalShape(buffer, ms.method_23760().method_23761(), crystalSize, color);
         
         ms.method_22909();
       } 
     } 
     BufferRenderer.method_43433(buffer.method_60800());
     
     ms.method_22909();
     
     float glowBaseSize = 4.5F + entityWidth * 3.0F;
     float outerGlowSize = glowBaseSize * 1.28F;
     RenderSystem.blendFunc(770, 1);
     RenderSystem.setShader(ShaderProgramKeys.field_53880);
     RenderSystem.setShaderTexture(0, getBloomTexture());
     
     for (int j = 0; j < 360; j += 20) {
       float angleRad = (float)Math.toRadians((j + this.crystalRotationAngle));
       float sin = (float)(Math.sin(angleRad) * width * orbitScale);
       float cos = (float)(Math.cos(angleRad) * width * orbitScale);
       float yOffset = 0.1F + entityHeight * Math.abs(MathHelper.method_15374(j));
       
       double worldX = renderPos.field_1352 + sin;
       double worldY = renderPos.field_1351 + yOffset;
       double worldZ = renderPos.field_1350 + cos;
       drawBillboard(ms, cameraPos, worldX, worldY, worldZ, outerGlowSize, multAlpha(glowColor, 0.24F), this.crystalRotationAngle + j);
       drawBillboard(ms, cameraPos, worldX, worldY, worldZ, glowBaseSize, glowColor, -(this.crystalRotationAngle + j * 0.5F));
     } 
     
     RenderSystem.enableDepthTest();
     RenderSystem.enableCull();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableBlend();
     RenderSystem.depthMask(true);
   }
   
   private void renderCrystalShape(BufferBuilder buffer, Matrix4f matrix, float size, int color) {
     int r = color >> 16 & 0xFF;
     int g = color >> 8 & 0xFF;
     int b = color & 0xFF;
     int a = color >> 24 & 0xFF;
     
     float w = 0.34F * size / 0.1F;
     float h = 1.15F * size / 0.1F;
     w = 0.06F;
     h = 0.2F;
     tri(buffer, matrix, 0.0F, h, 0.0F, w, 0.0F, 0.0F, 0.0F, 0.0F, w, r, g, b, a);
     tri(buffer, matrix, 0.0F, h, 0.0F, 0.0F, 0.0F, w, -w, 0.0F, 0.0F, r, g, b, a);
     tri(buffer, matrix, 0.0F, h, 0.0F, -w, 0.0F, 0.0F, 0.0F, 0.0F, -w, r, g, b, a);
     tri(buffer, matrix, 0.0F, h, 0.0F, 0.0F, 0.0F, -w, w, 0.0F, 0.0F, r, g, b, a);
     tri(buffer, matrix, 0.0F, -h, 0.0F, w, 0.0F, 0.0F, 0.0F, 0.0F, w, r, g, b, a);
     tri(buffer, matrix, 0.0F, -h, 0.0F, 0.0F, 0.0F, w, -w, 0.0F, 0.0F, r, g, b, a);
     tri(buffer, matrix, 0.0F, -h, 0.0F, -w, 0.0F, 0.0F, 0.0F, 0.0F, -w, r, g, b, a);
     tri(buffer, matrix, 0.0F, -h, 0.0F, 0.0F, 0.0F, -w, w, 0.0F, 0.0F, r, g, b, a);
   }
   
   private float[] project2D(double worldX, double worldY, double worldZ) {
     return null;
   }
   
   private double getScale(double worldX, double worldY, double worldZ) {
     Vec3d cam = mc.field_1773.method_19418().method_19326();
     double dx = worldX - cam.field_1352;
     double dy = worldY - cam.field_1351;
     double dz = worldZ - cam.field_1350;
     double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
     return Math.max(0.5D, 8.0D / Math.max(0.1D, distance));
   }
   
   private void drawTexturedRect2D(MatrixStack matrix, float x, float y, float width, float height, int color) {
     int r = color >> 16 & 0xFF;
     int g = color >> 8 & 0xFF;
     int b = color & 0xFF;
     int a = color >> 24 & 0xFF;
     if (a <= 0)
       return;  Matrix4f mat = matrix.method_23760().method_23761();
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     buffer.method_22918(mat, x, y, 0.0F).method_22913(0.0F, 0.0F).method_1336(r, g, b, a);
     buffer.method_22918(mat, x, y + height, 0.0F).method_22913(0.0F, 1.0F).method_1336(r, g, b, a);
     buffer.method_22918(mat, x + width, y + height, 0.0F).method_22913(1.0F, 1.0F).method_1336(r, g, b, a);
     buffer.method_22918(mat, x + width, y, 0.0F).method_22913(1.0F, 0.0F).method_1336(r, g, b, a);
     BufferRenderer.method_43433(buffer.method_60800());
   }
 
 
 
   
   private void drawCrystalGlow2D(MatrixStack matrix, LivingEntity target) {}
 
 
 
   
   private void tri(BufferBuilder buffer, Matrix4f matrix, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, int r, int g, int b, int a) {
     buffer.method_22918(matrix, x1, y1, z1).method_1336(r, g, b, a);
     buffer.method_22918(matrix, x2, y2, z2).method_1336(r, g, b, a);
     buffer.method_22918(matrix, x3, y3, z3).method_1336(r, g, b, a);
   }
   private static class GlowPoint { final float x;
     final float y;
     final float z;
     final long startTime;
     final int maxLife;
     final int baseColor;
     
     GlowPoint(float x, float y, float z, int maxLife, int baseColor) {
       this.x = x;
       this.y = y;
       this.z = z;
       this.startTime = System.currentTimeMillis();
       this.maxLife = maxLife;
       this.baseColor = baseColor;
     }
     
     boolean shouldRemove() {
       return (System.currentTimeMillis() - this.startTime >= this.maxLife);
     }
     
     float getTimeProgress() {
       return MathHelper.method_15363((float)(System.currentTimeMillis() - this.startTime) / this.maxLife, 0.0F, 1.0F);
     }
     
     int getColor(float timePC) {
       int a = (int)((this.baseColor >> 24 & 0xFF) * (1.0F - timePC));
       a = Math.max(0, Math.min(255, a));
       return a << 24 | this.baseColor & 0xFFFFFF;
     } }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\TargetESP.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */