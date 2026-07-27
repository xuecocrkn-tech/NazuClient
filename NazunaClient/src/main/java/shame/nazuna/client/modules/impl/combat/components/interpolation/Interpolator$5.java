package shame.nazuna.client.modules.impl.combat.components.interpolation;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 class null
   extends Interpolator
 {
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
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\components\interpolation\Interpolator$5.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */