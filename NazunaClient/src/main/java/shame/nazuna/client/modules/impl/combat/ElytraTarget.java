package shame.nazuna.client.modules.impl.combat;
 
 import com.mojang.blaze3d.systems.RenderSystem;
 import net.minecraft.ShaderProgramKeys;
 import net.minecraft.LivingEntity;
 import net.minecraft.Box;
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
 import org.joml.Matrix4f;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.combat.PredictUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class ElytraTarget
   extends Module {
   public static ElytraTarget INSTANCE = new ElytraTarget();
   
   private static final Identifier GLOW_TEXTURE = Identifier.method_60655("astra", "textures/trajectories/glow.png");
   private static final float BOX_GLOW_OUTER_THICKNESS = 0.17F;
   private static final float BOX_GLOW_MID_THICKNESS = 0.13F;
   private static final float BOX_GLOW_CORE_THICKNESS = 0.11F;
   private static final float BOX_GLOW_LINE_U = 0.4F;
   private static final int[][] BOX_EDGES = new int[][] { { 0, 2 }, { 2, 6 }, { 6, 4 }, { 4, 0 }, { 1, 3 }, { 3, 7 }, { 7, 5 }, { 5, 1 }, { 0, 1 }, { 2, 3 }, { 6, 7 }, { 4, 5 } };
 
   
   private Box smoothedPredictionBox;
 
   
   private LivingEntity smoothedTarget;
   
   public final FloatSetting forward = new FloatSetting("Сила предикта", 3.0F, 1.0F, 6.0F, 1.0F);
   
   public ElytraTarget() {
     super("ElytraSample", "Таргетит игрока на элитрах", Module.ModuleCategory.COMBAT);
     addSettings(new Setting[] { (Setting)this.forward });
   }
   
   @EventLink
   public void onRender3D(Event3DRender event) {
     if (mc.field_1724 == null || mc.field_1687 == null) {
       resetPredictionSmoothing();
       return;
     } 
     if (!mc.field_1724.method_6128()) {
       resetPredictionSmoothing();
       
       return;
     } 
     Aura aura = ModuleClass.aura;
     if (aura == null || !aura.isEnable()) {
       resetPredictionSmoothing();
       
       return;
     } 
     LivingEntity target = aura.getTarget();
     if (target == null || !target.method_5805() || !target.method_6128()) {
       resetPredictionSmoothing();
       
       return;
     } 
     Box predictedBox = buildPredictedBox(target);
     renderPredictionBox(event, smoothPredictionBox(target, predictedBox));
   }
 
   
   public void onDisable() {
     resetPredictionSmoothing();
     super.onDisable();
   }
   
   private Box smoothPredictionBox(LivingEntity target, Box predictedBox) {
     if (this.smoothedPredictionBox == null || this.smoothedTarget != target || this.smoothedPredictionBox.method_1005().method_1025(predictedBox.method_1005()) > 144.0D) {
       this.smoothedPredictionBox = predictedBox;
       this.smoothedTarget = target;
       return predictedBox;
     } 
     
     double distance = Math.sqrt(this.smoothedPredictionBox.method_1005().method_1025(predictedBox.method_1005()));
     double smoothFactor = MathHelper.method_15350(0.08D + distance * 0.035D, 0.08D, 0.18D);
     this.smoothedPredictionBox = lerpBox(this.smoothedPredictionBox, predictedBox, smoothFactor);
     return this.smoothedPredictionBox;
   }
   
   private void resetPredictionSmoothing() {
     this.smoothedPredictionBox = null;
     this.smoothedTarget = null;
   }
   
   private Box buildPredictedBox(LivingEntity target) {
     Box currentBox = target.method_5829();
     Vec3d predictedCenter = PredictUtils.predict(target, currentBox.method_1005(), Math.max(0, this.forward.getValue().intValue()));
     Vec3d offset = predictedCenter.method_1020(currentBox.method_1005());
     return currentBox.method_997(offset);
   }
   
   private Box lerpBox(Box from, Box to, double factor) {
     return new Box(
         MathHelper.method_16436(factor, from.field_1323, to.field_1323), 
         MathHelper.method_16436(factor, from.field_1322, to.field_1322), 
         MathHelper.method_16436(factor, from.field_1321, to.field_1321), 
         MathHelper.method_16436(factor, from.field_1320, to.field_1320), 
         MathHelper.method_16436(factor, from.field_1325, to.field_1325), 
         MathHelper.method_16436(factor, from.field_1324, to.field_1324));
   }
 
   
   private void renderPredictionBox(Event3DRender event, Box box) {
     MatrixStack matrices = event.getMatrices();
     Camera camera = event.getCamera();
     Vec3d cameraPos = camera.method_19326();
     
     int themeColor = ColorUtils.getThemeColor();
     int outerColor = ColorUtils.setAlphaColor(themeColor, 118);
     int midColor = ColorUtils.setAlphaColor(ColorUtils.interpolateColor(themeColor, -1, 0.24F), 210);
     int coreColor = ColorUtils.setAlphaColor(ColorUtils.interpolateColor(themeColor, -1, 0.6F), 255);
     
     RenderSystem.enableBlend();
     RenderSystem.blendFunc(770, 1);
     RenderSystem.disableCull();
     RenderSystem.disableDepthTest();
     RenderSystem.depthMask(false);
     RenderSystem.setShaderTexture(0, GLOW_TEXTURE);
     RenderSystem.setShader(ShaderProgramKeys.field_53880);
     
     Matrix4f matrix = matrices.method_23760().method_23761();
     BufferBuilder quads = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     addGlowBox(quads, matrix, cameraPos, box, outerColor, 0.17F);
     addGlowBox(quads, matrix, cameraPos, box, midColor, 0.13F);
     addGlowBox(quads, matrix, cameraPos, box, coreColor, 0.11F);
     BufferRenderer.method_43433(quads.method_60800());
     
     RenderSystem.setShaderTexture(0, 0);
     RenderSystem.defaultBlendFunc();
     RenderSystem.depthMask(true);
     RenderSystem.enableDepthTest();
     RenderSystem.enableCull();
     RenderSystem.disableBlend();
   }
   
   private void addGlowBox(BufferBuilder buffer, Matrix4f matrix, Vec3d camera, Box box, int color, float thickness) {
     Vec3d[] corners = getBoxVectors(box);
     for (int[] edge : BOX_EDGES) {
       addGlowEdge(buffer, matrix, camera, corners[edge[0]], corners[edge[1]], color, thickness);
     }
   }
   
   private void addGlowEdge(BufferBuilder buffer, Matrix4f matrix, Vec3d camera, Vec3d start, Vec3d end, int color, float thickness) {
     Vec3d edge = end.method_1020(start);
     if (edge.method_1027() <= 1.0E-6D)
       return; 
     Vec3d direction = edge.method_1029();
     double overlap = (thickness * 0.22F);
     start = start.method_1020(direction.method_1021(overlap));
     end = end.method_1019(direction.method_1021(overlap));
     edge = end.method_1020(start);
     
     Vec3d center = start.method_1019(end).method_1021(0.5D);
     Vec3d toCamera = camera.method_1020(center);
     if (toCamera.method_1027() <= 1.0E-6D) {
       toCamera = new Vec3d(0.0D, 1.0D, 0.0D);
     }
     
     Vec3d side = edge.method_1036(toCamera);
     if (side.method_1027() <= 1.0E-6D) {
       side = edge.method_1036(new Vec3d(0.0D, 1.0D, 0.0D));
       if (side.method_1027() <= 1.0E-6D) {
         side = edge.method_1036(new Vec3d(1.0D, 0.0D, 0.0D));
       }
     } 
     
     side = side.method_1029().method_1021((thickness * 0.48F));
     
     Vec3d p1 = start.method_1019(side).method_1020(camera);
     Vec3d p2 = start.method_1020(side).method_1020(camera);
     Vec3d p3 = end.method_1020(side).method_1020(camera);
     Vec3d p4 = end.method_1019(side).method_1020(camera);
     
     float[] rgba = ColorUtils.rgba(color);
     buffer.method_22918(matrix, (float)p1.field_1352, (float)p1.field_1351, (float)p1.field_1350).method_22913(0.4F, 0.0F).method_22915(rgba[0], rgba[1], rgba[2], rgba[3]);
     buffer.method_22918(matrix, (float)p2.field_1352, (float)p2.field_1351, (float)p2.field_1350).method_22913(0.4F, 1.0F).method_22915(rgba[0], rgba[1], rgba[2], rgba[3]);
     buffer.method_22918(matrix, (float)p3.field_1352, (float)p3.field_1351, (float)p3.field_1350).method_22913(0.4F, 1.0F).method_22915(rgba[0], rgba[1], rgba[2], rgba[3]);
     buffer.method_22918(matrix, (float)p4.field_1352, (float)p4.field_1351, (float)p4.field_1350).method_22913(0.4F, 0.0F).method_22915(rgba[0], rgba[1], rgba[2], rgba[3]);
   }
   
   private Vec3d[] getBoxVectors(Box box) {
     return new Vec3d[] { new Vec3d(box.field_1323, box.field_1322, box.field_1321), new Vec3d(box.field_1323, box.field_1325, box.field_1321), new Vec3d(box.field_1320, box.field_1322, box.field_1321), new Vec3d(box.field_1320, box.field_1325, box.field_1321), new Vec3d(box.field_1323, box.field_1322, box.field_1324), new Vec3d(box.field_1323, box.field_1325, box.field_1324), new Vec3d(box.field_1320, box.field_1322, box.field_1324), new Vec3d(box.field_1320, box.field_1325, box.field_1324) };
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\ElytraTarget.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */