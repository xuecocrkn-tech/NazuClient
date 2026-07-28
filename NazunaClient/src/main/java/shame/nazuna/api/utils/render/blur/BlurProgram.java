package shame.nazuna.api.utils.render.blur;
 
 import com.mojang.blaze3d.systems.RenderSystem;
 import net.minecraft.Framebuffer;
 import net.minecraft.GlUniform;
 import net.minecraft.BufferBuilder;
 import net.minecraft.Tessellator;
 import net.minecraft.VertexFormat;
 import net.minecraft.ShaderProgram;
 import net.minecraft.SimpleFramebuffer;
 import org.lwjgl.opengl.GL30;
 import shame.nazuna.api.utils.render.ShaderUtils;
 
 public class BlurProgram implements QClient {
   private static BlurProgram instance;
   
   public static Framebuffer getBuffer2() {
     return buffer2;
   }
   
   private int lastWidth = -1;
   private int lastHeight = -1;
   private long lastUpdateTime = 0L;
   
   private boolean requestedThisFrame = true;
 
   
   private final int iterations = 4;
   
   public static BlurProgram getInstance() {
     if (instance == null) {
       instance = new BlurProgram();
     }
     return instance;
   }
   
   public void beginFrame() {
     boolean shouldDraw = this.requestedThisFrame;
     this.requestedThisFrame = false;
     if (!shouldDraw) {
       return;
     }
     draw();
   }
   
   public void request() {
     this.requestedThisFrame = true;
   }
   
   private void draw() {
     long currentTime = System.currentTimeMillis();
     if (currentTime - this.lastUpdateTime < 16L) {
       return;
     }
     this.lastUpdateTime = currentTime;
     
     int width = mc.method_22683().method_4489();
     int height = mc.method_22683().method_4506();
     
     if (buffer1 == null || buffer2 == null || this.lastWidth != width || this.lastHeight != height) {
       if (buffer1 != null) {
         buffer1.method_1238();
       }
       if (buffer2 != null) {
         buffer2.method_1238();
       }
       buffer1 = (Framebuffer)new SimpleFramebuffer(width, height, false);
       buffer2 = (Framebuffer)new SimpleFramebuffer(width, height, false);
       
       setLinearFiltering(buffer1);
       setLinearFiltering(buffer2);
       
       this.lastWidth = width;
       this.lastHeight = height;
     } 
     
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     
     ShaderProgram kawaseDown = mc.method_62887().method_62947(ShaderUtils.kawaseDown);
     ShaderProgram kawaseUp = mc.method_62887().method_62947(ShaderUtils.kawaseUp);
     
     buffer1.method_1236(0.0F, 0.0F, 0.0F, 0.0F);
     buffer1.method_1230();
     buffer1.method_1235(true);
     
     RenderSystem.setShader(ShaderUtils.kawaseDown);
     mc.method_1522().method_35610();
     RenderSystem.setShaderTexture(0, mc.method_1522().method_30277());
     
     setKawaseUniforms(kawaseDown, width, height);
     drawQuad(mc.method_22683().method_4486(), mc.method_22683().method_4502());
     
     mc.method_1522().method_1242();
     buffer1.method_1240();
     
     Framebuffer[] buffers = { buffer1, buffer2 };
     int i;
     for (i = 1; i < 4; i++) {
       int srcIndex = (i + 1) % 2;
       int dstIndex = i % 2;
       
       Framebuffer src = buffers[srcIndex];
       Framebuffer dst = buffers[dstIndex];
       
       dst.method_1236(0.0F, 0.0F, 0.0F, 0.0F);
       dst.method_1230();
       dst.method_1235(true);
       
       RenderSystem.setShader(ShaderUtils.kawaseDown);
       src.method_35610();
       RenderSystem.setShaderTexture(0, src.method_30277());
       
       setKawaseUniforms(kawaseDown, src.field_1482, src.field_1481);
       drawQuad(mc.method_22683().method_4486(), mc.method_22683().method_4502());
       
       src.method_1242();
       dst.method_1240();
     } 
     
     for (i = 0; i < 4; i++) {
       int srcIndex = i % 2;
       int dstIndex = (i + 1) % 2;
       
       Framebuffer src = buffers[srcIndex];
       Framebuffer dst = buffers[dstIndex];
       
       dst.method_1236(0.0F, 0.0F, 0.0F, 0.0F);
       dst.method_1230();
       dst.method_1235(true);
       
       RenderSystem.setShader(ShaderUtils.kawaseUp);
       src.method_35610();
       RenderSystem.setShaderTexture(0, src.method_30277());
       
       setKawaseUniforms(kawaseUp, src.field_1482, src.field_1481);
       drawQuad(mc.method_22683().method_4486(), mc.method_22683().method_4502());
       
       src.method_1242();
       dst.method_1240();
     } 
     
     RenderSystem.disableBlend();
     mc.method_1522().method_1235(true);
     RenderSystem.setShaderTexture(0, 0);
   }
   
   private void setLinearFiltering(Framebuffer framebuffer) {
     RenderSystem.bindTexture(framebuffer.method_30277());
     GL30.glTexParameteri(3553, 10241, 9729);
     GL30.glTexParameteri(3553, 10240, 9729);
     RenderSystem.bindTexture(0);
   }
   
   private void setKawaseUniforms(ShaderProgram shader, int texWidth, int texHeight) {
     GlUniform resolutionUniform = shader.method_34582("Resolution");
     GlUniform offsetUniform = shader.method_34582("Offset");
     GlUniform saturationUniform = shader.method_34582("Saturation");
     GlUniform tintIntensityUniform = shader.method_34582("TintIntensity");
     GlUniform tintColorUniform = shader.method_34582("TintColor");
     
     if (resolutionUniform != null) resolutionUniform.method_1255(1.0F / texWidth, 1.0F / texHeight); 
     if (offsetUniform != null) offsetUniform.method_1251(this.blurOffset); 
     if (saturationUniform != null) saturationUniform.method_1251(1.0F); 
     if (tintIntensityUniform != null) tintIntensityUniform.method_1251(0.0F); 
     if (tintColorUniform != null) tintColorUniform.method_1249(1.0F, 1.0F, 1.0F); 
   }
   
   private void drawQuad(float width, float height) {
     BufferBuilder builder = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     builder.method_22912(0.0F, 0.0F, 0.0F).method_22913(0.0F, 1.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     builder.method_22912(0.0F, height, 0.0F).method_22913(0.0F, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     builder.method_22912(width, height, 0.0F).method_22913(1.0F, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     builder.method_22912(width, 0.0F, 0.0F).method_22913(1.0F, 1.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     BufferRenderer.method_43433(builder.method_60800());
   }
   
   public static int getTexture() {
     getInstance().request();
     return (buffer1 != null) ? buffer1.method_30277() : 0;
   }
 }

