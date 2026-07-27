package shame.nazuna.api.utils.color.fontscolor;
 
 import java.awt.Color;
 import java.nio.ByteBuffer;
 import java.util.Objects;
 import net.minecraft.MathHelper;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.math.MathUtils;
 
 public class ColorRGBA {
   public static final ColorRGBA WHITE = new ColorRGBA(255, 255, 255);
   public static final ColorRGBA BLACK = new ColorRGBA(0, 0, 0);
   public static final ColorRGBA GREEN = new ColorRGBA(0, 255, 0);
   public static final ColorRGBA RED = new ColorRGBA(255, 0, 0);
   public static final ColorRGBA BLUE = new ColorRGBA(0, 0, 255);
   public static final ColorRGBA YELLOW = new ColorRGBA(255, 255, 0);
   public static final ColorRGBA GRAY = new ColorRGBA(88, 87, 93);
   public static final ColorRGBA TRANSPARENT = new ColorRGBA(0, 0, 0, 0);
   private transient float[] hsbValues;
   private final int red;
   private final int green;
   private final int blue;
   private final int alpha;
   private static final ByteBuffer PIXEL_BUFFER = ByteBuffer.allocateDirect(4);
   
   public ColorRGBA(int color) {
     this(ColorUtils.red(color), ColorUtils.green(color), ColorUtils.blue(color), ColorUtils.alpha(color));
   }
   
   public ColorRGBA(Color color) {
     this(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
   }
   
   public ColorRGBA(int red, int green, int blue) {
     this(red, green, blue, 255);
   }
   
   public ColorRGBA(int red, int green, int blue, int alpha) {
     red = MathHelper.method_15340(red, 0, 255);
     green = MathHelper.method_15340(green, 0, 255);
     blue = MathHelper.method_15340(blue, 0, 255);
     alpha = MathHelper.method_15340(alpha, 0, 255);
     this.red = red;
     this.green = green;
     this.blue = blue;
     this.alpha = alpha;
   }
   
   public ColorRGBA(int red, int green, int blue, float alpha) {
     red = MathHelper.method_15340(red, 0, 255);
     green = MathHelper.method_15340(green, 0, 255);
     blue = MathHelper.method_15340(blue, 0, 255);
     alpha = MathHelper.method_15363(alpha, 0.0F, 255.0F);
     this.red = red;
     this.green = green;
     this.blue = blue;
     this.alpha = (int)alpha;
   }
   
   public int getRGB() {
     int a = Math.round(clamp(this.alpha));
     int r = Math.round(clamp(this.red));
     int g = Math.round(clamp(this.green));
     int b = Math.round(clamp(this.blue));
     return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
   }
   
   private int clamp(float value) {
     return (int)Math.max(0.0F, Math.min(255.0F, value));
   }
   
   public static ColorRGBA fromHex(String hex) {
     String sanitized = hex.startsWith("#") ? hex.substring(1) : hex;
     if (sanitized.length() != 6 && sanitized.length() != 8) {
       throw new IllegalArgumentException("Hex color must be in the format #RRGGBB or #RRGGBBAA");
     }
     int red = Integer.parseInt(sanitized.substring(0, 2), 16);
     int green = Integer.parseInt(sanitized.substring(2, 4), 16);
     int blue = Integer.parseInt(sanitized.substring(4, 6), 16);
     int alpha = (sanitized.length() == 8) ? Integer.parseInt(sanitized.substring(6, 8), 16) : 255;
     return new ColorRGBA(red, green, blue, alpha);
   }
 
   
   public static ColorRGBA lerp(ColorRGBA startColor, ColorRGBA endColor, float delta) {
     float clampedDelta = Math.max(0.0F, Math.min(1.0F, delta));
     int r = (int)(startColor.getRed() + (endColor.getRed() - startColor.getRed()) * clampedDelta);
     int g = (int)(startColor.getGreen() + (endColor.getGreen() - startColor.getGreen()) * clampedDelta);
     int b = (int)(startColor.getBlue() + (endColor.getBlue() - startColor.getBlue()) * clampedDelta);
     int a = (int)(startColor.getAlpha() + (endColor.getAlpha() - startColor.getAlpha()) * clampedDelta);
     return new ColorRGBA(r, g, b, a);
   }
   
   public static ColorRGBA fromInt(int colorInt) {
     int alpha = colorInt >> 24 & 0xFF;
     int red = colorInt >> 16 & 0xFF;
     int green = colorInt >> 8 & 0xFF;
     int blue = colorInt & 0xFF;
     return new ColorRGBA(red, green, blue, alpha);
   }
   
   public ColorRGBA withAlpha(float newAlpha) {
     return new ColorRGBA(this.red, this.green, this.blue, (int)newAlpha);
   }
   
   public ColorRGBA withAlpha(int newAlpha) {
     return new ColorRGBA(this.red, this.green, this.blue, newAlpha);
   }
   
   public ColorRGBA mulAlpha(float percent) {
     return withAlpha((int)(this.alpha * percent));
   }
   
   public ColorRGBA mix(ColorRGBA color2, float amount) {
     amount = Math.min(1.0F, Math.max(0.0F, amount));
     return new ColorRGBA((int)MathUtils.interpolate(getRed(), color2.getRed(), amount), (int)MathUtils.interpolate(getGreen(), color2.getGreen(), amount), (int)MathUtils.interpolate(getBlue(), color2.getBlue(), amount), (int)MathUtils.interpolate(getAlpha(), color2.getAlpha(), amount));
   }
   
   public ColorRGBA darker(float amount) {
     amount = MathHelper.method_15363(amount, 0.0F, 1.0F);
     return new ColorRGBA((int)(this.red * (1.0F - amount)), (int)(this.green * (1.0F - amount)), (int)(this.blue * (1.0F - amount)), this.alpha);
   }
   
   public static ColorRGBA fromHSB(float hue, float saturation, float brightness) {
     if (saturation == 0.0F) {
       int grayValue = (int)(brightness * 255.0F + 0.5F);
       return new ColorRGBA(grayValue, grayValue, grayValue);
     } 
     float h = (hue - (float)Math.floor(hue)) * 6.0F;
     float f = h - (float)Math.floor(h);
     float p = brightness * (1.0F - saturation);
     float q = brightness * (1.0F - saturation * f);
     float t = brightness * (1.0F - saturation * (1.0F - f));
     float r = 0.0F;
     float g = 0.0F;
     float b = 0.0F;
     switch ((int)h) {
       case 0:
         r = brightness;
         g = t;
         b = p;
         break;
       case 1:
         r = q;
         g = brightness;
         b = p;
         break;
       case 2:
         r = p;
         g = brightness;
         b = t;
         break;
       case 3:
         r = p;
         g = q;
         b = brightness;
         break;
       case 4:
         r = t;
         g = p;
         b = brightness;
         break;
       case 5:
         r = brightness;
         g = p;
         b = q;
         break;
     } 
     return new ColorRGBA((int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F));
   }
 
   
   public float getHue() {
     return getHSBValues()[0];
   }
   
   public float getSaturation() {
     return getHSBValues()[2];
   }
   
   public float getBrightness() {
     return getHSBValues()[1];
   }
   
   private float[] getHSBValues() {
     if (this.hsbValues == null) {
       this.hsbValues = calculateHSB();
     }
     
     return this.hsbValues;
   }
   
   private float[] calculateHSB() {
     float r = this.red / 255.0F;
     float g = this.green / 255.0F;
     float b = this.blue / 255.0F;
     float maxC = Math.max(r, Math.max(g, b));
     float minC = Math.min(r, Math.min(g, b));
     float delta = maxC - minC;
     float hue = 0.0F;
     if (delta != 0.0F) {
       if (maxC == r) {
         hue = (g - b) / delta;
       } else if (maxC == g) {
         hue = (b - r) / delta + 2.0F;
       } else {
         hue = (r - g) / delta + 4.0F;
       } 
       
       hue /= 6.0F;
       if (hue < 0.0F) {
         hue++;
       }
     } 
     
     float saturation = (maxC == 0.0F) ? 0.0F : (delta / maxC);
     return new float[] { hue, saturation, maxC };
   }
   
   public ColorRGBA brighter(float amount) {
     amount = MathHelper.method_15363(amount, 0.0F, 1.0F);
     return new ColorRGBA((int)(this.red + (255.0F - this.red) * amount), (int)(this.green + (255.0F - this.green) * amount), (int)(this.blue + (255.0F - this.blue) * amount), this.alpha);
   }
   
   public boolean equals(Object o) {
     if (this == o)
       return true; 
     if (o != null && getClass() == o.getClass()) {
       ColorRGBA colorRGBA = (ColorRGBA)o;
       return (Float.compare(this.red, colorRGBA.red) == 0 && Float.compare(this.green, colorRGBA.green) == 0 && Float.compare(this.blue, colorRGBA.blue) == 0 && Float.compare(this.alpha, colorRGBA.alpha) == 0);
     } 
     return false;
   }
 
   
   public float difference(ColorRGBA colorRGBA) {
     return Math.abs(getHue() - colorRGBA.getHue()) + Math.abs(getBrightness() - colorRGBA.getBrightness()) + Math.abs(getSaturation() - colorRGBA.getSaturation());
   }
   
   public int hashCode() {
     return Objects.hash(new Object[] { Integer.valueOf(this.red), Integer.valueOf(this.green), Integer.valueOf(this.blue), Integer.valueOf(this.alpha) });
   }
   
   public int getRed() {
     return this.red;
   }
   
   public int getGreen() {
     return this.green;
   }
   
   public int getBlue() {
     return this.blue;
   }
   
   public int getAlpha() {
     return this.alpha;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\color\fontscolor\ColorRGBA.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */