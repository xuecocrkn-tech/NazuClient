package shame.nazuna.api.storages.implement.helpertstorages;
 public final class SituationKey { private final String targetType; private final String distanceBucket;
   
   public String getHealthState() { return this.healthState; }
 
   
   public String toString() {
     return this.targetType + "_" + this.targetType + "_" + this.distanceBucket + "_" + this.movementState + "_" + this.critState;
   } }

