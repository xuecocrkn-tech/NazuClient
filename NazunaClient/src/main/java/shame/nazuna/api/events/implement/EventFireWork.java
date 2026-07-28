package shame.nazuna.api.events.implement;
 
 import net.minecraft.FireworkRocketEntity;
 
 public class EventFireWork extends Event {
   private final FireworkRocketEntity firework;
   
   public FireworkRocketEntity getFirework() {
     return this.firework;
   }
 }

