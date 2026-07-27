package shame.nazuna.api.utils.math;
 import java.math.BigDecimal;
 import java.math.RoundingMode;
 import java.util.concurrent.ThreadLocalRandom;
 import net.minecraft.MathHelper;
 
 public final class Mathf {
   private Mathf() {
     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
   public static float clamp01(float x) {
     return (float)clamp(0.0D, 1.0D, x);
   }
   
   public static double getRandom(double min, double max) {
     if (min == max)
       return min; 
     if (min > max) {
       double d = min;
       min = max;
       max = d;
     } 
     return ThreadLocalRandom.current().nextDouble() * (max - min) + min;
   }
   
   public static float calculateDelta(float a, float b) {
     return a - b;
   }
   
   public static double round(double target, int decimal) {
     double p = Math.pow(10.0D, decimal);
     return Math.round(target * p) / p;
   }
   
   public static Number round(double num, double increment) {
     if (increment <= 0.0D) {
       throw new IllegalArgumentException("Increment must be greater than zero");
     }
     double roundedValue = Math.round(num / increment) * increment;
     BigDecimal bigDecimal = BigDecimal.valueOf(roundedValue);
     bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
     return Double.valueOf(bigDecimal.doubleValue());
   }
   
   public static String formatTime(long millis) {
     long hours = millis / 3600000L;
     long minutes = millis % 3600000L / 60000L;
     long seconds = millis % 360000L % 60000L / 1000L;
     return String.format("%02d:%02d:%02d", new Object[] { Long.valueOf(hours), Long.valueOf(minutes), Long.valueOf(seconds) });
   }
   
   public static float slerp(float start, float end, float t) {
     t = Math.max(0.0F, Math.min(1.0F, t));
     float startRadians = (float)Math.toRadians(start);
     float endRadians = (float)Math.toRadians(end);
 
     
     float dotProduct = (float)Math.cos(startRadians) * (float)Math.cos(endRadians) + (float)Math.sin(startRadians) * (float)Math.sin(endRadians);
     
     float angle = (float)Math.acos(dotProduct);
     
     if (Math.abs(angle) < 0.001F) {
       return start;
     }
     
     float factorStart = (float)(Math.sin(((1.0F - t) * angle)) / Math.sin(angle));
     float factorEnd = (float)(Math.sin((t * angle)) / Math.sin(angle));
     
     float interpolatedValue = start * factorStart + end * factorEnd;
     return (float)MathHelper.method_15350(MathHelper.method_15338(Math.toDegrees(interpolatedValue)), start, end);
   }
   
   public static double round(double value, int scale, double inc) {
     double halfOfInc = inc / 2.0D;
     double floored = Math.floor(value / inc) * inc;
     
     if (value >= floored + halfOfInc) {
       return (new BigDecimal(Math.ceil(value / inc) * inc))
         .setScale(scale, RoundingMode.HALF_UP)
         .doubleValue();
     }
     return (new BigDecimal(floored))
       .setScale(scale, RoundingMode.HALF_UP)
       .doubleValue();
   }
 
   
   public static double step(double value, double steps) {
     double a = Math.round(value / steps) * steps;
     a *= 1000.0D;
     a = (int)a;
     a /= 1000.0D;
     return a;
   }
   
   public static double getDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
     double deltaX = x2 - x1;
     double deltaY = y2 - y1;
     double deltaZ = z2 - z1;
     return MathHelper.method_15355((float)(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ));
   }
   
   public static double clamp(double min, double max, double n) {
     return Math.max(min, Math.min(max, n));
   }
   public static int clamp(int min, int max, int value) {
     return Math.max(min, Math.min(max, value));
   }
   
   public static float normalize(float value, float min, float max) {
     return (value - min) / (max - min);
   }
   
   public static double interporate(double p_219803_0_, double p_219803_2_, double p_219803_4_) {
     return p_219803_2_ + p_219803_0_ * (p_219803_4_ - p_219803_2_);
   }
   
   public static float lerp(float min, float max, float delta) {
     return min + (max - min) * delta;
   }
   
   public static float easeOutExpo(float x) {
     return (x == 1.0F) ? 1.0F : (float)(1.0D - Math.pow(2.0D, (-10.0F * x)));
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\math\Mathf.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */