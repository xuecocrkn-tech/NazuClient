package shame.nazuna.client.modules.impl.combat.components.interpolation;
 
 class null
   extends Interpolator
 {
   protected double curve(double var1) {
     return (Math.abs(var1 - 1.0D) < 1.0E-12D) ? 1.0D : 0.0D;
   }
   
   public String toString() {
     return "Interpolator.DISCRETE";
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\components\interpolation\Interpolator$1.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */