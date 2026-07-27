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


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\events\implement\EventSlowWalking.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */