package shame.nazuna.api.events.implement;
 
 import net.minecraft.PlayerEntity;
 
 public class EventPopTotem extends Event {
   public PlayerEntity getPlayer() {
     return this.player;
   }
 }

