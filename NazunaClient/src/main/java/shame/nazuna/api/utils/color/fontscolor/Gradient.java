package shame.nazuna.api.utils.color.fontscolor;
 
 import java.util.List;
 
 public class Gradient {
   protected final ColorRGBA topLeftColor;
   protected final ColorRGBA bottomLeftColor;
   protected final ColorRGBA topRightColor;
   protected final ColorRGBA bottomRightColor;
   
   protected Gradient(ColorRGBA topLeftColor, ColorRGBA bottomLeftColor, ColorRGBA topRightColor, ColorRGBA bottomRightColor) {
     this.topLeftColor = topLeftColor;
     this.bottomLeftColor = bottomLeftColor;
     this.topRightColor = topRightColor;
     this.bottomRightColor = bottomRightColor;
   }
   
   public static Gradient of(ColorRGBA topLeftColor, ColorRGBA bottomLeftColor, ColorRGBA topRightColor, ColorRGBA bottomRightColor) {
     return new Gradient(topLeftColor, bottomLeftColor, topRightColor, bottomRightColor);
   }
   
   public static Gradient of(List<ColorRGBA> colors) {
     return new Gradient(colors.get(0), colors.get(1), colors.get(2), colors.get(3));
   }
   
   public Gradient rotate() {
     return this;
   }
   
   public Gradient mulAlpha(float alphaMultiplier) {
     return new Gradient(this.topLeftColor.mulAlpha(alphaMultiplier), this.bottomLeftColor.mulAlpha(alphaMultiplier), this.topRightColor.mulAlpha(alphaMultiplier), this.bottomRightColor.mulAlpha(alphaMultiplier));
   }
   
   public ColorRGBA getTopLeftColor() {
     return this.topLeftColor;
   }
   
   public ColorRGBA getBottomLeftColor() {
     return this.bottomLeftColor;
   }
   
   public ColorRGBA getTopRightColor() {
     return this.topRightColor;
   }
   
   public ColorRGBA getBottomRightColor() {
     return this.bottomRightColor;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\color\fontscolor\Gradient.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */