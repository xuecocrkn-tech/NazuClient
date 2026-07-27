package shame.nazuna.api.events;
 
 
 public class Event {
   public void setCancelled(boolean cancelled) {
     this.cancelled = cancelled;
   } private boolean cancelled;
   public boolean isCancelled() {
     return this.cancelled;
   }
   public void cancel() {
     this.cancelled = true;
   }
 
 
   
   public void call() {
     try {
       EventInvoker.invoke(this);
     }
     catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|InstantiationException e) {
       
       throw new RuntimeException("Failed to Invoke Method", e);
     } 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\events\Event.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */