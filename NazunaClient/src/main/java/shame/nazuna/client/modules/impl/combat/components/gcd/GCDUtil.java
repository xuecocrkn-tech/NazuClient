package shame.nazuna.client.modules.impl.combat.components.gcd;
 
 import shame.nazuna.api.QClient;
 
 public class GCDUtil
   implements QClient {
   public static float getFixedRotation(float rot) {
     return getDeltaMouse(rot) * getGCDValue();
   }
   public static float getGCDValue() {
     return (float)(getGCD() * 0.15D);
   }
   
   public static float getGCD() {
     double f = 0.5000000149011612D;
     return (float)(f * f * f * 8.0D);
   }
   
   public static float getDeltaMouse(float delta) {
     return Math.round(delta / getGCDValue());
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\components\gcd\GCDUtil.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */