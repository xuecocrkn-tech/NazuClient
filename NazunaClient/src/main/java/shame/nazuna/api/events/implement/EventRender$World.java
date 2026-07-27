package shame.nazuna.api.events.implement;
 
 import net.minecraft.class_1041;
 import net.minecraft.class_4587;
 import org.joml.Matrix4f;
 import shame.nazuna.api.events.Event;
 
 
 
 
 
 
 
 public class World
   extends Event
 {
   private final class_1041 scaledResolution;
   private final float partialTicks;
   private final Matrix4f matrix;
   private final class_4587 matrixStack;
   
   public World(class_1041 scaledResolution, float partialTicks, Matrix4f matrix, class_4587 matrixStack) {
     this.scaledResolution = scaledResolution; this.partialTicks = partialTicks; this.matrix = matrix; this.matrixStack = matrixStack;
   public class_4587 getMatrixStack() { return this.matrixStack; }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\events\implement\EventRender$World.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */