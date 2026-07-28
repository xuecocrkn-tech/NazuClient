package shame.nazuna.client.modules.impl.render;
 import com.mojang.blaze3d.systems.RenderSystem;
 import net.minecraft.ShaderProgramKeys;
 import net.minecraft.BlockView;
 import net.minecraft.BlockPos;
 import net.minecraft.Box;
 import net.minecraft.HitResult;
 import net.minecraft.Vec3d;
 import net.minecraft.VoxelShape;
 import net.minecraft.Framebuffer;
 import net.minecraft.GlUniform;
 import net.minecraft.BufferRenderer;
 import net.minecraft.BufferBuilder;
 import net.minecraft.Tessellator;
 import net.minecraft.VertexFormats;
 import net.minecraft.VertexFormat;
 import net.minecraft.BlockHitResult;
 import net.minecraft.ShaderProgram;
 import net.minecraft.SimpleFramebuffer;
 import org.joml.Matrix4f;
 import org.lwjgl.opengl.GL11;
 import org.lwjgl.opengl.GL30;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.storages.implement.helpertstorages.Theme;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.render.ShaderUtils;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class BlockOverlay extends Module {
   public static BlockOverlay INSTANCE = new BlockOverlay();
   private final ModeSetting mode = new ModeSetting("Режим", "Шейдер", new String[] { "Шейдер", "Нитки" });
   private final FloatSetting waveSpeed = (new FloatSetting("Скорость волн", 1.2F, 0.1F, 5.0F, 0.1F))
     .visible(() -> Boolean.valueOf(this.mode.is("Шейдер")));
   private final FloatSetting waveScale = (new FloatSetting("Частота волн", 1.0F, 1.0F, 3.0F, 0.1F))
     .visible(() -> Boolean.valueOf(this.mode.is("Шейдер")));
   private final FloatSetting lineSpeed = (new FloatSetting("Скорость нитей", 1.4F, 0.1F, 5.0F, 0.1F))
     .visible(() -> Boolean.valueOf(this.mode.is("Нитки")));
   private final FloatSetting lineJitter = (new FloatSetting("Изгиб нитей", 0.55F, 0.0F, 1.5F, 0.01F))
     .visible(() -> Boolean.valueOf(this.mode.is("Нитки")));
   private final FloatSetting outline = new FloatSetting("Ширина обводки", 1.1F, 0.1F, 5.0F, 0.1F);
   private final FloatSetting glow = new FloatSetting("Сила свечения", 1.0F, 0.0F, 5.0F, 0.1F);
   private final FloatSetting fill = new FloatSetting("Заливка", 0.6F, 0.0F, 1.0F, 0.01F);
   private final FloatSetting alpha = new FloatSetting("Прозрачность", 1.0F, 0.0F, 1.0F, 0.01F);
   private final FloatSetting smooth = new FloatSetting("Плавность", 0.24F, 0.05F, 0.6F, 0.01F);
   
   private Framebuffer maskBuffer;
   private int fbWidth = -1;
   private int fbHeight = -1;
   
   private boolean hasMask;
   private BlockPos lastBlockPos;
   private Box displayBox;
   private Box targetBox;
   private int cachedThemeColor1 = -1;
   private int cachedThemeColor2 = -1;
   
   public BlockOverlay() {
     super("BlockOverlay", "Block overlay shader", Module.ModuleCategory.RENDER);
     addSettings(new Setting[] { (Setting)this.mode, (Setting)this.waveSpeed, (Setting)this.waveScale, (Setting)this.lineSpeed, (Setting)this.lineJitter, (Setting)this.outline, (Setting)this.glow, (Setting)this.fill, (Setting)this.alpha, (Setting)this.smooth });
   }
 
   
   public void onDisable() {
     this.hasMask = false;
     this.lastBlockPos = null;
     this.displayBox = null;
     this.targetBox = null;
     super.onDisable();
   }
   
   @EventLink(priority = -100)
   public void onRender3D(Event3DRender event) {
     if (mc == null || mc.field_1687 == null || mc.field_1724 == null)
       return; 
     Box worldBox = getTargetedBlockBox();
     if (worldBox == null) {
       this.hasMask = false;
       this.lastBlockPos = null;
       this.displayBox = null;
       this.targetBox = null;
       
       return;
     } 
     if (this.displayBox == null || this.targetBox == null || this.lastBlockPos == null) {
       this.displayBox = worldBox;
       this.targetBox = worldBox;
     } else {
       this.targetBox = worldBox;
       this.displayBox = lerpBox(this.displayBox, this.targetBox, this.smooth.get());
     } 
     this.lastBlockPos = BlockPos.method_49637(worldBox.field_1323, worldBox.field_1322, worldBox.field_1321);
     updateCachedThemeColors();
     
     Vec3d cam = event.getCamera().method_19326();
     Box localBox = this.displayBox.method_989(-cam.field_1352, -cam.field_1351, -cam.field_1350);
     Matrix4f matrix = event.getMatrices().method_23760().method_23761();
     
     ensureMaskBuffer();
     if (this.maskBuffer == null)
       return; 
     this.hasMask = true;
     this.maskBuffer.method_1236(0.0F, 0.0F, 0.0F, 0.0F);
     this.maskBuffer.method_1230();
     copyMainDepthToMask();
     this.maskBuffer.method_1235(false);
     
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableCull();
     RenderSystem.enableDepthTest();
     RenderSystem.depthMask(false);
     RenderSystem.setShader(ShaderProgramKeys.field_53876);
     drawMaskBox(matrix, localBox);
     RenderSystem.depthMask(true);
     RenderSystem.disableDepthTest();
     RenderSystem.enableCull();
     RenderSystem.disableBlend();
     
     mc.method_1522().method_1235(false);
     
     if (this.mode.is("Нитки")) {
       drawAnimatedWeb(matrix, localBox);
       return;
     } 
   }
   
   @EventLink(priority = 200)
   public void onRender2D(EventRender.Default event) {
     if (!this.hasMask || this.maskBuffer == null)
       return;  if (this.mode.is("Нитки"))
       return; 
     ShaderProgram shader = mc.method_62887().method_62947(ShaderUtils.blockOverlay);
     if (shader == null)
       return; 
     boolean lineMode = this.mode.is("Нитки");
     int color1 = this.cachedThemeColor1;
     int color2 = this.cachedThemeColor2;
     
     mc.method_1522().method_1235(false);
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.enableDepthTest();
     
     RenderSystem.setShader(ShaderUtils.blockOverlay);
     RenderSystem.setShaderTexture(0, this.maskBuffer.method_30277());
     
     setUniform(shader, "texelSize", 1.0F / Math.max(1, mc.method_22683().method_4489()), 1.0F / Math.max(1, mc.method_22683().method_4506()));
     setUniform(shader, "color", ColorUtils.redf(color1), ColorUtils.greenf(color1), ColorUtils.bluef(color1));
     setUniform(shader, "color2", ColorUtils.redf(color2), ColorUtils.greenf(color2), ColorUtils.bluef(color2));
     setUniform(shader, "time", (float)(System.currentTimeMillis() % 100000L) / 1000.0F);
     setUniform(shader, "speed", this.waveSpeed.get());
     setUniform(shader, "scale", this.waveScale.get());
     setUniform(shader, "outline", this.outline.get());
     setUniform(shader, "glow", lineMode ? 0.0F : this.glow.get());
     setUniform(shader, "fill", lineMode ? 0.0F : this.fill.get());
     setUniform(shader, "alpha", lineMode ? 1.0F : this.alpha.get());
     setUniform(shader, "outlineOnly", lineMode ? 1.0F : 0.0F);
     
     drawFullscreenQuad();
     
     RenderSystem.enableDepthTest();
     RenderSystem.disableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.setShaderTexture(0, 0);
   }
   
   private void setUniform(ShaderProgram shader, String name, float value) {
     GlUniform uniform = shader.method_34582(name);
     if (uniform != null) uniform.method_1251(value); 
   }
   
   private void setUniform(ShaderProgram shader, String name, float x, float y) {
     GlUniform uniform = shader.method_34582(name);
     if (uniform != null) uniform.method_1255(x, y); 
   }
   
   private void setUniform(ShaderProgram shader, String name, float x, float y, float z) {
     GlUniform uniform = shader.method_34582(name);
     if (uniform != null) uniform.method_1249(x, y, z); 
   }
   
   private void ensureMaskBuffer() {
     int w = mc.method_22683().method_4489();
     int h = mc.method_22683().method_4506();
     if (this.maskBuffer == null || this.fbWidth != w || this.fbHeight != h) {
       if (this.maskBuffer != null) {
         this.maskBuffer.method_1238();
       }
       this.maskBuffer = (Framebuffer)new SimpleFramebuffer(w, h, true);
       this.fbWidth = w;
       this.fbHeight = h;
     } 
   }
   
   private Box getTargetedBlockBox() {
     HitResult hit = mc.field_1765;
     if (hit instanceof BlockHitResult) { BlockHitResult blockHit = (BlockHitResult)hit; if (hit.method_17783() == HitResult.class_240.field_1332) {
 
 
         
         BlockPos pos = blockHit.method_17777();
         if (pos == null) return null; 
         if (mc.field_1687.method_8320(pos).method_26215()) return null;
         
         VoxelShape shape = mc.field_1687.method_8320(pos).method_26218((BlockView)mc.field_1687, pos);
         Box box = shape.method_1110() ? new Box(pos) : shape.method_1107().method_996(pos);
         return box.method_1014(0.002D);
       }  }
     
     return null; } private Box lerpBox(Box a, Box b, float t) {
     return new Box(a.field_1323 + (b.field_1323 - a.field_1323) * t, a.field_1322 + (b.field_1322 - a.field_1322) * t, a.field_1321 + (b.field_1321 - a.field_1321) * t, a.field_1320 + (b.field_1320 - a.field_1320) * t, a.field_1325 + (b.field_1325 - a.field_1325) * t, a.field_1324 + (b.field_1324 - a.field_1324) * t);
   }
 
 
 
 
 
 
 
   
   private void drawAnimatedWeb(Matrix4f matrix, Box box) {
     int strandsPerFace = 5;
     int samples = 18;
     float t = (float)(System.currentTimeMillis() % 100000L) / 1000.0F * this.lineSpeed.get();
     float lineWidth = 0.0025F;
     float bendBase = 0.06F + this.lineJitter.get() * 0.2F;
     int baseAlpha = Math.max(20, Math.min(255, (int)(this.alpha.get() * 210.0F)));
     int themeColor = this.cachedThemeColor1;
     long seed = (this.lastBlockPos != null) ? this.lastBlockPos.method_10063() : 1L;
     
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableCull();
     RenderSystem.enableDepthTest();
     RenderSystem.depthMask(false);
     RenderSystem.setShader(ShaderProgramKeys.field_53876);
     drawFilledBox(matrix, box, ColorUtils.setAlphaColor(themeColor, (int)(this.alpha.get() * this.fill.get() * 170.0F)));
     
     for (int face = 0; face < 6; face++) {
       int[] neighbors = faceNeighbors(face);
       for (int strand = 0; strand < strandsPerFace; strand++) {
         int key = face * 1000 + strand * 53;
         int adj = neighbors[strand % neighbors.length];
         double phase = t * (0.95D + rand01(seed, key + 1) * 0.55D) + strand * 0.83D + face * 1.11D;
         double edgeT = clamp01(0.5D + Math.sin(phase * 1.37D + rand01(seed, key + 2) * 6.2831853D) * 0.38D);
         
         Vec3d pivot = edgePoint(box, face, adj, edgeT, 0.0015D);
         Vec3d start = facePoint(box, face, 
             clamp01(0.5D + (rand01(seed, key + 3) - 0.5D) * 0.46D), 
             clamp01(0.5D + (rand01(seed, key + 4) - 0.5D) * 0.46D), 0.0015D);
         
         Vec3d end = facePoint(box, adj, 
             clamp01(0.5D + (rand01(seed, key + 5) - 0.5D) * 0.46D), 
             clamp01(0.5D + (rand01(seed, key + 6) - 0.5D) * 0.46D), 0.0015D);
 
         
         Vec3d[] basisA = faceBasis(face);
         Vec3d[] basisB = faceBasis(adj);
         Vec3d normalA = faceNormal(face);
         Vec3d normalB = faceNormal(adj);
         
         double bendA = bendBase * (0.7D + rand01(seed, key + 7)) * Math.sin(phase * 1.9D + rand01(seed, key + 8) * 6.2831853D);
         
         double bendB = bendBase * (0.7D + rand01(seed, key + 9)) * Math.cos(phase * 1.7D + rand01(seed, key + 10) * 6.2831853D);
         
         Vec3d dirA = pivot.method_1020(start);
         Vec3d c1a = start.method_1019(dirA.method_1021(0.38D)).method_1019(basisA[0].method_1021(bendA)).method_1019(basisA[1].method_1021(-bendA * 0.55D));
         Vec3d c2a = start.method_1019(dirA.method_1021(0.76D)).method_1019(basisA[0].method_1021(-bendA * 0.65D)).method_1019(basisA[1].method_1021(bendA * 0.4D));
         
         Vec3d dirB = end.method_1020(pivot);
         Vec3d c1b = pivot.method_1019(dirB.method_1021(0.24D)).method_1019(basisB[0].method_1021(bendB)).method_1019(basisB[1].method_1021(bendB * 0.45D));
         Vec3d c2b = pivot.method_1019(dirB.method_1021(0.62D)).method_1019(basisB[0].method_1021(-bendB * 0.7D)).method_1019(basisB[1].method_1021(-bendB * 0.35D));
         
         int alphaLine = Math.max(18, Math.min(255, (int)(baseAlpha * (0.74D + 0.26D * Math.sin(phase * 2.6D)))));
         int color = ColorUtils.setAlphaColor(themeColor, alphaLine);
         drawBezierRibbon(matrix, start, c1a, c2a, pivot, normalA, samples, color, lineWidth);
         drawBezierRibbon(matrix, pivot, c1b, c2b, end, normalB, samples, color, lineWidth);
       } 
     } 
     
     RenderSystem.enableDepthTest();
     RenderSystem.depthMask(true);
     RenderSystem.enableCull();
     RenderSystem.disableBlend();
   }
   
   private void copyMainDepthToMask() {
     if (this.maskBuffer == null)
       return; 
     int readFbo = GL11.glGetInteger(36010);
     int drawFbo = GL11.glGetInteger(36006);
     int w = mc.method_22683().method_4489();
     int h = mc.method_22683().method_4506();
     
     GL30.glBindFramebuffer(36008, (mc.method_1522()).field_1476);
     GL30.glBindFramebuffer(36009, this.maskBuffer.field_1476);
     GL30.glBlitFramebuffer(0, 0, w, h, 0, 0, w, h, 256, 9728);
     GL30.glBindFramebuffer(36008, readFbo);
     GL30.glBindFramebuffer(36009, drawFbo);
   }
   
   private Vec3d cubicBezier(Vec3d p0, Vec3d p1, Vec3d p2, Vec3d p3, float t) {
     double it = 1.0D - t;
     double it2 = it * it;
     double t2 = (t * t);
     return p0.method_1021(it2 * it)
       .method_1019(p1.method_1021(3.0D * it2 * t))
       .method_1019(p2.method_1021(3.0D * it * t2))
       .method_1019(p3.method_1021(t2 * t));
   }
   
   private void drawBezierRibbon(Matrix4f matrix, Vec3d p0, Vec3d p1, Vec3d p2, Vec3d p3, Vec3d faceNormal, int samples, int color, float halfWidth) {
     Vec3d[] points = new Vec3d[samples + 1];
     for (int s = 0; s <= samples; s++) {
       float u = s / samples;
       points[s] = cubicBezier(p0, p1, p2, p3, u);
     } 
     
     BufferBuilder quads = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1576);
     for (int i = 0; i < samples; i++) {
       Vec3d a = points[i];
       Vec3d b = points[i + 1];
       Vec3d dir = b.method_1020(a);
       if (dir.method_1027() >= 1.0E-6D) {
         
         Vec3d perp = faceNormal.method_1036(dir).method_1029().method_1021(halfWidth);
         Vec3d aL = a.method_1019(perp);
         Vec3d aR = a.method_1020(perp);
         Vec3d bL = b.method_1019(perp);
         Vec3d bR = b.method_1020(perp);
         
         quads.method_22918(matrix, (float)aL.field_1352, (float)aL.field_1351, (float)aL.field_1350).method_39415(color);
         quads.method_22918(matrix, (float)aR.field_1352, (float)aR.field_1351, (float)aR.field_1350).method_39415(color);
         quads.method_22918(matrix, (float)bR.field_1352, (float)bR.field_1351, (float)bR.field_1350).method_39415(color);
         quads.method_22918(matrix, (float)bL.field_1352, (float)bL.field_1351, (float)bL.field_1350).method_39415(color);
       } 
     }  BufferRenderer.method_43433(quads.method_60800());
   }
   
   private void updateCachedThemeColors() {
     if (NazunaClient.INSTANCE == null || NazunaClient.INSTANCE.themeStorage == null || NazunaClient.INSTANCE.themeStorage.getThemes() == null) {
       this.cachedThemeColor1 = ColorUtils.getThemeColor(0);
       this.cachedThemeColor2 = ColorUtils.getThemeColor(180);
       
       return;
     } 
     Theme theme = NazunaClient.INSTANCE.themeStorage.getThemes().getTheme();
     if (theme == null) {
       this.cachedThemeColor1 = ColorUtils.getThemeColor(0);
       this.cachedThemeColor2 = ColorUtils.getThemeColor(180);
       
       return;
     } 
     if (!"Rainbow".equals(theme.getName())) {
       int base = (theme.color != null && theme.color.length > 0) ? theme.color[0] : ColorUtils.getThemeColor(0);
       this.cachedThemeColor1 = base;
       this.cachedThemeColor2 = base;
     } else {
       this.cachedThemeColor1 = ColorUtils.getThemeColor();
       this.cachedThemeColor2 = ColorUtils.getThemeColor(180);
     } 
   }
   
   private int[] faceNeighbors(int face) {
     switch (face) { case 0: case 1:
         (new int[4])[0] = 2; (new int[4])[1] = 3; (new int[4])[2] = 4; (new int[4])[3] = 5;
       case 2: case 3: (new int[4])[0] = 0; (new int[4])[1] = 1; (new int[4])[2] = 4; (new int[4])[3] = 5; }
      return new int[] { 0, 1, 2, 3 };
   }
 
   
   private Vec3d[] faceBasis(int face) {
     switch (face) { case 0: case 1:
         (new Vec3d[2])[0] = new Vec3d(1.0D, 0.0D, 0.0D); (new Vec3d[2])[1] = new Vec3d(0.0D, 0.0D, 1.0D);
       case 2: case 3: (new Vec3d[2])[0] = new Vec3d(1.0D, 0.0D, 0.0D); (new Vec3d[2])[1] = new Vec3d(0.0D, 1.0D, 0.0D); }
      return new Vec3d[] { new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(0.0D, 1.0D, 0.0D) };
   }
 
   
   private Vec3d faceNormal(int face) {
     switch (face) { case 0: case 1: case 2: case 3: case 4:  }  return 
 
 
 
 
       
       new Vec3d(1.0D, 0.0D, 0.0D);
   }
 
   
   private Vec3d edgePoint(Box box, int faceA, int faceB, double t, double inset) {
     double x = Double.NaN;
     double y = Double.NaN;
     double z = Double.NaN;
     
     double[] fixedA = faceFixedCoords(box, faceA, inset);
     if (!Double.isNaN(fixedA[0])) x = fixedA[0]; 
     if (!Double.isNaN(fixedA[1])) y = fixedA[1]; 
     if (!Double.isNaN(fixedA[2])) z = fixedA[2];
     
     double[] fixedB = faceFixedCoords(box, faceB, inset);
     if (!Double.isNaN(fixedB[0])) x = fixedB[0]; 
     if (!Double.isNaN(fixedB[1])) y = fixedB[1]; 
     if (!Double.isNaN(fixedB[2])) z = fixedB[2];
     
     double tt = clamp01(t);
     if (Double.isNaN(x)) x = lerp(box.field_1323, box.field_1320, tt); 
     if (Double.isNaN(y)) y = lerp(box.field_1322, box.field_1325, tt); 
     if (Double.isNaN(z)) z = lerp(box.field_1321, box.field_1324, tt); 
     return new Vec3d(x, y, z);
   }
   
   private double[] faceFixedCoords(Box box, int face, double inset) {
     switch (face) { case 0:
         (new double[3])[0] = Double.NaN; (new double[3])[1] = box.field_1325 - inset; (new double[3])[2] = Double.NaN;
       case 1: (new double[3])[0] = Double.NaN; (new double[3])[1] = box.field_1322 + inset; (new double[3])[2] = Double.NaN;
       case 2: (new double[3])[0] = Double.NaN; (new double[3])[1] = Double.NaN; (new double[3])[2] = box.field_1321 + inset;
       case 3: (new double[3])[0] = Double.NaN; (new double[3])[1] = Double.NaN; (new double[3])[2] = box.field_1324 - inset;
       case 4: (new double[3])[0] = box.field_1323 + inset; (new double[3])[1] = Double.NaN; (new double[3])[2] = Double.NaN; }
      return new double[] { box.field_1320 - inset, Double.NaN, Double.NaN };
   }
 
   
   private Vec3d facePoint(Box box, int face, double u, double v, double inset) {
     u = clamp01(u);
     v = clamp01(v);
     switch (face) { case 0: case 1: case 2: case 3: case 4:  }  return 
 
 
 
 
       
       new Vec3d(box.field_1320 - inset, lerp(box.field_1322, box.field_1325, v), lerp(box.field_1321, box.field_1324, u));
   }
 
   
   private double rand01(long seed, int salt) {
     long x = seed + -7046029254386353131L * (salt + 1L);
     x ^= x >>> 30L;
     x *= -4658895280553007687L;
     x ^= x >>> 27L;
     x *= -7723592293110705685L;
     x ^= x >>> 31L;
     return (x & 0xFFFFFFL) / 1.6777216E7D;
   }
   
   private double lerp(double a, double b, double t) {
     return a + (b - a) * t;
   }
   
   private double clamp01(double v) {
     return Math.max(0.0D, Math.min(1.0D, v));
   }
   
   private void drawFilledBox(Matrix4f matrix, Box box, int color) {
     BufferBuilder b = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1576);
 
     
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1322, (float)box.field_1321).method_39415(color);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1322, (float)box.field_1324).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1322, (float)box.field_1324).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1322, (float)box.field_1321).method_39415(color);
     
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1325, (float)box.field_1321).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1325, (float)box.field_1321).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1325, (float)box.field_1324).method_39415(color);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1325, (float)box.field_1324).method_39415(color);
     
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1322, (float)box.field_1321).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1322, (float)box.field_1321).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1325, (float)box.field_1321).method_39415(color);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1325, (float)box.field_1321).method_39415(color);
     
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1322, (float)box.field_1324).method_39415(color);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1325, (float)box.field_1324).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1325, (float)box.field_1324).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1322, (float)box.field_1324).method_39415(color);
     
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1322, (float)box.field_1321).method_39415(color);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1325, (float)box.field_1321).method_39415(color);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1325, (float)box.field_1324).method_39415(color);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1322, (float)box.field_1324).method_39415(color);
     
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1322, (float)box.field_1321).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1322, (float)box.field_1324).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1325, (float)box.field_1324).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1325, (float)box.field_1321).method_39415(color);
     
     BufferRenderer.method_43433(b.method_60800());
   }
   
   private void drawMaskBox(Matrix4f matrix, Box box) {
     BufferBuilder b = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1576);
     int white = -1;
 
     
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1322, (float)box.field_1321).method_39415(white);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1322, (float)box.field_1324).method_39415(white);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1322, (float)box.field_1324).method_39415(white);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1322, (float)box.field_1321).method_39415(white);
     
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1325, (float)box.field_1321).method_39415(white);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1325, (float)box.field_1321).method_39415(white);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1325, (float)box.field_1324).method_39415(white);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1325, (float)box.field_1324).method_39415(white);
     
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1322, (float)box.field_1321).method_39415(white);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1322, (float)box.field_1321).method_39415(white);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1325, (float)box.field_1321).method_39415(white);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1325, (float)box.field_1321).method_39415(white);
     
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1322, (float)box.field_1324).method_39415(white);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1325, (float)box.field_1324).method_39415(white);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1325, (float)box.field_1324).method_39415(white);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1322, (float)box.field_1324).method_39415(white);
     
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1322, (float)box.field_1321).method_39415(white);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1325, (float)box.field_1321).method_39415(white);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1325, (float)box.field_1324).method_39415(white);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1322, (float)box.field_1324).method_39415(white);
     
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1322, (float)box.field_1321).method_39415(white);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1322, (float)box.field_1324).method_39415(white);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1325, (float)box.field_1324).method_39415(white);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1325, (float)box.field_1321).method_39415(white);
     
     BufferRenderer.method_43433(b.method_60800());
   }
   
   private void drawFullscreenQuad() {
     BufferBuilder b = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     float width = Math.max(mc.method_22683().method_4486(), 1);
     float height = Math.max(mc.method_22683().method_4502(), 1);
     b.method_22912(0.0F, 0.0F, 0.0F).method_22913(0.0F, 1.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     b.method_22912(0.0F, height, 0.0F).method_22913(0.0F, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     b.method_22912(width, height, 0.0F).method_22913(1.0F, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     b.method_22912(width, 0.0F, 0.0F).method_22913(1.0F, 1.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     BufferRenderer.method_43433(b.method_60800());
   }
 }

