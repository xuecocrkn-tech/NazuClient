package shame.nazuna.client.modules.impl.combat.components.interpolation;
 
 public abstract class Interpolator
 {
   public static final Interpolator DISCRETE = new Interpolator() {
       protected double curve(double var1) {
         return (Math.abs(var1 - 1.0D) < 1.0E-12D) ? 1.0D : 0.0D;
       }
       
       public String toString() {
         return "Interpolator.DISCRETE";
       }
     }; private static final double EPSILON = 1.0E-12D;
   public static final Interpolator LINEAR = new Interpolator() {
       protected double curve(double var1) {
         return var1;
       }
       
       public String toString() {
         return "Interpolator.LINEAR";
       }
     };
   public static final Interpolator EASE_BOTH = new Interpolator() {
       protected double curve(double var1) {
         return Interpolator.clamp((var1 < 0.2D) ? (3.125D * var1 * var1) : ((var1 > 0.8D) ? (-3.125D * var1 * var1 + 6.25D * var1 - 2.125D) : (1.25D * var1 - 0.125D)));
       }
       
       public String toString() {
         return "Interpolator.EASE_BOTH";
       }
     };
   public static final Interpolator EASE_IN = new Interpolator() {
       private static final double S1 = 2.7777777777777777D;
       private static final double S3 = 1.1111111111111112D;
       private static final double S4 = 0.1111111111111111D;
       
       protected double curve(double var1) {
         return Interpolator.clamp((var1 < 0.2D) ? (2.7777777777777777D * var1 * var1) : (1.1111111111111112D * var1 - 0.1111111111111111D));
       }
       
       public String toString() {
         return "Interpolator.EASE_IN";
       }
     };
   public static final Interpolator EASE_OUT = new Interpolator() {
       private static final double S1 = -2.7777777777777777D;
       private static final double S2 = 5.555555555555555D;
       private static final double S3 = -1.7777777777777777D;
       private static final double S4 = 1.1111111111111112D;
       
       protected double curve(double var1) {
         return Interpolator.clamp((var1 > 0.8D) ? (-2.7777777777777777D * var1 * var1 + 5.555555555555555D * var1 + -1.7777777777777777D) : (1.1111111111111112D * var1));
       }
       
       public String toString() {
         return "Interpolator.EASE_OUT";
       }
     };
 
 
 
 
 
 
 
 
   
   public boolean interpolate(boolean var1, boolean var2, double var3) {
     return (Math.abs(curve(var3) - 1.0D) < 1.0E-12D) ? var2 : var1;
   }
   
   public double interpolate(double var1, double var3, double var5) {
     return var1 + (var3 - var1) * curve(var5);
   }
   
   public int interpolate(int var1, int var2, double var3) {
     return var1 + (int)Math.round((var2 - var1) * curve(var3));
   }
   
   public long interpolate(long var1, long var3, double var5) {
     return var1 + Math.round((var3 - var1) * curve(var5));
   }
   
   private static double clamp(double var0) {
     return (var0 < 0.0D) ? 0.0D : ((var0 > 1.0D) ? 1.0D : var0);
   }
   
   protected abstract double curve(double paramDouble);
 }

