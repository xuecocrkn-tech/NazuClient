package shame.nazuna.api.events.implement;
 
 public class EventCloseInv extends Event {
   public EventCloseInv(int windowId) {
     this.windowId = windowId;
   }
   
   public int windowId;
 }

