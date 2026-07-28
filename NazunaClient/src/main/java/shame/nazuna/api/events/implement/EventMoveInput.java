package shame.nazuna.api.events.implement;
 
 
 public class EventMoveInput extends Event {
   private float forward;
   private float strafe;
   
   public EventMoveInput(float forward, float strafe, boolean jump, boolean sneak) { this.forward = forward; this.strafe = strafe; this.jump = jump; this.sneak = sneak; }
 
 }

