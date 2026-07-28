package shame.nazuna.api.utils.render.hands;
 
 import com.mojang.blaze3d.platform.GlStateManager;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.util.ArrayList;
 import java.util.List;
 import net.minecraft.Framebuffer;
 import net.minecraft.GlUniform;
 import net.minecraft.BufferRenderer;
 import net.minecraft.BufferBuilder;
 import net.minecraft.Tessellator;
 import net.minecraft.VertexFormats;
 import net.minecraft.VertexFormat;
 import net.minecraft.ShaderProgram;
 import net.minecraft.SimpleFramebuffer;
 import org.lwjgl.opengl.GL11;
 import org.lwjgl.opengl.GL30;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.render.ShaderUtils;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.impl.render.ShaderHands;
 
 
 
 public class ShaderHandsRenderer
   implements QClient
 {
   private static final float EPSILON = 0.001F;
   private static ShaderHandsRenderer instance;
   private Framebuffer beforeBuffer;
   private Framebuffer afterBuffer;
   private Framebuffer maskBuffer;
   private final List<Framebuffer> bloomBuffers = new ArrayList<>();
   private int width = -1;
   private int height = -1;
   private boolean hasBeforeCapture;
   private boolean pendingComposite;
   private int configuredBeforeDepthTex = -1;
   private int configuredAfterDepthTex = -1;
   
   public static ShaderHandsRenderer getInstance() {
     if (instance == null) instance = new ShaderHandsRenderer(); 
     return instance;
   }
   
   public void captureBeforeHands() {
     ShaderHands module = getModule();
     if (!isEffectEnabled(module)) {
       invalidateState();
       return;
     } 
     ensureBuffers();
     if (this.beforeBuffer == null)
       return;  copyMainFramebuffer(this.beforeBuffer);
     this.hasBeforeCapture = true;
   }
   
   public void captureAfterHands() {
     ShaderHands module = getModule();
     if (!isEffectEnabled(module)) {
       invalidateState();
       return;
     } 
     ensureBuffers();
     if (this.beforeBuffer == null || this.afterBuffer == null || this.maskBuffer == null)
       return;  if (!this.hasBeforeCapture)
       return; 
     copyMainFramebuffer(this.afterBuffer);
     this.pendingComposite = true;
   }
   
   public void renderOverlayIfPending() {
     if (!this.pendingComposite)
       return;  ensureBuffers();
     if (this.beforeBuffer == null || this.afterBuffer == null || this.maskBuffer == null)
       return;  ShaderHands module = getModule();
     if (!isEffectEnabled(module)) {
       invalidateState();
       
       return;
     } 
     ShaderProgram maskShader = mc.method_62887().method_62947(ShaderUtils.shaderHandsMaskDiff);
     if (maskShader == null) {
       invalidateState();
       return;
     } 
     this.maskBuffer.method_1236(0.0F, 0.0F, 0.0F, 0.0F);
     this.maskBuffer.method_1230();
     this.maskBuffer.method_1235(false);
     RenderSystem.disableDepthTest();
     RenderSystem.disableBlend();
     RenderSystem.setShader(ShaderUtils.shaderHandsMaskDiff);
     RenderSystem.setShaderTexture(0, this.beforeBuffer.method_30277());
     RenderSystem.setShaderTexture(1, this.afterBuffer.method_30277());
     int beforeDepth = this.beforeBuffer.method_30278();
     int afterDepth = this.afterBuffer.method_30278();
     if (beforeDepth != 0 && beforeDepth != this.configuredBeforeDepthTex) {
       configureDepthTexture(beforeDepth);
       this.configuredBeforeDepthTex = beforeDepth;
     } 
     if (afterDepth != 0 && afterDepth != this.configuredAfterDepthTex) {
       configureDepthTexture(afterDepth);
       this.configuredAfterDepthTex = afterDepth;
     } 
     RenderSystem.setShaderTexture(2, beforeDepth);
     RenderSystem.setShaderTexture(3, afterDepth);
     drawFullscreenQuad();
     RenderSystem.enableDepthTest();
     
     float glowValue = module.glow.get();
     float fillValue = module.fill.get();
     float alphaValue = module.alpha.get();
     float outlineValue = module.outline.get();
     
     boolean hasGlow = (glowValue > 0.001F);
     boolean hasFill = (fillValue > 0.001F && alphaValue > 0.001F);
 
     
     int color1 = NazunaClient.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow") ? ColorUtils.getThemeColor(0) : (NazunaClient.INSTANCE.themeStorage.getThemes().getTheme()).color[0];
     int color2 = color1;
     
     if (module.mode.is("Красивый")) {
       renderPrettyMode(module, color1, color2, glowValue, fillValue, alphaValue, outlineValue);
       invalidateState();
       
       return;
     } 
     int blurredMaskTexture = 0;
     if (hasGlow) {
       int iterations = Math.max(3, Math.min(8, 4 + Math.round(outlineValue * 0.7F)));
       blurredMaskTexture = runKawaseBloom(iterations);
     } 
     
     mc.method_1522().method_1235(true);
     RenderSystem.enableBlend();
     RenderSystem.colorMask(true, true, true, false);
     RenderSystem.disableDepthTest();
     
     ShaderProgram glowShader = hasGlow ? mc.method_62887().method_62947(ShaderUtils.shaderHandsGlow) : null;
     if (glowShader != null) {
       RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE, GlStateManager.class_4535.ZERO, GlStateManager.class_4534.ONE);
 
 
 
 
       
       RenderSystem.setShader(ShaderUtils.shaderHandsGlow);
       RenderSystem.setShaderTexture(0, blurredMaskTexture);
       RenderSystem.setShaderTexture(1, this.maskBuffer.method_30277());
       setUniform(glowShader, "color", ColorUtils.redf(color1), ColorUtils.greenf(color1), ColorUtils.bluef(color1));
       setUniform(glowShader, "color2", ColorUtils.redf(color2), ColorUtils.greenf(color2), ColorUtils.bluef(color2));
       setUniform(glowShader, "exposure", 1.0F + glowValue * 1.8F);
       drawFullscreenQuad();
     } 
     
     if (hasFill) {
       ShaderProgram overlayShader = mc.method_62887().method_62947(ShaderUtils.shaderHandsOverlay);
       if (overlayShader == null) {
         restoreCompositeState();
         invalidateState();
         return;
       } 
       RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ZERO, GlStateManager.class_4534.ONE);
 
 
 
 
       
       RenderSystem.setShader(ShaderUtils.shaderHandsOverlay);
       RenderSystem.setShaderTexture(0, this.maskBuffer.method_30277());
       setUniform(overlayShader, "color", ColorUtils.redf(color1), ColorUtils.greenf(color1), ColorUtils.bluef(color1));
       setUniform(overlayShader, "fill", fillValue);
       setUniform(overlayShader, "alpha", alphaValue);
       drawFullscreenQuad();
     } 
     
     restoreCompositeState();
     invalidateState();
   }
   
   public void invalidateState() {
     this.hasBeforeCapture = false;
     this.pendingComposite = false;
     this.configuredBeforeDepthTex = -1;
     this.configuredAfterDepthTex = -1;
   }
   
   private int runKawaseBloom(int iterations) {
     ensureBloomBuffers(iterations);
     if (this.bloomBuffers.isEmpty()) {
       return this.maskBuffer.method_30277();
     }
     
     int currentTexture = this.maskBuffer.method_30277();
     ShaderProgram downShader = mc.method_62887().method_62947(ShaderUtils.shaderHandsKawaseDown);
     ShaderProgram upShader = mc.method_62887().method_62947(ShaderUtils.shaderHandsKawaseUp);
     if (downShader == null || upShader == null) {
       return currentTexture;
     }
     int i;
     for (i = 0; i < iterations; i++) {
       Framebuffer dst = this.bloomBuffers.get(i);
       dst.method_1236(0.0F, 0.0F, 0.0F, 0.0F);
       dst.method_1230();
       dst.method_1235(true);
       
       RenderSystem.setShader(ShaderUtils.shaderHandsKawaseDown);
       RenderSystem.setShaderTexture(0, currentTexture);
       setHandsKawaseUniforms(downShader, dst.field_1482, dst.field_1481, 1.0F + i);
       drawFullscreenQuad();
       
       currentTexture = dst.method_30277();
     } 
     
     for (i = iterations - 1; i >= 1; i--) {
       Framebuffer dst = this.bloomBuffers.get(i - 1);
       dst.method_1236(0.0F, 0.0F, 0.0F, 0.0F);
       dst.method_1230();
       dst.method_1235(true);
       
       RenderSystem.setShader(ShaderUtils.shaderHandsKawaseUp);
       RenderSystem.setShaderTexture(0, currentTexture);
       setHandsKawaseUniforms(upShader, dst.field_1482, dst.field_1481, 1.0F + i);
       setUniform(upShader, "color", 1.0F, 1.0F, 1.0F);
       drawFullscreenQuad();
       
       currentTexture = dst.method_30277();
     } 
     
     mc.method_1522().method_1235(true);
     return currentTexture;
   }
   
   private void copyMainFramebuffer(Framebuffer target) {
     int readFbo = GL11.glGetInteger(36010);
     int drawFbo = GL11.glGetInteger(36006);
     
     GL30.glBindFramebuffer(36008, (mc.method_1522()).field_1476);
     GL30.glBindFramebuffer(36009, target.field_1476);
     
     GL30.glBlitFramebuffer(0, 0, this.width, this.height, 0, 0, this.width, this.height, 16640, 9728);
 
 
 
 
 
     
     GL30.glBindFramebuffer(36008, readFbo);
     GL30.glBindFramebuffer(36009, drawFbo);
     mc.method_1522().method_1235(true);
   }
   
   private void configureDepthTexture(int depthTex) {
     RenderSystem.bindTexture(depthTex);
     GL11.glTexParameteri(3553, 34892, 0);
     GL11.glTexParameteri(3553, 10241, 9728);
     GL11.glTexParameteri(3553, 10240, 9728);
     RenderSystem.bindTexture(0);
   }
   
   private void ensureBuffers() {
     int w = mc.method_22683().method_4489();
     int h = mc.method_22683().method_4506();
     if (w == this.width && h == this.height && this.beforeBuffer != null && this.afterBuffer != null && this.maskBuffer != null)
       return; 
     if (this.beforeBuffer != null) this.beforeBuffer.method_1238(); 
     if (this.afterBuffer != null) this.afterBuffer.method_1238(); 
     if (this.maskBuffer != null) this.maskBuffer.method_1238(); 
     for (Framebuffer fb : this.bloomBuffers) {
       fb.method_1238();
     }
     this.bloomBuffers.clear();
     
     this.beforeBuffer = (Framebuffer)new SimpleFramebuffer(w, h, true);
     this.afterBuffer = (Framebuffer)new SimpleFramebuffer(w, h, true);
     this.maskBuffer = (Framebuffer)new SimpleFramebuffer(w, h, true);
     this.width = w;
     this.height = h;
     this.configuredBeforeDepthTex = -1;
     this.configuredAfterDepthTex = -1;
   }
   
   private void ensureBloomBuffers(int iterations) {
     while (this.bloomBuffers.size() > iterations) {
       int last = this.bloomBuffers.size() - 1;
       ((Framebuffer)this.bloomBuffers.get(last)).method_1238();
       this.bloomBuffers.remove(last);
     } 
     
     for (int i = 0; i < iterations; i++) {
       int w = Math.max(2, this.width >> i + 1);
       int h = Math.max(2, this.height >> i + 1);
       
       if (i >= this.bloomBuffers.size()) {
         SimpleFramebuffer SimpleFramebuffer = new SimpleFramebuffer(w, h, false);
         setLinearFiltering((Framebuffer)SimpleFramebuffer);
         this.bloomBuffers.add(SimpleFramebuffer);
       }
       else {
         
         Framebuffer fb = this.bloomBuffers.get(i);
         if (fb.field_1482 != w || fb.field_1481 != h) {
           fb.method_1238();
           SimpleFramebuffer SimpleFramebuffer = new SimpleFramebuffer(w, h, false);
           setLinearFiltering((Framebuffer)SimpleFramebuffer);
           this.bloomBuffers.set(i, SimpleFramebuffer);
         } 
       } 
     } 
   }
   private void setLinearFiltering(Framebuffer fb) {
     RenderSystem.bindTexture(fb.method_30277());
     GL11.glTexParameteri(3553, 10241, 9729);
     GL11.glTexParameteri(3553, 10240, 9729);
     RenderSystem.bindTexture(0);
   }
   
   private ShaderHands getModule() {
     if (NazunaClient.INSTANCE == null || ModuleClass.INSTANCE == null) return null; 
     return ModuleClass.shaderHands;
   }
   
   private void renderPrettyMode(ShaderHands module, int color1, int color2, float glowValue, float fillValue, float alphaValue, float outlineValue) {
     ShaderProgram shader = mc.method_62887().method_62947(ShaderUtils.blockOverlay);
     if (shader == null)
       return; 
     mc.method_1522().method_1235(false);
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableDepthTest();
     
     RenderSystem.setShader(ShaderUtils.blockOverlay);
     RenderSystem.setShaderTexture(0, this.maskBuffer.method_30277());
     
     setUniform(shader, "texelSize", 1.0F / 
         Math.max(1, mc.method_22683().method_4489()), 1.0F / 
         Math.max(1, mc.method_22683().method_4506()));
     setUniform(shader, "color", ColorUtils.redf(color1), ColorUtils.greenf(color1), ColorUtils.bluef(color1));
     setUniform(shader, "color2", ColorUtils.redf(color2), ColorUtils.greenf(color2), ColorUtils.bluef(color2));
     setUniform(shader, "time", (float)(System.currentTimeMillis() % 100000L) / 1000.0F);
     setUniform(shader, "speed", module.waveSpeed.get());
     setUniform(shader, "scale", module.waveScale.get());
     setUniform(shader, "outline", outlineValue);
     setUniform(shader, "glow", glowValue);
     setUniform(shader, "fill", fillValue);
     setUniform(shader, "alpha", alphaValue);
     setUniform(shader, "outlineOnly", 0.0F);
     drawFullscreenQuad();
     
     RenderSystem.enableDepthTest();
     RenderSystem.disableBlend();
     RenderSystem.defaultBlendFunc();
     restoreCompositeState();
   }
   
   private void restoreCompositeState() {
     RenderSystem.colorMask(true, true, true, true);
     RenderSystem.depthMask(true);
     RenderSystem.enableDepthTest();
     RenderSystem.enableCull();
     RenderSystem.disableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
     RenderSystem.setShaderTexture(0, 0);
     RenderSystem.setShaderTexture(1, 0);
     RenderSystem.setShaderTexture(2, 0);
     RenderSystem.setShaderTexture(3, 0);
     mc.method_1522().method_1235(true);
   }
   
   private boolean isEffectEnabled(ShaderHands module) {
     if (module == null || !module.isEnable()) return false; 
     boolean hasGlow = (module.glow.get() > 0.001F);
     boolean hasFill = (module.fill.get() > 0.001F && module.alpha.get() > 0.001F);
     return (hasGlow || hasFill);
   }
   
   private void setUniform(ShaderProgram shader, String name, float v) {
     GlUniform u = shader.method_34582(name);
     if (u != null) u.method_1251(v); 
   }
   
   private void setUniform(ShaderProgram shader, String name, float x, float y) {
     GlUniform u = shader.method_34582(name);
     if (u != null) u.method_1255(x, y); 
   }
   
   private void setUniform(ShaderProgram shader, String name, float x, float y, float z) {
     GlUniform u = shader.method_34582(name);
     if (u != null) u.method_1249(x, y, z); 
   }
   
   private void setHandsKawaseUniforms(ShaderProgram shader, int texWidth, int texHeight, float offset) {
     setUniform(shader, "uSize", Math.max(1, texWidth), Math.max(1, texHeight));
     setUniform(shader, "uOffset", offset, offset);
     setUniform(shader, "uHalfPixel", 0.5F / Math.max(1, texWidth), 0.5F / Math.max(1, texHeight));
   }
   
   private void drawFullscreenQuad() {
     float sw = Math.max(mc.method_22683().method_4486(), 1);
     float sh = Math.max(mc.method_22683().method_4502(), 1);
     BufferBuilder b = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     b.method_22912(0.0F, 0.0F, 0.0F).method_22913(0.0F, 1.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     b.method_22912(0.0F, sh, 0.0F).method_22913(0.0F, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     b.method_22912(sw, sh, 0.0F).method_22913(1.0F, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     b.method_22912(sw, 0.0F, 0.0F).method_22913(1.0F, 1.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     BufferRenderer.method_43433(b.method_60800());
   }
 }

