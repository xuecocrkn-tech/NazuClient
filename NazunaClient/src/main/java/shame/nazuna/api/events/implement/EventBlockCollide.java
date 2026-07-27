package shame.nazuna.api.events.implement;
 import net.minecraft.class_2338;
 import shame.nazuna.api.events.Event;
 
 public class EventBlockCollide extends Event {
   public class_2338 getPos() {
     return this.pos;
   } private final class_2338 pos;
   public EventBlockCollide(class_2338 pos) {
     this.pos = pos;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\events\implement\EventBlockCollide.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */