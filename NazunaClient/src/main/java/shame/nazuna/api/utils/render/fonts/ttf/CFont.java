package shame.nazuna.api.utils.render.fonts.ttf;
 import java.awt.Font;
 import java.awt.FontMetrics;
 import java.awt.Graphics2D;
 import java.awt.RenderingHints;
 import java.awt.geom.Rectangle2D;
 import java.awt.image.BufferedImage;
 import net.minecraft.NativeImage;
 import net.minecraft.NativeImageBackedTexture;
 import net.minecraft.BufferBuilder;
 import net.minecraft.Identifier;
 import org.joml.Matrix4f;
 
 public class CFont {
   protected static final int IMG_SIZE = 512;
   public boolean isAntiAlias() {
   public boolean isFractionalMetrics() {
     return this.fractionalMetrics;
   }
   protected int fontHeight = -1;
   public int getGlTextureId() {
     return this.glTextureId;
   }
   private static int textureCounter = 0;
   
   public CFont(Font font, boolean antiAlias, boolean fractionalMetrics) {
     this.font = font;
     this.antiAlias = antiAlias;
     this.fractionalMetrics = fractionalMetrics;
     setupTexture(font, antiAlias, fractionalMetrics, this.charData);
   }
   
   protected void setupTexture(Font font, boolean antiAlias, boolean fractionalMetrics, CharData[] chars) {
     BufferedImage img = generateFontImage(font, antiAlias, fractionalMetrics, chars);
     try {
       NativeImage nativeImage = new NativeImage(img.getWidth(), img.getHeight(), false);
       for (int y = 0; y < img.getHeight(); y++) {
         for (int x = 0; x < img.getWidth(); x++) {
           int argb = img.getRGB(x, y);
           int a = argb >> 24 & 0xFF;
           int r = argb >> 16 & 0xFF;
           int g = argb >> 8 & 0xFF;
           int b = argb & 0xFF;
           nativeImage.method_61941(x, y, a << 24 | r << 16 | g << 8 | b);
         } 
       } 
       NativeImageBackedTexture texture = new NativeImageBackedTexture(nativeImage);
       this.glTextureId = texture.method_4624();
       String name = "cfont_" + textureCounter++;
       this.textureId = Identifier.method_60655("customfont", name);
       MinecraftClient.method_1551().method_1531().method_4616(this.textureId, (AbstractTexture)texture);
     } catch (Exception e) {
       e.printStackTrace();
     } 
   }
   
   protected BufferedImage generateFontImage(Font font, boolean antiAlias, boolean fractionalMetrics, CharData[] chars) {
     BufferedImage bufferedImage = new BufferedImage(512, 512, 2);
     Graphics2D g = (Graphics2D)bufferedImage.getGraphics();
     g.setFont(font);
     g.setColor(new Color(255, 255, 255, 0));
     g.fillRect(0, 0, 512, 512);
     g.setColor(Color.WHITE);
     g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
     g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
     g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
     FontMetrics fontMetrics = g.getFontMetrics();
     int charHeight = 0;
     int positionX = 0;
     int positionY = 1;
     for (int i = 0; i < chars.length; i++) {
       char ch = (char)i;
       if ((ch > 'Џ' && ch < 'ѐ') || ch < 'Ā') {
         CharData charData = new CharData();
         Rectangle2D dimensions = fontMetrics.getStringBounds(String.valueOf(ch), g);
         charData.width = (dimensions.getBounds()).width + 8;
         charData.height = (dimensions.getBounds()).height;
         if (positionX + charData.width >= 512) {
           positionX = 0;
           positionY += charHeight;
           charHeight = 0;
         } 
         if (charData.height > charHeight) {
           charHeight = charData.height;
         }
         charData.storedX = positionX;
         charData.storedY = positionY;
         if (charData.height > this.fontHeight) {
           this.fontHeight = charData.height;
         }
         chars[i] = charData;
         g.drawString(String.valueOf(ch), positionX + 2, positionY + fontMetrics.getAscent());
         positionX += charData.width;
       } 
     }  return bufferedImage;
   }
   
   public void drawChar(CharData[] chars, char c, float x, float y, Matrix4f matrix, BufferBuilder buffer) {
     try {
       if (chars[c] == null)
         return;  drawQuad(x, y, (chars[c]).width, (chars[c]).height, (chars[c]).storedX, (chars[c]).storedY, (chars[c]).width, (chars[c]).height, matrix, buffer);
     } catch (Exception exception) {}
   }
 
   
   protected void drawQuad(float x, float y, float width, float height, float srcX, float srcY, float srcWidth, float srcHeight, Matrix4f matrix, BufferBuilder buffer) {
     float renderSRCX = srcX / 512.0F;
     float renderSRCY = srcY / 512.0F;
     float renderSRCWidth = srcWidth / 512.0F;
     float renderSRCHeight = srcHeight / 512.0F;
     
     buffer.method_22918(matrix, x + width, y, 0.0F).method_22913(renderSRCX + renderSRCWidth, renderSRCY);
     buffer.method_22918(matrix, x, y, 0.0F).method_22913(renderSRCX, renderSRCY);
     buffer.method_22918(matrix, x, y + height, 0.0F).method_22913(renderSRCX, renderSRCY + renderSRCHeight);
     buffer.method_22918(matrix, x, y + height, 0.0F).method_22913(renderSRCX, renderSRCY + renderSRCHeight);
     buffer.method_22918(matrix, x + width, y + height, 0.0F).method_22913(renderSRCX + renderSRCWidth, renderSRCY + renderSRCHeight);
     buffer.method_22918(matrix, x + width, y, 0.0F).method_22913(renderSRCX + renderSRCWidth, renderSRCY);
   }
   
   public int getStringHeight(String text) {
     return getFontHeight();
   }
   
   public int getFontHeight() {
     return (this.fontHeight - 8) / 2;
   }
   
   public int getStringWidth(String text) {
     int width = 0;
     for (char c : text.toCharArray()) {
       if (c < this.charData.length && this.charData[c] != null)
         width += (this.charData[c]).width - 8 + this.charOffset; 
     } 
     return width / 2;
   }
   
   public void setAntiAlias(boolean antiAlias) {
     if (this.antiAlias != antiAlias) {
       this.antiAlias = antiAlias;
       setupTexture(this.font, antiAlias, this.fractionalMetrics, this.charData);
     } 
   }
   
   public void setFractionalMetrics(boolean fractionalMetrics) {
     if (this.fractionalMetrics != fractionalMetrics) {
       this.fractionalMetrics = fractionalMetrics;
       setupTexture(this.font, this.antiAlias, fractionalMetrics, this.charData);
     } 
   }
   
   public void setFont(Font font) {
     this.font = font;
     setupTexture(font, this.antiAlias, this.fractionalMetrics, this.charData);
   }
   
   protected static class CharData {
     public int width;
     public int height;
     public int storedX;
     public int storedY;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\render\fonts\ttf\CFont.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */