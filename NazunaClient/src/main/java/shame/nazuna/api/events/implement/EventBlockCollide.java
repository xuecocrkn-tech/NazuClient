package shame.nazuna.api.events.implement;
 import net.minecraft.BlockPos;
 import shame.nazuna.api.events.Event;
 
 public class EventBlockCollide extends Event {
   public BlockPos getPos() {
     return this.pos;
   } private final BlockPos pos;
   public EventBlockCollide(BlockPos pos) {
     this.pos = pos;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\events\implement\EventBlockCollide.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */