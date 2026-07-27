package shame.nazuna.client.modules.impl.combat.components.interpolation;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 class null
   extends Interpolator
 {
   protected double curve(double var1) {
     return Interpolator.clamp((var1 < 0.2D) ? (3.125D * var1 * var1) : ((var1 > 0.8D) ? (-3.125D * var1 * var1 + 6.25D * var1 - 2.125D) : (1.25D * var1 - 0.125D)));
   }
   
   public String toString() {
     return "Interpolator.EASE_BOTH";
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\components\interpolation\Interpolator$3.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */