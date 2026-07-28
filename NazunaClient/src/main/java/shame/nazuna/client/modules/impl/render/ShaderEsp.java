package shame.nazuna.client.modules.impl.render;
 
 import com.mojang.blaze3d.platform.GlStateManager;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.util.ArrayList;
 import java.util.List;
 import net.minecraft.Entity;
 import net.minecraft.Framebuffer;
 import net.minecraft.GlUniform;
 import net.minecraft.BufferRenderer;
 import net.minecraft.BufferBuilder;
 import net.minecraft.Tessellator;
 import net.minecraft.VertexFormats;
 import net.minecraft.VertexFormat;
 import net.minecraft.ShaderProgram;
 import net.minecraft.SimpleFramebuffer;
 import net.minecraft.WorldRenderer;
 import org.lwjgl.opengl.GL11;
 import org.lwjgl.opengl.GL30;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.render.ShaderUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.ListSetting;
 import shame.nazuna.mixin.WorldRendererAccessor;
 
 
 
 public class ShaderEsp
   extends Module
 {
   public static ShaderEsp INSTANCE = new ShaderEsp();
   
   private static final float EPSILON = 0.001F;
   private static final long OUTLINE_RETRY_DELAY_MS = 3000L;
   private static final double MAX_RANGE = 256.0D;
   private static final float FILL_ALPHA = 0.7F;
   private static final int FILL_MIN_ITERATIONS = 2;
   private static final float GLOW_VALUE = 0.55F;
   private static final float WIDTH_VALUE = 0.9F;
   private final ListSetting targets = new ListSetting("Цели", new BooleanSetting[] { new BooleanSetting("Игроки", true), new BooleanSetting("Кристаллы", true), new BooleanSetting("Предметы", false), new BooleanSetting("Себя", false) });
 
 
 
 
 
   
   private final BooleanSetting fill = new BooleanSetting("Заливка", false);
   
   private final List<Framebuffer> bloomBuffers = new ArrayList<>();
   private Framebuffer depthCopyBuffer;
   private int bloomWidth = -1;
   private int bloomHeight = -1;
   private boolean outlineReady;
   private boolean hasOutlineTargetsCached;
   private long nextOutlineRetryAt;
   
   public ShaderEsp() {
     super("ShaderESP", "Красивая обводка энтити", Module.ModuleCategory.RENDER);
     addSettings(new Setting[] { (Setting)this.targets, (Setting)this.fill });
   }
 
   
   public void onEnable() {
     super.onEnable();
     this.outlineReady = false;
     this.nextOutlineRetryAt = 0L;
     tryEnsureOutlineProcessor();
   }
 
   
   public void onDisable() {
     for (Framebuffer fb : this.bloomBuffers) {
       fb.method_1238();
     }
     this.bloomBuffers.clear();
     if (this.depthCopyBuffer != null) {
       this.depthCopyBuffer.method_1238();
       this.depthCopyBuffer = null;
     } 
     this.bloomWidth = -1;
     this.bloomHeight = -1;
     this.outlineReady = false;
     this.hasOutlineTargetsCached = false;
     this.nextOutlineRetryAt = 0L;
     super.onDisable();
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (!isEnable())
       return;  if (mc.field_1687 == null || mc.field_1769 == null) {
       this.outlineReady = false;
       this.hasOutlineTargetsCached = false;
       return;
     } 
     this.hasOutlineTargetsCached = hasOutlineTargets();
     if (!this.hasOutlineTargetsCached) {
       this.outlineReady = false;
       return;
     } 
     if (!this.outlineReady && System.currentTimeMillis() >= this.nextOutlineRetryAt) {
       tryEnsureOutlineProcessor();
     }
   }
   
   @EventLink(priority = 200)
   public void onRender2D(EventRender.Default event) {
     if (!isEnable() || mc.field_1687 == null || mc.field_1724 == null || mc.field_1769 == null)
       return;  boolean hasGlow = true;
     boolean hasFill = this.fill.isState();
     if (!hasGlow && !hasFill)
       return;  if (!this.hasOutlineTargetsCached)
       return;  if (!tryEnsureOutlineProcessor())
       return; 
     Framebuffer outlineBuffer = getOutlineSourceFramebuffer();
     if (outlineBuffer == null || outlineBuffer.method_30277() == 0)
       return; 
     Framebuffer mainBuffer = mc.method_1522();
     
     ensureDepthCopyBuffer(mainBuffer.field_1482, mainBuffer.field_1481);
     
     int iterations = Math.max(1, Math.min(8, (int)Math.ceil(1.125D)));
     int fillTexture = 0;
     if (hasFill) {
       int fillIterations = Math.max(2, Math.min(6, iterations + 1));
       fillTexture = runKawaseBloom(outlineBuffer.method_30277(), fillIterations);
     } 
 
     
     int blurredTexture = hasGlow ? runKawaseBloom(outlineBuffer.method_30277(), iterations) : fillTexture;
     int color = getOutlineColor();
     
     mainBuffer.method_1235(false);
     RenderSystem.enableBlend();
     RenderSystem.disableDepthTest();
     RenderSystem.colorMask(true, true, true, false);
     
     if (hasFill) {
       ShaderProgram fillShader = mc.method_62887().method_62947(ShaderUtils.shaderEspFill);
       if (fillShader != null) {
         RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ZERO, GlStateManager.class_4534.ONE);
 
 
 
 
         
         RenderSystem.setShader(ShaderUtils.shaderEspFill);
         RenderSystem.setShaderTexture(0, outlineBuffer.method_30277());
         RenderSystem.setShaderTexture(1, (fillTexture == 0) ? blurredTexture : fillTexture);
         setUniform(fillShader, "color", ColorUtils.redf(color), ColorUtils.greenf(color), ColorUtils.bluef(color));
         setUniform(fillShader, "alpha", 0.7F);
         setUniform(fillShader, "time", (float)(System.currentTimeMillis() % 100000L) / 1000.0F);
         drawFullscreenQuad();
       } 
     } 
     
     if (hasGlow) {
       ShaderProgram glowShader = mc.method_62887().method_62947(ShaderUtils.shaderEspGlow);
       if (glowShader != null) {
         RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ZERO, GlStateManager.class_4534.ONE);
 
 
 
 
         
         RenderSystem.setShader(ShaderUtils.shaderEspGlow);
         RenderSystem.setShaderTexture(0, blurredTexture);
         RenderSystem.setShaderTexture(1, outlineBuffer.method_30277());
         setUniform(glowShader, "color", ColorUtils.redf(color), ColorUtils.greenf(color), ColorUtils.bluef(color));
         setUniform(glowShader, "color2", ColorUtils.redf(color), ColorUtils.greenf(color), ColorUtils.bluef(color));
         setUniform(glowShader, "exposure", 0.05075F);
         setUniform(glowShader, "time", (float)(System.currentTimeMillis() % 100000L) / 1000.0F);
         setUniform(glowShader, "animate", 1.0F);
         drawFullscreenQuadWithDepthTest(mainBuffer, outlineBuffer);
       } 
     } 
     
     RenderSystem.colorMask(true, true, true, true);
     RenderSystem.disableDepthTest();
     RenderSystem.disableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
     RenderSystem.setShaderTexture(0, 0);
     RenderSystem.setShaderTexture(1, 0);
     mainBuffer.method_1235(true);
   }
   
   private void drawFullscreenQuadWithDepthTest(Framebuffer mainBuffer, Framebuffer outlineBuffer) {
     if (this.depthCopyBuffer == null) {
       drawFullscreenQuad();
       
       return;
     } 
     GL30.glBindFramebuffer(36008, mainBuffer.field_1476);
     GL30.glBindFramebuffer(36009, this.depthCopyBuffer.field_1476);
     GL30.glBlitFramebuffer(0, 0, mainBuffer.field_1482, mainBuffer.field_1481, 0, 0, this.depthCopyBuffer.field_1482, this.depthCopyBuffer.field_1481, 256, 9728);
 
 
 
 
 
     
     GL30.glBindFramebuffer(36008, outlineBuffer.field_1476);
     GL30.glBindFramebuffer(36009, mainBuffer.field_1476);
     GL30.glBlitFramebuffer(0, 0, outlineBuffer.field_1482, outlineBuffer.field_1481, 0, 0, mainBuffer.field_1482, mainBuffer.field_1481, 256, 9728);
 
 
 
 
 
     
     mainBuffer.method_1235(false);
     RenderSystem.enableDepthTest();
     RenderSystem.depthFunc(515);
     RenderSystem.depthMask(false);
     
     drawFullscreenQuad();
     
     RenderSystem.depthMask(true);
     RenderSystem.disableDepthTest();
     
     GL30.glBindFramebuffer(36008, this.depthCopyBuffer.field_1476);
     GL30.glBindFramebuffer(36009, mainBuffer.field_1476);
     GL30.glBlitFramebuffer(0, 0, this.depthCopyBuffer.field_1482, this.depthCopyBuffer.field_1481, 0, 0, mainBuffer.field_1482, mainBuffer.field_1481, 256, 9728);
 
 
 
 
 
     
     mainBuffer.method_1235(false);
   }
   
   private void ensureDepthCopyBuffer(int width, int height) {
     if (this.depthCopyBuffer != null && (
       this.depthCopyBuffer.field_1482 != width || this.depthCopyBuffer.field_1481 != height)) {
       this.depthCopyBuffer.method_1238();
       this.depthCopyBuffer = null;
     } 
     
     if (this.depthCopyBuffer == null) {
       this.depthCopyBuffer = (Framebuffer)new SimpleFramebuffer(width, height, true);
     }
   }
   
   private boolean tryEnsureOutlineProcessor() {
     if (mc.field_1687 == null || mc.field_1769 == null) {
       this.outlineReady = false;
       return false;
     } 
     Framebuffer outlines = getOutlineSourceFramebuffer();
     if (outlines != null && outlines.method_30277() != 0) {
       this.outlineReady = true;
       return true;
     } 
     if (this.outlineReady) {
       this.outlineReady = false;
     }
     if (System.currentTimeMillis() < this.nextOutlineRetryAt) {
       return false;
     }
     try {
       mc.field_1769.method_3296();
       outlines = getOutlineSourceFramebuffer();
       this.outlineReady = (outlines != null && outlines.method_30277() != 0);
       if (!this.outlineReady) {
         this.nextOutlineRetryAt = System.currentTimeMillis() + 3000L;
       }
       return this.outlineReady;
     } catch (Throwable ignored) {
       this.outlineReady = false;
       this.nextOutlineRetryAt = System.currentTimeMillis() + 3000L;
       return false;
     } 
   }
   
   private Framebuffer getOutlineSourceFramebuffer() {
     WorldRenderer WorldRenderer = mc.field_1769; if (WorldRenderer instanceof WorldRendererAccessor) { WorldRendererAccessor accessor = (WorldRendererAccessor)WorldRenderer;
       Framebuffer raw = accessor.astra$getEntityOutlineFramebufferRaw();
       if (raw != null && raw.method_30277() != 0) {
         return raw;
       } }
     
     return mc.field_1769.method_22990();
   }
   
   public boolean shouldOutline(Entity entity) {
     if (!isEnable() || entity == null || mc.field_1724 == null || mc.field_1687 == null) return false; 
     if (!entity.method_5805()) return false; 
     if (entity.method_31481()) return false; 
     if (entity == mc.field_1724 && !this.targets.is("Себя")) return false; 
     if (entity.method_5858((Entity)mc.field_1724) > 65536.0D) return false;
     
     if (entity instanceof net.minecraft.PlayerEntity) {
       return this.targets.is("Игроки");
     }
     if (entity instanceof net.minecraft.EndCrystalEntity) {
       return this.targets.is("Кристаллы");
     }
     if (entity instanceof net.minecraft.ItemEntity) {
       return this.targets.is("Предметы");
     }
     return false;
   }
   
   private boolean hasOutlineTargets() {
     if (mc.field_1687 == null || mc.field_1724 == null) {
       return false;
     }
     for (Entity entity : mc.field_1687.method_18112()) {
       if (shouldOutline(entity)) {
         return true;
       }
     } 
     return false;
   }
   
   public int getOutlineColor() {
     return ColorUtils.setAlphaColor(ColorUtils.getThemeColor(), 255) & 0xFFFFFF;
   }
   
   private int runKawaseBloom(int sourceTexture, int iterations) {
     ensureBloomBuffers(iterations);
     if (this.bloomBuffers.isEmpty()) {
       return sourceTexture;
     }
     
     int currentTexture = sourceTexture;
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
   
   private void ensureBloomBuffers(int iterations) {
     int w = mc.method_22683().method_4489();
     int h = mc.method_22683().method_4506();
     
     if (this.bloomWidth != w || this.bloomHeight != h) {
       for (Framebuffer fb : this.bloomBuffers) {
         fb.method_1238();
       }
       this.bloomBuffers.clear();
       this.bloomWidth = w;
       this.bloomHeight = h;
     } 
     
     while (this.bloomBuffers.size() > iterations) {
       int last = this.bloomBuffers.size() - 1;
       ((Framebuffer)this.bloomBuffers.get(last)).method_1238();
       this.bloomBuffers.remove(last);
     } 
     
     for (int i = 0; i < iterations; i++) {
       int tw = Math.max(2, w >> i + 1);
       int th = Math.max(2, h >> i + 1);
       if (i >= this.bloomBuffers.size()) {
         SimpleFramebuffer SimpleFramebuffer = new SimpleFramebuffer(tw, th, false);
         setLinearFiltering((Framebuffer)SimpleFramebuffer);
         this.bloomBuffers.add(SimpleFramebuffer);
       }
       else {
         
         Framebuffer fb = this.bloomBuffers.get(i);
         if (fb.field_1482 != tw || fb.field_1481 != th) {
           fb.method_1238();
           SimpleFramebuffer SimpleFramebuffer = new SimpleFramebuffer(tw, th, false);
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
   
   private void setHandsKawaseUniforms(ShaderProgram shader, int texWidth, int texHeight, float offset) {
     setUniform(shader, "uSize", Math.max(1, texWidth), Math.max(1, texHeight));
     setUniform(shader, "uOffset", offset, offset);
     setUniform(shader, "uHalfPixel", 0.5F / Math.max(1, texWidth), 0.5F / Math.max(1, texHeight));
   }
   
   private void drawFullscreenQuad() {
     float width = Math.max(mc.method_22683().method_4486(), 1);
     float height = Math.max(mc.method_22683().method_4502(), 1);
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     buffer.method_22912(0.0F, 0.0F, 0.0F).method_22913(0.0F, 1.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     buffer.method_22912(0.0F, height, 0.0F).method_22913(0.0F, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     buffer.method_22912(width, height, 0.0F).method_22913(1.0F, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     buffer.method_22912(width, 0.0F, 0.0F).method_22913(1.0F, 1.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     BufferRenderer.method_43433(buffer.method_60800());
   }
 }

