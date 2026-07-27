package shame.nazuna.api.events.implement;
 
 import net.minecraft.Window;
 import net.minecraft.MatrixStack;
 import org.joml.Matrix4f;
 import shame.nazuna.api.events.Event;
 
 
 
 
 
 
 
 public class World
   extends Event
 {
   private final Window scaledResolution;
   private final float partialTicks;
   private final Matrix4f matrix;
   private final MatrixStack matrixStack;
   
   public World(Window scaledResolution, float partialTicks, Matrix4f matrix, MatrixStack matrixStack) {
     this.scaledResolution = scaledResolution; this.partialTicks = partialTicks; this.matrix = matrix; this.matrixStack = matrixStack;
  }
   public MatrixStack getMatrixStack() { return this.matrixStack; }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\events\implement\EventRender$World.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */