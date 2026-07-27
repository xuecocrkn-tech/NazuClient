package shame.nazuna.api.utils.render.glow;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.awt.Color;
 import net.minecraft.ProjectionType;
 import net.minecraft.Framebuffer;
 import net.minecraft.BufferRenderer;
 import net.minecraft.BufferBuilder;
 import net.minecraft.Tessellator;
 import net.minecraft.VertexFormat;
 import net.minecraft.MinecraftClient;
 import net.minecraft.MatrixStack;
 import net.minecraft.SimpleFramebuffer;
 import org.joml.Matrix4f;
 import org.joml.Matrix4fc;
 import org.lwjgl.opengl.GL11;
 import org.lwjgl.opengl.GL30;
 
 public class GlowProgram {
   private static final MinecraftClient mc = MinecraftClient.method_1551();
   
   private static GlowProgram instance;
   private Framebuffer glowBuffer;
   private int lastWidth;
   private int lastHeight;
   private float glowRadius = 10.0F;
   private float glowIntensity = 1.0F;
   private Color glowColor = Color.WHITE;
   
   private Matrix4f savedProjection;
   
   private int savedFbo;
   
   private static final int RINGS = 6;
   private static final int ANGLES_PER_RING = 12;
   
   public static GlowProgram getInstance() {
     if (instance == null) {
       instance = new GlowProgram();
     }
     return instance;
   }
   
   private void checkFramebuffers() {
     int width = mc.method_22683().method_4489();
     int height = mc.method_22683().method_4506();
     
     if (this.glowBuffer == null || this.lastWidth != width || this.lastHeight != height) {
       if (this.glowBuffer != null) {
         this.glowBuffer.method_1238();
       }
       this.glowBuffer = (Framebuffer)new SimpleFramebuffer(width, height, false);
       this.lastWidth = width;
       this.lastHeight = height;
     } 
   }
   
   public void begin(float radius, Color color) {
     begin(radius, 1.0F, color);
   }
   
   public void begin(float radius, float intensity, Color color) {
     checkFramebuffers();
     
     this.glowRadius = radius;
     this.glowIntensity = intensity;
     this.glowColor = color;
     
     this.savedProjection = new Matrix4f((Matrix4fc)RenderSystem.getProjectionMatrix());
     this.savedFbo = GL11.glGetInteger(36006);
     
     GL30.glBindFramebuffer(36160, this.glowBuffer.field_1476);
     GL11.glViewport(0, 0, this.lastWidth, this.lastHeight);
     RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
     RenderSystem.clear(16384);
     RenderSystem.setProjectionMatrix(this.savedProjection, ProjectionType.field_54954);
   }
   
   public void end(MatrixStack matrices, GlowCallback contentCallback) {
     GL30.glBindFramebuffer(36160, this.savedFbo);
     GL11.glViewport(0, 0, mc.method_22683().method_4489(), mc.method_22683().method_4506());
     RenderSystem.setProjectionMatrix(this.savedProjection, ProjectionType.field_54954);
     
     renderGlow(matrices);
     
     if (contentCallback != null) {
       contentCallback.render();
     }
   }
   
   private float gaussian(float x, float sigma) {
     return (float)Math.exp((-(x * x) / 2.0F * sigma * sigma));
   }
   
   private void renderGlow(MatrixStack matrices) {
     RenderSystem.enableBlend();
     RenderSystem.blendFunc(770, 1);
     RenderSystem.disableDepthTest();
     
     int width = mc.method_22683().method_4486();
     int height = mc.method_22683().method_4502();
     
     RenderSystem.setShaderTexture(0, this.glowBuffer.method_30277());
     RenderSystem.setShader(ShaderProgramKeys.field_53880);
     
     GL11.glTexParameteri(3553, 10241, 9729);
     GL11.glTexParameteri(3553, 10240, 9729);
     
     Matrix4f matrix = matrices.method_23760().method_23761();
     
     float r = this.glowColor.getRed() / 255.0F;
     float g = this.glowColor.getGreen() / 255.0F;
     float b = this.glowColor.getBlue() / 255.0F;
     float baseAlpha = this.glowColor.getAlpha() / 255.0F * this.glowIntensity;
     
     float sigma = this.glowRadius * 0.4F;
 
     
     float[] ringWeights = new float[6];
     float totalWeight = 0.0F;
     int i;
     for (i = 0; i < 6; i++) {
       float distance = this.glowRadius * (i + 1) / 6.0F;
       ringWeights[i] = gaussian(distance, sigma);
       totalWeight += ringWeights[i];
     } 
 
     
     for (i = 0; i < 6; i++) {
       ringWeights[i] = ringWeights[i] / totalWeight;
     }
 
     
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
 
 
 
     
     for (int ring = 0; ring < 6; ring++) {
       float distance = this.glowRadius * (ring + 1) / 6.0F;
       float alpha = baseAlpha * ringWeights[ring] * 0.7F;
       
       if (alpha >= 0.001F) {
         alpha = Math.min(alpha, 1.0F);
         
         for (int angle = 0; angle < 12; angle++) {
           float a1 = (float)(angle * 2.0D * Math.PI) / 12.0F;
           float ox = (float)Math.cos(a1) * distance;
           float oy = (float)Math.sin(a1) * distance;
           
           buffer.method_22918(matrix, ox, oy, 0.0F)
             .method_22913(0.0F, 1.0F).method_22915(r, g, b, alpha);
           buffer.method_22918(matrix, ox, height + oy, 0.0F)
             .method_22913(0.0F, 0.0F).method_22915(r, g, b, alpha);
           buffer.method_22918(matrix, width + ox, height + oy, 0.0F)
             .method_22913(1.0F, 0.0F).method_22915(r, g, b, alpha);
           buffer.method_22918(matrix, width + ox, oy, 0.0F)
             .method_22913(1.0F, 1.0F).method_22915(r, g, b, alpha);
 
           
           if (ring > 0) {
             float a2 = (float)((angle + 0.5D) * 2.0D * Math.PI) / 12.0F;
             float innerDist = distance * 0.6F;
             float ox2 = (float)Math.cos(a2) * innerDist;
             float oy2 = (float)Math.sin(a2) * innerDist;
             float alpha2 = alpha * 0.5F;
             
             buffer.method_22918(matrix, ox2, oy2, 0.0F)
               .method_22913(0.0F, 1.0F).method_22915(r, g, b, alpha2);
             buffer.method_22918(matrix, ox2, height + oy2, 0.0F)
               .method_22913(0.0F, 0.0F).method_22915(r, g, b, alpha2);
             buffer.method_22918(matrix, width + ox2, height + oy2, 0.0F)
               .method_22913(1.0F, 0.0F).method_22915(r, g, b, alpha2);
             buffer.method_22918(matrix, width + ox2, oy2, 0.0F)
               .method_22913(1.0F, 1.0F).method_22915(r, g, b, alpha2);
           } 
         } 
       } 
     } 
     BufferRenderer.method_43433(buffer.method_60800());
 
     
     GL11.glTexParameteri(3553, 10241, 9728);
     GL11.glTexParameteri(3553, 10240, 9728);
     RenderSystem.setShaderTexture(0, 0);
     
     RenderSystem.defaultBlendFunc();
     RenderSystem.enableDepthTest();
     RenderSystem.disableBlend();
   }
   
   public static void startGlow(float radius, int color, GlowCallback callback, MatrixStack matrices) {
     startGlow(radius, 1.0F, color, callback, matrices);
   }
   
   public static void startGlow(float radius, float intensity, int color, GlowCallback callback, MatrixStack matrices) {
     int a = color >> 24 & 0xFF;
     int r = color >> 16 & 0xFF;
     int g = color >> 8 & 0xFF;
     int b = color & 0xFF;
     if (a == 0) a = 255;
     
     GlowProgram glow = getInstance();
     glow.begin(radius, intensity, new Color(r, g, b, a));
     callback.render();
     glow.end(matrices, callback);
   }
   
   public void cleanup() {
     if (this.glowBuffer != null) {
       this.glowBuffer.method_1238();
       this.glowBuffer = null;
     } 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\render\glow\GlowProgram.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */