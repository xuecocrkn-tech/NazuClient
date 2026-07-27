package shame.nazuna.api.events.implement;
 
 import net.minecraft.DrawContext;
 import shame.nazuna.api.events.Event;
 
 
 
 public class Default
   extends Event
 {
   private final DrawContext context;
   private final float partialTicks;
   
   public Default(DrawContext context, float partialTicks) {
     this.context = context; this.partialTicks = partialTicks;
   public float getPartialTicks() { return this.partialTicks; }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\events\implement\EventRender$Default.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */