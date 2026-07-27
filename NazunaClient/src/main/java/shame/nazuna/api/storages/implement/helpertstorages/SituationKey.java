package shame.nazuna.api.storages.implement.helpertstorages;
 public final class SituationKey { private final String targetType; private final String distanceBucket;
   
   public String getHealthState() { return this.healthState; }
 
   
   public String toString() {
     return this.targetType + "_" + this.targetType + "_" + this.distanceBucket + "_" + this.movementState + "_" + this.critState;
   } }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\storages\implement\helpertstorages\SituationKey.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */