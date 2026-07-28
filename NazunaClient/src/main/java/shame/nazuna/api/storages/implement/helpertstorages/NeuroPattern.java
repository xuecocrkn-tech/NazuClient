package shame.nazuna.api.storages.implement.helpertstorages;
 public class NeuroPattern implements Serializable { private static final long serialVersionUID = 1L; private final float yaw; private final float pitch; private final float deltaYaw;
   private final float deltaPitch;
   
   
   public float getSmoothness() { return this.smoothness; }
 
   
   public NeuroPattern(float yaw, float pitch, float deltaYaw, float deltaPitch, double distance, boolean isCritical, double targetSpeed, String targetType, float smoothness) {
     this.yaw = yaw;
     this.pitch = pitch;
     this.deltaYaw = deltaYaw;
     this.deltaPitch = deltaPitch;
     this.distance = distance;
     this.timestamp = System.currentTimeMillis();
     this.isCritical = isCritical;
     this.targetSpeed = targetSpeed;
     this.targetType = targetType;
     this.smoothness = smoothness;
   } }

