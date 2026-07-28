package shame.nazuna.client.modules.impl.render;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.util.ArrayList;
 import java.util.List;
 import net.minecraft.Entity;
 import net.minecraft.EnderPearlEntity;
 import net.minecraft.Blocks;
 import net.minecraft.Position;
 import net.minecraft.Box;
 import net.minecraft.HitResult;
 import net.minecraft.Vec3d;
 import net.minecraft.BufferRenderer;
 import net.minecraft.BufferBuilder;
 import net.minecraft.Tessellator;
 import net.minecraft.VertexFormats;
 import net.minecraft.VertexFormat;
 import net.minecraft.Identifier;
 import net.minecraft.MathHelper;
 import net.minecraft.RaycastContext;
 import net.minecraft.BlockHitResult;
 import net.minecraft.Camera;
 import net.minecraft.MatrixStack;
 import org.joml.Matrix4f;
 import org.joml.Quaternionf;
 import org.joml.Quaternionfc;
 import org.joml.Vector3f;
 import org.joml.Vector4f;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.render.RenderUtils;
 import shame.nazuna.api.utils.render.fonts.msdf.Font;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class Projectile extends Module {
   private final Font impactFont = Fonts.getFont("sf_regular", 14);
   
   public static Projectile INSTANCE = new Projectile();
   private static final Identifier BLOOM_TEXTURE = Identifier.method_60655("astra", "textures/particle/bloom.png");
   
   private final FloatSetting size = new FloatSetting("Размер", 1.2F, 0.6F, 2.4F, 0.1F);
   
   private final List<ImpactPoint> impactPoints = new ArrayList<>();
   private final Matrix4f lastProjectionMatrix = new Matrix4f();
   private final Quaternionf lastCameraRotation = new Quaternionf();
   private Vec3d lastCameraPos = Vec3d.field_1353;
   private boolean hasMatrices;
   
   public Projectile() {
     super("Projectile", "Траектория жемчуга эндера", Module.ModuleCategory.RENDER);
     addSettings(new Setting[] { (Setting)this.size });
   }
   
   @EventLink
   public void onRender3D(Event3DRender event) {
     if (mc.field_1687 == null || mc.field_1724 == null)
       return; 
     this.impactPoints.clear();
     this.hasMatrices = true;
     this.lastProjectionMatrix.set((Matrix4fc)event.getProjectionMatrix());
     this.lastCameraPos = event.getCamera().method_19326();
     this.lastCameraRotation.set((Quaternionfc)event.getCamera().method_23767());
     
     MatrixStack matrices = event.getMatrices();
     Camera camera = event.getCamera();
     Vec3d cameraPos = camera.method_19326();
     Quaternionf cameraRotation = camera.method_23767();
     float tickDelta = event.getTickDelta();
     
     RenderSystem.enableBlend();
     RenderSystem.blendFunc(770, 1);
     RenderSystem.disableCull();
     RenderSystem.disableDepthTest();
     RenderSystem.depthMask(false);
     RenderSystem.setShader(ShaderProgramKeys.field_53880);
     RenderSystem.setShaderTexture(0, BLOOM_TEXTURE);
     
     Box searchBox = mc.field_1724.method_5829().method_1014(128.0D);
     for (EnderPearlEntity pearl : mc.field_1687.method_8390(EnderPearlEntity.class, searchBox, Entity::method_5805)) {
       
       List<Vec3d> points = simulate(pearl, tickDelta);
       if (points.size() < 2)
         continue; 
       float seconds = (points.size() - 1) / 20.0F;
       Vec3d impactPos = points.get(points.size() - 1);
       this.impactPoints.add(new ImpactPoint(impactPos, seconds));
       
       float quadSize = this.size.get() * 0.2F;
       int color = ColorUtils.setAlphaColor(ColorUtils.getThemeColor(), 40);
       int r = color >> 16 & 0xFF;
       int g = color >> 8 & 0xFF;
       int b = color & 0xFF;
       int a = color >> 24 & 0xFF;
       
       matrices.method_22903();
       matrices.method_22904(-cameraPos.field_1352, -cameraPos.field_1351, -cameraPos.field_1350);
       
       BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
       for (int i = 0; i < points.size() - 1; i++) {
         Vec3d start = points.get(i);
         Vec3d end = points.get(i + 1);
         
         int samples = Math.max(2, Math.min(12, (int)Math.ceil(start.method_1022(end) / Math.max(quadSize * 1.75F, 0.08F))));
         for (int j = 0; j < samples; j++) {
           Vec3d interp = start.method_35590(end, j / samples);
           
           matrices.method_22903();
           matrices.method_22904(interp.field_1352, interp.field_1351, interp.field_1350);
           matrices.method_22907(cameraRotation);
           Matrix4f matrix = matrices.method_23760().method_23761();
           
           buffer.method_22918(matrix, -quadSize, -quadSize, 0.0F).method_22913(0.0F, 0.0F).method_1336(r, g, b, a);
           buffer.method_22918(matrix, -quadSize, quadSize, 0.0F).method_22913(0.0F, 1.0F).method_1336(r, g, b, a);
           buffer.method_22918(matrix, quadSize, quadSize, 0.0F).method_22913(1.0F, 1.0F).method_1336(r, g, b, a);
           buffer.method_22918(matrix, quadSize, -quadSize, 0.0F).method_22913(1.0F, 0.0F).method_1336(r, g, b, a);
           matrices.method_22909();
         } 
       } 
       BufferRenderer.method_43433(buffer.method_60800());
       matrices.method_22909();
       
       float markerSize = quadSize * 1.6F;
       int markerColor = ColorUtils.setAlphaColor(ColorUtils.getThemeColor(), 170);
       int mr = markerColor >> 16 & 0xFF;
       int mg = markerColor >> 8 & 0xFF;
       int mb = markerColor & 0xFF;
       int ma = markerColor >> 24 & 0xFF;
       float mx = (float)(impactPos.field_1352 - cameraPos.field_1352);
       float my = (float)(impactPos.field_1351 - cameraPos.field_1351 + 0.029999999329447746D);
       float mz = (float)(impactPos.field_1350 - cameraPos.field_1350);
       
       matrices.method_22903();
       matrices.method_46416(mx, my, mz);
       matrices.method_22907(cameraRotation);
       Matrix4f markerMatrix = matrices.method_23760().method_23761();
       BufferBuilder marker = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
       marker.method_22918(markerMatrix, -markerSize, -markerSize, 0.0F).method_22913(0.0F, 0.0F).method_1336(mr, mg, mb, ma);
       marker.method_22918(markerMatrix, -markerSize, markerSize, 0.0F).method_22913(0.0F, 1.0F).method_1336(mr, mg, mb, ma);
       marker.method_22918(markerMatrix, markerSize, markerSize, 0.0F).method_22913(1.0F, 1.0F).method_1336(mr, mg, mb, ma);
       marker.method_22918(markerMatrix, markerSize, -markerSize, 0.0F).method_22913(1.0F, 0.0F).method_1336(mr, mg, mb, ma);
       BufferRenderer.method_43433(marker.method_60800());
       matrices.method_22909();
     } 
     
     RenderSystem.depthMask(true);
     RenderSystem.enableDepthTest();
     RenderSystem.enableCull();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableBlend();
   }
   
   @EventLink
   public void onRender2D(EventRender.Default event) {
     if (!this.hasMatrices || this.impactPoints.isEmpty() || mc.field_1724 == null)
       return; 
     MatrixStack matrices = event.getContext().method_51448();
     Font font = this.impactFont;
     if (font == null)
       return; 
     int themeColor = ColorUtils.getThemeColor();
     
     for (ImpactPoint impact : this.impactPoints) {
       Vec3d screen = worldToScreen(impact.pos());
       if (screen == null)
         continue; 
       String text = formatOneDecimal(impact.seconds()) + " сек";
       float textWidth = font.getStringWidth(text);
       float boxWidth = textWidth + 10.0F;
       float boxHeight = 12.5F;
       float x = (float)screen.field_1352 - boxWidth / 2.0F;
       float y = (float)screen.field_1351 - 6.0F;
       
       RenderUtils.drawDefaultHudThemedPanel(matrices, x, y, boxWidth, boxHeight, 3.0F, 3.5F, themeColor);
       font.drawString(matrices, text, x + 5.5F, y + 4.55F, -1);
     } 
   }
   
   private Vec3d worldToScreen(Vec3d worldPos) {
     if (mc == null || mc.method_22683() == null) return null;
     
     Vector3f relative = new Vector3f((float)(worldPos.field_1352 - this.lastCameraPos.field_1352), (float)(worldPos.field_1351 - this.lastCameraPos.field_1351), (float)(worldPos.field_1350 - this.lastCameraPos.field_1350));
 
 
 
 
     
     Quaternionf invCameraRot = (new Quaternionf((Quaternionfc)this.lastCameraRotation)).conjugate();
     relative.rotate((Quaternionfc)invCameraRot);
     
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
     
     if (screenX < -400.0F || screenY < -400.0F || screenX > (mc.method_22683().method_4486() + 400) || screenY > (mc.method_22683().method_4502() + 400)) {
       return null;
     }
     
     return new Vec3d(screenX, screenY, ndcZ);
   }
   
   private String formatOneDecimal(float value) {
     int scaled = Math.round(value * 10.0F);
     return "" + scaled / 10 + "." + scaled / 10;
   }
   
   private List<Vec3d> simulate(EnderPearlEntity pearl, float tickDelta) {
     List<Vec3d> points = new ArrayList<>();
 
 
 
     
     Vec3d pos = new Vec3d(MathHelper.method_16436(tickDelta, pearl.field_6014, pearl.method_23317()), MathHelper.method_16436(tickDelta, pearl.field_6036, pearl.method_23318()), MathHelper.method_16436(tickDelta, pearl.field_5969, pearl.method_23321()));
     
     Vec3d motion = pearl.method_18798();
     points.add(pos);
     
     for (int i = 0; i < 300; i++) {
       Vec3d lastPos = pos;
       Vec3d nextPos = pos.method_1019(motion);
       
       BlockHitResult hit = mc.field_1687.method_17742(new RaycastContext(lastPos, nextPos, RaycastContext.class_3960.field_17558, RaycastContext.class_242.field_1348, (Entity)mc.field_1724));
 
 
 
 
 
 
       
       if (hit.method_17783() == HitResult.class_240.field_1332) {
         points.add(hit.method_17784());
         
         break;
       } 
       points.add(nextPos);
       pos = nextPos;
       
       boolean inWater = mc.field_1687.method_8320(BlockPos.method_49638((Position)pos)).method_27852(Blocks.field_10382);
       double drag = inWater ? 0.8D : 0.99D;
       motion = motion.method_1021(drag).method_1023(0.0D, 0.03D, 0.0D);
       
       if (pos.field_1351 <= mc.field_1687.method_31607())
         break; 
     } 
     return points;
   }
 }

