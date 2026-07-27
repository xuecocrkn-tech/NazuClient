package shame.nazuna.api.events.implement;
 
 
 public class EventMoveInput extends Event {
   private float forward;
   private float strafe;
   
   public EventMoveInput(float forward, float strafe, boolean jump, boolean sneak) { this.forward = forward; this.strafe = strafe; this.jump = jump; this.sneak = sneak; }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\events\implement\EventMoveInput.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */