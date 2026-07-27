package shame.nazuna.client.modules.impl.combat;
 
 import com.mojang.blaze3d.systems.RenderSystem;
 import net.minecraft.class_10142;
 import net.minecraft.class_1309;
 import net.minecraft.class_238;
 import net.minecraft.class_243;
 import net.minecraft.class_286;
 import net.minecraft.class_287;
 import net.minecraft.class_289;
 import net.minecraft.class_290;
 import net.minecraft.class_293;
 import net.minecraft.class_2960;
 import net.minecraft.class_3532;
 import net.minecraft.class_4184;
 import net.minecraft.class_4587;
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
   
   private static final class_2960 GLOW_TEXTURE = class_2960.method_60655("astra", "textures/trajectories/glow.png");
   private static final float BOX_GLOW_OUTER_THICKNESS = 0.17F;
   private static final float BOX_GLOW_MID_THICKNESS = 0.13F;
   private static final float BOX_GLOW_CORE_THICKNESS = 0.11F;
   private static final float BOX_GLOW_LINE_U = 0.4F;
   private static final int[][] BOX_EDGES = new int[][] { { 0, 2 }, { 2, 6 }, { 6, 4 }, { 4, 0 }, { 1, 3 }, { 3, 7 }, { 7, 5 }, { 5, 1 }, { 0, 1 }, { 2, 3 }, { 6, 7 }, { 4, 5 } };
 
   
   private class_238 smoothedPredictionBox;
 
   
   private class_1309 smoothedTarget;
   
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
     class_1309 target = aura.getTarget();
     if (target == null || !target.method_5805() || !target.method_6128()) {
       resetPredictionSmoothing();
       
       return;
     } 
     class_238 predictedBox = buildPredictedBox(target);
     renderPredictionBox(event, smoothPredictionBox(target, predictedBox));
   }
 
   
   public void onDisable() {
     resetPredictionSmoothing();
     super.onDisable();
   }
   
   private class_238 smoothPredictionBox(class_1309 target, class_238 predictedBox) {
     if (this.smoothedPredictionBox == null || this.smoothedTarget != target || this.smoothedPredictionBox.method_1005().method_1025(predictedBox.method_1005()) > 144.0D) {
       this.smoothedPredictionBox = predictedBox;
       this.smoothedTarget = target;
       return predictedBox;
     } 
     
     double distance = Math.sqrt(this.smoothedPredictionBox.method_1005().method_1025(predictedBox.method_1005()));
     double smoothFactor = class_3532.method_15350(0.08D + distance * 0.035D, 0.08D, 0.18D);
     this.smoothedPredictionBox = lerpBox(this.smoothedPredictionBox, predictedBox, smoothFactor);
     return this.smoothedPredictionBox;
   }
   
   private void resetPredictionSmoothing() {
     this.smoothedPredictionBox = null;
     this.smoothedTarget = null;
   }
   
   private class_238 buildPredictedBox(class_1309 target) {
     class_238 currentBox = target.method_5829();
     class_243 predictedCenter = PredictUtils.predict(target, currentBox.method_1005(), Math.max(0, this.forward.getValue().intValue()));
     class_243 offset = predictedCenter.method_1020(currentBox.method_1005());
     return currentBox.method_997(offset);
   }
   
   private class_238 lerpBox(class_238 from, class_238 to, double factor) {
     return new class_238(
         class_3532.method_16436(factor, from.field_1323, to.field_1323), 
         class_3532.method_16436(factor, from.field_1322, to.field_1322), 
         class_3532.method_16436(factor, from.field_1321, to.field_1321), 
         class_3532.method_16436(factor, from.field_1320, to.field_1320), 
         class_3532.method_16436(factor, from.field_1325, to.field_1325), 
         class_3532.method_16436(factor, from.field_1324, to.field_1324));
   }
 
   
   private void renderPredictionBox(Event3DRender event, class_238 box) {
     class_4587 matrices = event.getMatrices();
     class_4184 camera = event.getCamera();
     class_243 cameraPos = camera.method_19326();
     
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
     RenderSystem.setShader(class_10142.field_53880);
     
     Matrix4f matrix = matrices.method_23760().method_23761();
     class_287 quads = class_289.method_1348().method_60827(class_293.class_5596.field_27382, class_290.field_1575);
     addGlowBox(quads, matrix, cameraPos, box, outerColor, 0.17F);
     addGlowBox(quads, matrix, cameraPos, box, midColor, 0.13F);
     addGlowBox(quads, matrix, cameraPos, box, coreColor, 0.11F);
     class_286.method_43433(quads.method_60800());
     
     RenderSystem.setShaderTexture(0, 0);
     RenderSystem.defaultBlendFunc();
     RenderSystem.depthMask(true);
     RenderSystem.enableDepthTest();
     RenderSystem.enableCull();
     RenderSystem.disableBlend();
   }
   
   private void addGlowBox(class_287 buffer, Matrix4f matrix, class_243 camera, class_238 box, int color, float thickness) {
     class_243[] corners = getBoxVectors(box);
     for (int[] edge : BOX_EDGES) {
       addGlowEdge(buffer, matrix, camera, corners[edge[0]], corners[edge[1]], color, thickness);
     }
   }
   
   private void addGlowEdge(class_287 buffer, Matrix4f matrix, class_243 camera, class_243 start, class_243 end, int color, float thickness) {
     class_243 edge = end.method_1020(start);
     if (edge.method_1027() <= 1.0E-6D)
       return; 
     class_243 direction = edge.method_1029();
     double overlap = (thickness * 0.22F);
     start = start.method_1020(direction.method_1021(overlap));
     end = end.method_1019(direction.method_1021(overlap));
     edge = end.method_1020(start);
     
     class_243 center = start.method_1019(end).method_1021(0.5D);
     class_243 toCamera = camera.method_1020(center);
     if (toCamera.method_1027() <= 1.0E-6D) {
       toCamera = new class_243(0.0D, 1.0D, 0.0D);
     }
     
     class_243 side = edge.method_1036(toCamera);
     if (side.method_1027() <= 1.0E-6D) {
       side = edge.method_1036(new class_243(0.0D, 1.0D, 0.0D));
       if (side.method_1027() <= 1.0E-6D) {
         side = edge.method_1036(new class_243(1.0D, 0.0D, 0.0D));
       }
     } 
     
     side = side.method_1029().method_1021((thickness * 0.48F));
     
     class_243 p1 = start.method_1019(side).method_1020(camera);
     class_243 p2 = start.method_1020(side).method_1020(camera);
     class_243 p3 = end.method_1020(side).method_1020(camera);
     class_243 p4 = end.method_1019(side).method_1020(camera);
     
     float[] rgba = ColorUtils.rgba(color);
     buffer.method_22918(matrix, (float)p1.field_1352, (float)p1.field_1351, (float)p1.field_1350).method_22913(0.4F, 0.0F).method_22915(rgba[0], rgba[1], rgba[2], rgba[3]);
     buffer.method_22918(matrix, (float)p2.field_1352, (float)p2.field_1351, (float)p2.field_1350).method_22913(0.4F, 1.0F).method_22915(rgba[0], rgba[1], rgba[2], rgba[3]);
     buffer.method_22918(matrix, (float)p3.field_1352, (float)p3.field_1351, (float)p3.field_1350).method_22913(0.4F, 1.0F).method_22915(rgba[0], rgba[1], rgba[2], rgba[3]);
     buffer.method_22918(matrix, (float)p4.field_1352, (float)p4.field_1351, (float)p4.field_1350).method_22913(0.4F, 0.0F).method_22915(rgba[0], rgba[1], rgba[2], rgba[3]);
   }
   
   private class_243[] getBoxVectors(class_238 box) {
     return new class_243[] { new class_243(box.field_1323, box.field_1322, box.field_1321), new class_243(box.field_1323, box.field_1325, box.field_1321), new class_243(box.field_1320, box.field_1322, box.field_1321), new class_243(box.field_1320, box.field_1325, box.field_1321), new class_243(box.field_1323, box.field_1322, box.field_1324), new class_243(box.field_1323, box.field_1325, box.field_1324), new class_243(box.field_1320, box.field_1322, box.field_1324), new class_243(box.field_1320, box.field_1325, box.field_1324) };
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\ElytraTarget.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */