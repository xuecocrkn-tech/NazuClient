package shame.nazuna.client.modules.impl.render;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.awt.Color;
 import java.util.Objects;
 import net.minecraft.LivingEntityRenderState;
 import net.minecraft.PlayerEntityRenderState;
 import net.minecraft.PlayerEntityRenderer;
 import net.minecraft.ShaderProgramKeys;
 import net.minecraft.Entity;
 import net.minecraft.PlayerEntity;
 import net.minecraft.Vec3d;
 import net.minecraft.GlUniform;
 import net.minecraft.BufferRenderer;
 import net.minecraft.BufferBuilder;
 import net.minecraft.Tessellator;
 import net.minecraft.VertexFormats;
 import net.minecraft.VertexFormat;
 import net.minecraft.MathHelper;
 import net.minecraft.MatrixStack;
 import net.minecraft.Perspective;
 import net.minecraft.BipedEntityModel;
 import net.minecraft.PlayerEntityModel;
 import net.minecraft.ShaderProgram;
 import net.minecraft.ModelPart;
 import net.minecraft.AbstractClientPlayerEntity;
 import net.minecraft.EntityRenderer;
 import org.joml.Matrix4f;
 import org.lwjgl.opengl.GL11;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.render.ShaderUtils;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ListSetting;
 import shame.nazuna.mixin.LivingEntityRendererAccessor;
 
 public class Chams extends Module {
   public static final Chams INSTANCE = new Chams();
   
   public static final String TARGET_PLAYERS = "Игроков";
   
   public static final String TARGET_FRIENDS = "Друзей";
   public static final String TARGET_SELF = "Себя";
   private static final int DEFAULT_FILL_ALPHA = 130;
   private static final float DEFAULT_LINE_WIDTH = 0.5F;
   private static final float CLIENT_FILL_SATURATION = 1.18F;
   private static final float CLIENT_FILL_BRIGHTNESS = 1.12F;
   private static final float CLIENT_OUTLINE_SATURATION = 1.12F;
   private static final float CLIENT_OUTLINE_BRIGHTNESS = 1.08F;
   private static final float MIN_PULSE_ALPHA = 0.65F;
   private static final float PULSE_SWING = 0.35F;
   private static final int FRIEND_FILL_COLOR = (new Color(85, 255, 85, 60)).getRGB();
   private static final int FRIEND_OUTLINE_COLOR = (new Color(100, 255, 100, 255)).getRGB();
   
   private static final long OUTLINE_RETRY_DELAY_MS = 3000L;
   private final ListSetting rendering = new ListSetting("Отображать", new BooleanSetting[] { new BooleanSetting("Игроков", true), new BooleanSetting("Друзей", true), new BooleanSetting("Себя", false) });
 
 
 
   
   private final BooleanSetting waves = new BooleanSetting("Волны", true);
   
   private final FloatSetting waveSpeedX;
   
   private final FloatSetting waveSpeedY;
   
   private final FloatSetting waveScale;
   
   private final FloatSetting waveDensity;
   
   private final FloatSetting waveGlow;
   
   private final BooleanSetting glow;
   
   private final FloatSetting glowIntensity;
   
   private final FloatSetting glowLayers;
   
   private final BooleanSetting pulse;
   private final FloatSetting pulseSpeed;
   private final BooleanSetting hideOriginal;
   private final BooleanSetting hideItemsAndCape;
   private final long startTime;
   private boolean outlineAssistReady;
   private long nextOutlineRetryAt;
   
   private Chams() {
     super("Chams", "Чамсы по модели игрока", Module.ModuleCategory.RENDER); Objects.requireNonNull(this.waves); this.waveSpeedX = (new FloatSetting("Скорость X", 0.22F, 0.0F, 1.5F, 0.01F)).visible(this.waves::isState); Objects.requireNonNull(this.waves); this.waveSpeedY = (new FloatSetting("Скорость Y", 0.15F, 0.0F, 1.5F, 0.01F)).visible(this.waves::isState); Objects.requireNonNull(this.waves); this.waveScale = (new FloatSetting("Размер волн", 1.35F, 0.2F, 4.0F, 0.05F)).visible(this.waves::isState); Objects.requireNonNull(this.waves); this.waveDensity = (new FloatSetting("Плотность волн", 1.15F, 0.5F, 3.0F, 0.05F)).visible(this.waves::isState); Objects.requireNonNull(this.waves); this.waveGlow = (new FloatSetting("Сила волн", 1.0F, 0.2F, 3.0F, 0.05F)).visible(this.waves::isState); this.glow = new BooleanSetting("Свечение", true); Objects.requireNonNull(this.glow); this.glowIntensity = (new FloatSetting("Сила свечения", 2.0F, 1.0F, 5.0F, 0.1F)).visible(this.glow::isState); Objects.requireNonNull(this.glow); this.glowLayers = (new FloatSetting("Слои свечения", 3.0F, 1.0F, 6.0F, 1.0F)).visible(this.glow::isState); this.pulse = new BooleanSetting("Пульсирование", false); Objects.requireNonNull(this.pulse); this.pulseSpeed = (new FloatSetting("Скорость пульсации", 2.0F, 0.5F, 5.0F, 0.1F)).visible(this.pulse::isState); this.hideOriginal = new BooleanSetting("Скрыть оригинал", false); this.hideItemsAndCape = new BooleanSetting("Скрывать предметы и плащ", false); this.startTime = System.currentTimeMillis();
     addSettings(new Setting[] { (Setting)this.rendering, (Setting)this.waves, (Setting)this.waveSpeedX, (Setting)this.waveSpeedY, (Setting)this.waveScale, (Setting)this.waveDensity, (Setting)this.waveGlow, (Setting)this.glow, (Setting)this.glowIntensity, (Setting)this.glowLayers, (Setting)this.pulse, (Setting)this.pulseSpeed, (Setting)this.hideOriginal, (Setting)this.hideItemsAndCape });
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public void onEnable() {
     super.onEnable();
     this.outlineAssistReady = false;
     this.nextOutlineRetryAt = 0L;
     tryEnsureOutlineProcessor();
   }
 
   
   public void onDisable() {
     this.outlineAssistReady = false;
     this.nextOutlineRetryAt = 0L;
     super.onDisable();
   }
   
   @EventLink(priority = 100)
   public void onRender3D(Event3DRender event) {
     if (!isEnable() || mc.field_1687 == null || mc.field_1724 == null) {
       return;
     }
     
     if (hasOutlineAssistTargets() && !this.outlineAssistReady && System.currentTimeMillis() >= this.nextOutlineRetryAt) {
       tryEnsureOutlineProcessor();
     }
     
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableCull();
     RenderSystem.disableDepthTest();
     RenderSystem.depthMask(false);
     
     for (PlayerEntity player : mc.field_1687.method_18456()) {
       if (!affects(player)) {
         continue;
       }
       if (player == mc.field_1724 && mc.field_1690.method_31044() == Perspective.field_26664) {
         continue;
       }
       renderManualPlayer(event, player);
     } 
     
     RenderSystem.depthMask(true);
     RenderSystem.enableDepthTest();
     RenderSystem.enableCull();
     RenderSystem.disableBlend();
     RenderSystem.lineWidth(1.0F);
   } private void renderManualPlayer(Event3DRender event, PlayerEntity player) {
     AbstractClientPlayerEntity clientPlayer;
     PlayerEntityRenderer renderer;
     if (player instanceof AbstractClientPlayerEntity) { clientPlayer = (AbstractClientPlayerEntity)player; }
     else
     { return; }
     
     EntityRenderer<?, ?> rawRenderer = mc.method_1561().method_3953((Entity)player);
     if (rawRenderer instanceof PlayerEntityRenderer) { renderer = (PlayerEntityRenderer)rawRenderer; }
     else
     { return; }
     
     PlayerEntityRenderState state = renderer.method_62608();
     renderer.method_62604(clientPlayer, state, event.getTickDelta());
     PlayerEntityModel model = (PlayerEntityModel)renderer.method_4038();
     model.method_62110(state);
     
     MatrixStack matrices = event.getMatrices();
     matrices.method_22903();
     setupModelMatrix(matrices, state, renderer, event.getCamera().method_19326(), player, event.getTickDelta());
     
     int fillColor = resolveFillColor(player);
     int outlineColor = resolveOutlineColor(player);
     renderShaderFillModel(matrices, (BipedEntityModel<?>)model, 0.0F, fillColor);
     renderOutlineModel(matrices, (BipedEntityModel<?>)model, 0.0F, outlineColor);
     
     matrices.method_22909();
   }
   
   private void setupModelMatrix(MatrixStack matrices, PlayerEntityRenderState state, PlayerEntityRenderer renderer, Vec3d cameraPos, PlayerEntity player, float tickDelta) {
     Vec3d pos = player.method_30950(tickDelta);
     double x = pos.field_1352 - cameraPos.field_1352;
     double y = pos.field_1351 - cameraPos.field_1351;
     double z = pos.field_1350 - cameraPos.field_1350;
     matrices.method_22904(x, y, z);
     
     if (state.field_53463 != null) {
       float eyeOffset = state.field_53331 - 0.1F;
       matrices.method_46416(-state.field_53463.method_10148() * eyeOffset, 0.0F, -state.field_53463.method_10165() * eyeOffset);
     } 
     
     float baseScale = state.field_53453;
     matrices.method_22905(baseScale, baseScale, baseScale);
     LivingEntityRendererAccessor accessor = (LivingEntityRendererAccessor)renderer;
     accessor.astra$setupTransforms((LivingEntityRenderState)state, matrices, state.field_53446, baseScale);
     matrices.method_22905(-1.0F, -1.0F, 1.0F);
     accessor.astra$scale((LivingEntityRenderState)state, matrices);
     matrices.method_46416(0.0F, -1.501F, 0.0F);
   }
   
   private void renderShaderFillModel(MatrixStack matrices, BipedEntityModel<?> model, float expand, int color) {
     if (!this.waves.isState()) {
       renderSolidFillModel(matrices, model, expand, color);
       
       return;
     } 
     ShaderProgram shader = mc.method_62887().method_62947(ShaderUtils.chamsFill);
     if (shader == null) {
       return;
     }
     
     RenderSystem.setShader(ShaderUtils.chamsFill);
     setUniform(shader, "time", this.waves.isState() ? ((float)(System.currentTimeMillis() - this.startTime) / 1000.0F) : 0.0F);
     setUniform(shader, "speedX", this.waveSpeedX.get());
     setUniform(shader, "speedY", this.waveSpeedY.get());
     setUniform(shader, "scale", this.waveScale.get());
     setUniform(shader, "density", this.waveDensity.get());
     setUniform(shader, "glowStrength", this.waveGlow.get());
     
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     ModelPart root = model.method_63512();
     renderFillPart(matrices, buffer, root, model.field_3398, -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, expand, color);
     renderFillPart(matrices, buffer, root, model.field_3391, -4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, expand, color);
     renderFillPart(matrices, buffer, root, model.field_3401, -3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand, color);
     renderFillPart(matrices, buffer, root, model.field_27433, -1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand, color);
     renderFillPart(matrices, buffer, root, model.field_3392, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand, color);
     renderFillPart(matrices, buffer, root, model.field_3397, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand, color);
     BufferRenderer.method_43433(buffer.method_60800());
   }
   
   private void renderSolidFillModel(MatrixStack matrices, BipedEntityModel<?> model, float expand, int color) {
     RenderSystem.setShader(ShaderProgramKeys.field_53876);
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1576);
     ModelPart root = model.method_63512();
     renderSolidFillPart(matrices, buffer, root, model.field_3398, -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, expand, color);
     renderSolidFillPart(matrices, buffer, root, model.field_3391, -4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, expand, color);
     renderSolidFillPart(matrices, buffer, root, model.field_3401, -3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand, color);
     renderSolidFillPart(matrices, buffer, root, model.field_27433, -1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand, color);
     renderSolidFillPart(matrices, buffer, root, model.field_3392, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand, color);
     renderSolidFillPart(matrices, buffer, root, model.field_3397, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand, color);
     BufferRenderer.method_43433(buffer.method_60800());
   }
 
   
   private void renderSolidFillPart(MatrixStack baseStack, BufferBuilder buffer, ModelPart root, ModelPart part, float offX, float offY, float offZ, float width, float height, float depth, float expand, int color) {
     baseStack.method_22903();
     root.method_22703(baseStack);
     part.method_22703(baseStack);
     
     Matrix4f matrix = baseStack.method_23760().method_23761();
     float scale = 0.0625F;
     float expandScale = expand * scale;
     
     float minX = offX * scale - expandScale;
     float minY = offY * scale - expandScale;
     float minZ = offZ * scale - expandScale;
     float maxX = (offX + width) * scale + expandScale;
     float maxY = (offY + height) * scale + expandScale;
     float maxZ = (offZ + depth) * scale + expandScale;
     
     addSolidQuad(buffer, matrix, minX, maxY, minZ, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, color);
     addSolidQuad(buffer, matrix, minX, minY, maxZ, minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, color);
     addSolidQuad(buffer, matrix, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ, maxX, minY, minZ, color);
     addSolidQuad(buffer, matrix, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ, minX, minY, maxZ, color);
     addSolidQuad(buffer, matrix, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ, minX, minY, minZ, color);
     addSolidQuad(buffer, matrix, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, maxX, minY, maxZ, color);
     
     baseStack.method_22909();
   }
 
   
   private void renderFillPart(MatrixStack baseStack, BufferBuilder buffer, ModelPart root, ModelPart part, float offX, float offY, float offZ, float width, float height, float depth, float expand, int color) {
     baseStack.method_22903();
     root.method_22703(baseStack);
     part.method_22703(baseStack);
     
     Matrix4f matrix = baseStack.method_23760().method_23761();
     float scale = 0.0625F;
     float expandScale = expand * scale;
     
     float minX = offX * scale - expandScale;
     float minY = offY * scale - expandScale;
     float minZ = offZ * scale - expandScale;
     float maxX = (offX + width) * scale + expandScale;
     float maxY = (offY + height) * scale + expandScale;
     float maxZ = (offZ + depth) * scale + expandScale;
     
     addQuad(buffer, matrix, minX, maxY, minZ, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, color);
     addQuad(buffer, matrix, minX, minY, maxZ, minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, color);
     addQuad(buffer, matrix, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ, maxX, minY, minZ, color);
     addQuad(buffer, matrix, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ, minX, minY, maxZ, color);
     addQuad(buffer, matrix, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ, minX, minY, minZ, color);
     addQuad(buffer, matrix, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, maxX, minY, maxZ, color);
     
     baseStack.method_22909();
   }
 
 
 
 
 
   
   private void addQuad(BufferBuilder buffer, Matrix4f matrix, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int color) {
     int r = ColorUtils.red(color);
     int g = ColorUtils.green(color);
     int b = ColorUtils.blue(color);
     int a = ColorUtils.alpha(color);
     
     float u1 = waveU(x1, y1, z1);
     float v1 = waveV(x1, y1, z1);
     float u2 = waveU(x2, y2, z2);
     float v2 = waveV(x2, y2, z2);
     float u3 = waveU(x3, y3, z3);
     float v3 = waveV(x3, y3, z3);
     float u4 = waveU(x4, y4, z4);
     float v4 = waveV(x4, y4, z4);
     
     buffer.method_22918(matrix, x1, y1, z1).method_22913(u1, v1).method_1336(r, g, b, a);
     buffer.method_22918(matrix, x2, y2, z2).method_22913(u2, v2).method_1336(r, g, b, a);
     buffer.method_22918(matrix, x3, y3, z3).method_22913(u3, v3).method_1336(r, g, b, a);
     buffer.method_22918(matrix, x4, y4, z4).method_22913(u4, v4).method_1336(r, g, b, a);
   }
 
 
 
 
 
   
   private void addSolidQuad(BufferBuilder buffer, Matrix4f matrix, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int color) {
     int r = ColorUtils.red(color);
     int g = ColorUtils.green(color);
     int b = ColorUtils.blue(color);
     int a = ColorUtils.alpha(color);
     
     buffer.method_22918(matrix, x1, y1, z1).method_1336(r, g, b, a);
     buffer.method_22918(matrix, x2, y2, z2).method_1336(r, g, b, a);
     buffer.method_22918(matrix, x3, y3, z3).method_1336(r, g, b, a);
     buffer.method_22918(matrix, x4, y4, z4).method_1336(r, g, b, a);
   }
   
   private float waveU(float x, float y, float z) {
     return x * 1.15F + z * 0.72F;
   }
   
   private float waveV(float x, float y, float z) {
     return y * 1.05F - z * 0.38F + x * 0.18F;
   }
   
   private void renderOutlineModel(MatrixStack matrices, BipedEntityModel<?> model, float expand, int color) {
     RenderSystem.setShader(ShaderProgramKeys.field_53876);
     GL11.glEnable(2848);
     GL11.glHint(3154, 4354);
     RenderSystem.lineWidth(0.5F);
     
     if (this.glow.isState()) {
       RenderSystem.blendFuncSeparate(770, 1, 1, 0);
       int layers = Math.max(1, Math.round(this.glowLayers.get()));
       float intensity = Math.max(1.0F, this.glowIntensity.get());
       for (int index = layers; index >= 1; index--) {
         float layerExpand = expand + index * 0.5F * intensity;
         float alphaMul = 1.0F / (index + 1) * 0.7F;
         int alpha = Math.max(1, Math.min(255, Math.round(ColorUtils.alpha(color) * alphaMul)));
         drawOutlineParts(matrices, model, layerExpand, withAlpha(color, alpha));
       } 
     } 
     
     RenderSystem.defaultBlendFunc();
     drawOutlineParts(matrices, model, expand, color);
     GL11.glDisable(2848);
   }
   
   private void drawOutlineParts(MatrixStack matrices, BipedEntityModel<?> model, float expand, int color) {
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_29344, VertexFormats.field_1576);
     ModelPart root = model.method_63512();
     renderPartOutlineLines(matrices, buffer, root, model.field_3398, -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, expand, color);
     renderPartOutlineLines(matrices, buffer, root, model.field_3391, -4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, expand, color);
     renderPartOutlineLines(matrices, buffer, root, model.field_3401, -3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand, color);
     renderPartOutlineLines(matrices, buffer, root, model.field_27433, -1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand, color);
     renderPartOutlineLines(matrices, buffer, root, model.field_3392, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand, color);
     renderPartOutlineLines(matrices, buffer, root, model.field_3397, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand, color);
     BufferRenderer.method_43433(buffer.method_60800());
   }
 
   
   private void renderPartOutlineLines(MatrixStack baseStack, BufferBuilder buffer, ModelPart root, ModelPart part, float offX, float offY, float offZ, float width, float height, float depth, float expand, int color) {
     baseStack.method_22903();
     root.method_22703(baseStack);
     part.method_22703(baseStack);
     
     float scale = 0.0625F;
     float expandScale = expand * scale;
     float minX = offX * scale - expandScale;
     float minY = offY * scale - expandScale;
     float minZ = offZ * scale - expandScale;
     float maxX = (offX + width) * scale + expandScale;
     float maxY = (offY + height) * scale + expandScale;
     float maxZ = (offZ + depth) * scale + expandScale;
     Matrix4f matrix = baseStack.method_23760().method_23761();
     
     addLine(buffer, matrix, minX, minY, minZ, maxX, minY, minZ, color);
     addLine(buffer, matrix, maxX, minY, minZ, maxX, minY, maxZ, color);
     addLine(buffer, matrix, maxX, minY, maxZ, minX, minY, maxZ, color);
     addLine(buffer, matrix, minX, minY, maxZ, minX, minY, minZ, color);
     
     addLine(buffer, matrix, minX, maxY, minZ, maxX, maxY, minZ, color);
     addLine(buffer, matrix, maxX, maxY, minZ, maxX, maxY, maxZ, color);
     addLine(buffer, matrix, maxX, maxY, maxZ, minX, maxY, maxZ, color);
     addLine(buffer, matrix, minX, maxY, maxZ, minX, maxY, minZ, color);
     
     addLine(buffer, matrix, minX, minY, minZ, minX, maxY, minZ, color);
     addLine(buffer, matrix, maxX, minY, minZ, maxX, maxY, minZ, color);
     addLine(buffer, matrix, maxX, minY, maxZ, maxX, maxY, maxZ, color);
     addLine(buffer, matrix, minX, minY, maxZ, minX, maxY, maxZ, color);
     
     baseStack.method_22909();
   }
   
   private void addLine(BufferBuilder buffer, Matrix4f matrix, float x1, float y1, float z1, float x2, float y2, float z2, int color) {
     int r = ColorUtils.red(color);
     int g = ColorUtils.green(color);
     int b = ColorUtils.blue(color);
     int a = ColorUtils.alpha(color);
     
     buffer.method_22918(matrix, x1, y1, z1).method_1336(r, g, b, a);
     buffer.method_22918(matrix, x2, y2, z2).method_1336(r, g, b, a);
   }
   
   private void setUniform(ShaderProgram shader, String name, float value) {
     GlUniform uniform = shader.method_34582(name);
     if (uniform != null) {
       uniform.method_1251(value);
     }
   }
   
   public boolean affects(PlayerEntity player) {
     if (!isEnable() || player == null || !player.method_5805()) {
       return false;
     }
     if (player == mc.field_1724) {
       return (this.rendering.is("Себя") && mc.field_1690.method_31044() != Perspective.field_26664);
     }
     if (isFriend(player)) {
       return this.rendering.is("Друзей");
     }
     return this.rendering.is("Игроков");
   }
   
   public boolean shouldHideBaseModel(PlayerEntity player) {
     return (this.hideOriginal.isState() && affects(player));
   }
   
   public boolean shouldHideItemsAndCape(PlayerEntity player) {
     return (this.hideItemsAndCape.isState() && affects(player));
   }
   
   public boolean shouldUseOutlineAssist(PlayerEntity player) {
     return affects(player);
   }
   
   public boolean shouldHideOutlineFramebuffer() {
     return (isEnable() && hasOutlineAssistTargets());
   }
   
   public int resolveFillColor(PlayerEntity player) {
     return applyPulse(baseFillColor(player));
   }
   
   public int resolveOutlineColor(PlayerEntity player) {
     return applyPulse(baseOutlineColor(player));
   }
   
   private int baseFillColor(PlayerEntity player) {
     if (isFriend(player)) {
       return FRIEND_FILL_COLOR;
     }
     return vividWithAlpha(ColorUtils.getThemeColor(), 1.18F, 1.12F, 130);
   }
   
   private int baseOutlineColor(PlayerEntity player) {
     if (isFriend(player)) {
       return FRIEND_OUTLINE_COLOR;
     }
     return vividWithAlpha(ColorUtils.getThemeColor(), 1.12F, 1.08F, 255);
   }
   
   private int applyPulse(int color) {
     if (!this.pulse.isState()) {
       return color;
     }
     float elapsedSeconds = (float)(System.currentTimeMillis() - this.startTime) / 1000.0F;
     float pulseValue = (float)((Math.sin((elapsedSeconds * this.pulseSpeed.get()) * Math.PI) + 1.0D) * 0.5D);
     float alphaMul = 0.65F + 0.35F * pulseValue;
     return ColorUtils.multAlpha(color, alphaMul);
   }
   
   private int vividWithAlpha(int color, float saturationBoost, float brightnessBoost, int alpha) {
     float[] hsb = Color.RGBtoHSB(ColorUtils.red(color), ColorUtils.green(color), ColorUtils.blue(color), null);
     float saturation = MathHelper.method_15363(hsb[1] * saturationBoost, 0.0F, 1.0F);
     float brightness = MathHelper.method_15363(Math.max(hsb[2], 0.8F) * brightnessBoost, 0.0F, 1.0F);
     int rgb = Color.HSBtoRGB(hsb[0], saturation, brightness);
     return ColorUtils.rgba(ColorUtils.red(rgb), ColorUtils.green(rgb), ColorUtils.blue(rgb), alpha);
   }
   
   private int withAlpha(int color, int alpha) {
     return color & 0xFFFFFF | (alpha & 0xFF) << 24;
   }
   
   private boolean isFriend(PlayerEntity player) {
     return (NazunaClient.INSTANCE != null && NazunaClient.INSTANCE.friendStorage != null && NazunaClient.INSTANCE.friendStorage
       
       .isFriend(player.method_5477().getString()));
   }
   
   private boolean hasOutlineAssistTargets() {
     if (!isEnable() || mc.field_1687 == null || mc.field_1724 == null) {
       return false;
     }
     
     for (PlayerEntity player : mc.field_1687.method_18456()) {
       if (shouldUseOutlineAssist(player)) {
         return true;
       }
     } 
     return false;
   }
   
   private boolean tryEnsureOutlineProcessor() {
     if (mc.field_1769 == null) {
       this.outlineAssistReady = false;
       return false;
     } 
     
     if (mc.field_1769.method_22990() != null) {
       this.outlineAssistReady = true;
       return true;
     } 
     
     try {
       mc.field_1769.method_3296();
     } catch (Exception exception) {}
 
     
     this.outlineAssistReady = (mc.field_1769.method_22990() != null);
     if (!this.outlineAssistReady) {
       this.nextOutlineRetryAt = System.currentTimeMillis() + 3000L;
     }
     return this.outlineAssistReady;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\Chams.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */