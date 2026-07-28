package shame.nazuna.api.events.implement;
 
 
 public class EventKeyboardInput extends Event {
   private float movementForward;
   private float movementSideways;
   
   
   public void setYaw(float yaw, float yaw2) {
     float forward = getMovementForward();
     float sideways = getMovementSideways();
     double angle = MathHelper.method_15338(Math.toDegrees(direction(yaw2, forward, sideways)));
     if (forward == 0.0F && sideways == 0.0F)
       return;  float closestForward = 0.0F, closestSideways = 0.0F, closestDifference = Float.MAX_VALUE;
     float predictedForward;
     for (predictedForward = -1.0F; predictedForward <= 1.0F; predictedForward++) {
       float predictedSideways; for (predictedSideways = -1.0F; predictedSideways <= 1.0F; predictedSideways++) {
         if (predictedSideways != 0.0F || predictedForward != 0.0F) {
           
           double predictedAngle = MathHelper.method_15338(Math.toDegrees(direction(yaw, predictedForward, predictedSideways)));
           double difference = Math.abs(angle - predictedAngle);
           
           if (difference < closestDifference) {
             closestDifference = (float)difference;
             closestForward = predictedForward;
             closestSideways = predictedSideways;
           } 
         } 
       } 
     } 
     setMovementForward(closestForward);
     setMovementSideways(closestSideways);
   }
   
   private double direction(float yaw, double movementForward, double movementSideways) {
     if (movementForward < 0.0D) yaw += 180.0F; 
     float forward = 1.0F;
     if (movementForward < 0.0D) { forward = -0.5F; }
     else if (movementForward > 0.0D) { forward = 0.5F; }
      if (movementSideways > 0.0D) yaw -= 90.0F * forward; 
     if (movementSideways < 0.0D) yaw += 90.0F * forward; 
     return Math.toRadians(yaw);
   }
 }

