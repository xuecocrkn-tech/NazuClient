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

