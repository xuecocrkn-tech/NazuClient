package shame.nazuna.client.modules.impl.render;
 
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import net.minecraft.ShaderProgramKeys;
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
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class JumpCircle
   extends Module
 {
   public static JumpCircle INSTANCE = new JumpCircle();
   
   private static final float MAX_LIFETIME_MS = 1850.0F;
   private static final float ROTATION_SPEED = 120.0F;
   private static final float PULSE_SPEED = 7.0F;
   private static final float PULSE_SCALE = 0.06F;
   private static final float PULSE_ALPHA = 0.12F;
   private static final int MAX_CIRCLES = 8;
   private final FloatSetting radius = new FloatSetting("Радиус", 1.85F, 0.5F, 4.0F, 0.1F);
   private final FloatSetting speed = new FloatSetting("Скорость", 1.2F, 1.0F, 5.0F, 0.1F);
   private final FloatSetting fadeSpeed = new FloatSetting("Скорость исчезновения", 1.5F, 1.0F, 5.0F, 0.5F);
   
   private final List<CircleData> circles = new ArrayList<>();
   private final Identifier circleTexture = Identifier.method_60655("astra", "textures/jumpcircle/circle.png");
   
   private boolean wasOnGround = true;
   
   public JumpCircle() {
     super("JumpCircle", "Круг при прыжке", Module.ModuleCategory.RENDER);
     addSettings(new Setting[] { (Setting)this.radius, (Setting)this.speed, (Setting)this.fadeSpeed });
   }
 
   
   public void onEnable() {
     if (mc.field_1724 != null) {
       this.wasOnGround = mc.field_1724.method_24828();
     }
     super.onEnable();
   }
 
   
   public void onDisable() {
     this.circles.clear();
     super.onDisable();
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     boolean isOnGround = mc.field_1724.method_24828();
     if (this.wasOnGround && !isOnGround) {
 
 
       
       Vec3d pos = new Vec3d(mc.field_1724.method_23317(), Math.floor(mc.field_1724.method_23318()) + 0.001D, mc.field_1724.method_23321());
       
       this.circles.add(new CircleData(pos, System.currentTimeMillis()));
       while (this.circles.size() > 8) {
         this.circles.remove(0);
       }
     } 
     this.wasOnGround = isOnGround;
     
     long now = System.currentTimeMillis();
     float lifeTimeMs = getLifeTimeMs();
     Iterator<CircleData> iterator = this.circles.iterator();
     while (iterator.hasNext()) {
       CircleData circle = iterator.next();
       if (now - circle.startTimeMs > (long)lifeTimeMs) {
         iterator.remove();
       }
     } 
   }
   
   @EventLink
   public void onRender3D(Event3DRender event) {
     if (this.circles.isEmpty())
       return; 
     long now = System.currentTimeMillis();
     Vec3d cameraPos = event.getCamera().method_19326();
     MatrixStack matrices = event.getMatrices();
     
     RenderSystem.enableBlend();
     RenderSystem.enableDepthTest();
     RenderSystem.depthMask(false);
     RenderSystem.disableCull();
     RenderSystem.blendFunc(770, 1);
     RenderSystem.setShader(ShaderProgramKeys.field_53880);
     RenderSystem.setShaderTexture(0, this.circleTexture);
     
     for (CircleData circle : this.circles) {
       float progress = getProgress(now, circle);
       if (progress >= 1.0F)
         continue; 
       float alpha = getAlpha(progress);
       if (alpha <= 0.01F)
         continue;  renderGlowCircle(matrices, cameraPos, circle, progress, alpha, now);
     } 
     
     RenderSystem.enableCull();
     RenderSystem.depthMask(true);
     RenderSystem.enableDepthTest();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableBlend();
     RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }
   
   private float getLifeTimeMs() {
     return 1850.0F / Math.max(0.25F, this.speed.get());
   }
   
   private float getProgress(long now, CircleData circle) {
     return (float)(now - circle.startTimeMs) / getLifeTimeMs();
   }
   
   private float getAlpha(float progress) {
     float fade = MathHelper.method_15363(progress * this.fadeSpeed.get(), 0.0F, 1.0F);
     return 1.0F - fade;
   }
   
   private void renderGlowCircle(MatrixStack matrices, Vec3d cameraPos, CircleData circle, float progress, float alpha, long now) {
     float lifeTimeSec = (float)(now - circle.startTimeMs) / 1000.0F;
     float easedProgress = easeOutCubic(progress);
     float scale = Math.min(easedProgress * this.radius.get(), this.radius.get());
     
     float rotation = lifeTimeSec * 120.0F * this.speed.get();
     rotation += (float)Math.sin(progress * Math.PI * 2.0D) * 30.0F;
     
     float pulse = (float)Math.sin((lifeTimeSec * 7.0F * this.speed.get()));
     float pulseScale = 1.0F + pulse * 0.06F;
     float pulseAlpha = MathHelper.method_15363(alpha * (1.0F + pulse * 0.12F), 0.0F, 1.0F);
     float alphaBoost = MathHelper.method_15363(pulseAlpha * 1.25F, 0.0F, 1.0F);
     float finalScale = scale * pulseScale;
     
     int baseTheme = getStableThemeColor();
     int secondaryTheme = getStableThemeSecondaryColor();
     int colorA = ColorUtils.setAlphaColor(baseTheme, (int)(255.0F * alphaBoost));
     int colorB = ColorUtils.setAlphaColor(secondaryTheme, (int)(255.0F * alphaBoost));
     int darkA = ColorUtils.setAlphaColor(ColorUtils.darken(baseTheme, 0.65F), (int)(255.0F * MathHelper.method_15363(alphaBoost * 0.9F, 0.0F, 1.0F)));
     int darkB = ColorUtils.setAlphaColor(ColorUtils.darken(secondaryTheme, 0.65F), (int)(255.0F * MathHelper.method_15363(alphaBoost * 0.9F, 0.0F, 1.0F)));
     
     matrices.method_22903();
     matrices.method_22904(circle.pos.field_1352 - cameraPos.field_1352, circle.pos.field_1351 - cameraPos.field_1351, circle.pos.field_1350 - cameraPos.field_1350);
     matrices.method_22907(RotationAxis.field_40714.rotationDegrees(90.0F));
     matrices.method_22907(RotationAxis.field_40718.rotationDegrees(rotation));
     
     Matrix4f matrix = matrices.method_23760().method_23761();
     float half = finalScale * 0.5F;
     float thickScale = finalScale * 1.08F;
     float thickHalf = thickScale * 0.5F;
     
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     addTexturedQuad(buffer, matrix, -half, -half, half, half, colorA, colorB);
     addTexturedQuad(buffer, matrix, -thickHalf, -thickHalf, thickHalf, thickHalf, darkA, darkB);
     BufferRenderer.method_43433(buffer.method_60800());
     
     matrices.method_22909();
   }
   
   private void addTexturedQuad(BufferBuilder buffer, Matrix4f matrix, float x1, float y1, float x2, float y2, int colorA, int colorB) {
     int aR = colorA >> 16 & 0xFF;
     int aG = colorA >> 8 & 0xFF;
     int aB = colorA & 0xFF;
     int aA = colorA >> 24 & 0xFF;
     int bR = colorB >> 16 & 0xFF;
     int bG = colorB >> 8 & 0xFF;
     int bB = colorB & 0xFF;
     int bA = colorB >> 24 & 0xFF;
     
     buffer.method_22918(matrix, x1, y1, 0.0F).method_22913(0.0F, 1.0F).method_1336(aR, aG, aB, aA);
     buffer.method_22918(matrix, x1, y2, 0.0F).method_22913(0.0F, 0.0F).method_1336(bR, bG, bB, bA);
     buffer.method_22918(matrix, x2, y2, 0.0F).method_22913(1.0F, 0.0F).method_1336(bR, bG, bB, bA);
     buffer.method_22918(matrix, x2, y1, 0.0F).method_22913(1.0F, 1.0F).method_1336(aR, aG, aB, aA);
   }
   
   private int getStableThemeColor() {
     if (!NazunaClient.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
       return (NazunaClient.INSTANCE.themeStorage.getThemes().getTheme()).color[0];
     }
     return ColorUtils.getThemeColor();
   }
   
   private int getStableThemeSecondaryColor() {
     if (!NazunaClient.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
       return (NazunaClient.INSTANCE.themeStorage.getThemes().getTheme()).color[0];
     }
     return ColorUtils.getThemeColor(180);
   }
   
   private static float easeOutCubic(float t) {
     float u = 1.0F - t;
     return 1.0F - u * u * u;
   }
 }

