package shame.nazuna.api.events.implement;
 
 import shame.nazuna.api.events.Event;
 
 public class EventSlowWalking
   extends Event {
   private boolean cancelled;
   
   public boolean isCancelled() {
     return this.cancelled;
   }
   
   public void setCancelled(boolean cancelled) {
     this.cancelled = cancelled;
   }
 }

