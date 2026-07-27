package shame.nazuna.api.utils.render.fonts.ttf;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.awt.Font;
 import net.minecraft.ShaderProgramKeys;
 import net.minecraft.BufferBuilder;
 import net.minecraft.Tessellator;
 import net.minecraft.VertexFormats;
 import net.minecraft.VertexFormat;
 import org.joml.Matrix4f;
 
 public class GradientFontRenderer extends MCFontRenderer {
   public GradientFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
     super(font, antiAlias, fractionalMetrics);
   }
   
   public int drawGradientString(String text, float x, float y, int topColor, int bottomColor, boolean dropShadow, boolean horizontal) {
     int i;
     if (dropShadow) {
       i = renderGradientString(text, x + 1.0F, y + 1.0F, topColor, bottomColor, true, horizontal);
       i = Math.max(i, renderGradientString(text, x, y, topColor, bottomColor, false, horizontal));
     } else {
       i = renderGradientString(text, x, y, topColor, bottomColor, false, horizontal);
     } 
     return i;
   }
   
   private int renderGradientString(String text, float x, float y, int startColor, int endColor, boolean dropShadow, boolean horizontal) {
     if (text == null) {
       return 0;
     }
     if ((startColor & 0xFC000000) == 0) {
       startColor |= 0xFF000000;
     }
     if ((endColor & 0xFC000000) == 0) {
       endColor |= 0xFF000000;
     }
     if (dropShadow) {
       startColor = (startColor & 0xFCFCFC) >> 2 | startColor & 0xFF000000;
       endColor = (endColor & 0xFCFCFC) >> 2 | endColor & 0xFF000000;
     } 
     float posX = x;
     float posY = y;
     return renderGradientStringAtPos(text, posX, posY, dropShadow, startColor, endColor, horizontal);
   }
   
   private int renderGradientStringAtPos(String text, float posX, float posY, boolean shadow, int startColor, int endColor, boolean horizontal) {
     float totalWidth = getStringWidth(text);
     float currentCountWidth = 0.0F;
     
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     
     Matrix4f matrix = new Matrix4f();
     
     for (int i = 0; i < text.length(); i++) {
       char c0 = text.charAt(i);
       
       if (c0 == ' ' || c0 == ' ') {
         posX += 4.0F;
 
       
       }
       else if (c0 < this.charData.length && this.charData[c0] != null) {
         
         float charWidth = ((this.charData[c0]).width - 8 + this.charOffset);
         
         if (horizontal) {
           float firstMix = currentCountWidth / totalWidth;
           float lastMix = (currentCountWidth + charWidth) / totalWidth;
           int firstColor = colorMix(startColor, endColor, firstMix);
           int lastColor = colorMix(startColor, endColor, lastMix);
           renderGradientChar(c0, posX, posY, firstColor, lastColor, true, matrix);
           currentCountWidth += charWidth;
         } else {
           renderGradientChar(c0, posX, posY, startColor, endColor, false, matrix);
         } 
         posX += charWidth;
       } 
     } 
     RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
     return (int)posX;
   }
   
   private int colorMix(int startColor, int endColor, float mix) {
     float startAlpha = (startColor >> 24 & 0xFF) / 255.0F;
     float startRed = (startColor >> 16 & 0xFF) / 255.0F;
     float startGreen = (startColor >> 8 & 0xFF) / 255.0F;
     float startBlue = (startColor & 0xFF) / 255.0F;
     float endAlpha = (endColor >> 24 & 0xFF) / 255.0F;
     float endRed = (endColor >> 16 & 0xFF) / 255.0F;
     float endGreen = (endColor >> 8 & 0xFF) / 255.0F;
     float endBlue = (endColor & 0xFF) / 255.0F;
     int mixAlpha = (int)(((1.0F - mix) * startAlpha + mix * endAlpha) * 255.0F);
     int mixRed = (int)(((1.0F - mix) * startRed + mix * endRed) * 255.0F);
     int mixGreen = (int)(((1.0F - mix) * startGreen + mix * endGreen) * 255.0F);
     int mixBlue = (int)(((1.0F - mix) * startBlue + mix * endBlue) * 255.0F);
     return mixAlpha << 24 | mixRed << 16 | mixGreen << 8 | mixBlue;
   }
   
   private void renderGradientChar(char ch, float posX, float posY, int startColor, int endColor, boolean horizontal, Matrix4f matrix) {
     if (ch >= this.charData.length || this.charData[ch] == null)
       return; 
     float startAlpha = (startColor >> 24 & 0xFF) / 255.0F;
     float startRed = (startColor >> 16 & 0xFF) / 255.0F;
     float startGreen = (startColor >> 8 & 0xFF) / 255.0F;
     float startBlue = (startColor & 0xFF) / 255.0F;
     float endAlpha = (endColor >> 24 & 0xFF) / 255.0F;
     float endRed = (endColor >> 16 & 0xFF) / 255.0F;
     float endGreen = (endColor >> 8 & 0xFF) / 255.0F;
     float endBlue = (endColor & 0xFF) / 255.0F;
     
     CFont.CharData charData = this.charData[ch];
     float charXPos = charData.storedX;
     float charYPos = charData.storedY;
     int charWidth = charData.width;
     float width = charWidth - 0.01F;
     
     float u0 = charXPos / 512.0F;
     float v0 = charYPos / 512.0F;
     float u1 = (charXPos + width - 1.0F) / 512.0F;
     float v1 = (charYPos + 7.99F) / 512.0F;
     
     RenderSystem.setShaderTexture(0, this.glTextureId);
     RenderSystem.setShader(ShaderProgramKeys.field_53880);
     
     Tessellator tessellator = Tessellator.method_1348();
     BufferBuilder buffer = tessellator.method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     
     if (horizontal) {
       buffer.method_22918(matrix, posX, posY, 0.0F).method_22913(u0, v0).method_22915(startRed, startGreen, startBlue, startAlpha);
       buffer.method_22918(matrix, posX, posY + 7.99F, 0.0F).method_22913(u0, v1).method_22915(startRed, startGreen, startBlue, startAlpha);
       buffer.method_22918(matrix, posX + width - 1.0F, posY + 7.99F, 0.0F).method_22913(u1, v1).method_22915(endRed, endGreen, endBlue, endAlpha);
       buffer.method_22918(matrix, posX + width - 1.0F, posY, 0.0F).method_22913(u1, v0).method_22915(endRed, endGreen, endBlue, endAlpha);
     } else {
       buffer.method_22918(matrix, posX, posY, 0.0F).method_22913(u0, v0).method_22915(startRed, startGreen, startBlue, startAlpha);
       buffer.method_22918(matrix, posX, posY + 7.99F, 0.0F).method_22913(u0, v1).method_22915(endRed, endGreen, endBlue, endAlpha);
       buffer.method_22918(matrix, posX + width - 1.0F, posY + 7.99F, 0.0F).method_22913(u1, v1).method_22915(endRed, endGreen, endBlue, endAlpha);
       buffer.method_22918(matrix, posX + width - 1.0F, posY, 0.0F).method_22913(u1, v0).method_22915(startRed, startGreen, startBlue, startAlpha);
     } 
     
     BufferRenderer.method_43433(buffer.method_60800());
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\render\fonts\ttf\GradientFontRenderer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */