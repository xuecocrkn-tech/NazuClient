package shame.nazuna.api.events.implement;
 
 import net.minecraft.Camera;
 import net.minecraft.MatrixStack;
 import net.minecraft.WorldRenderer;
 import org.joml.Matrix4f;
 import shame.nazuna.api.events.Event;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Game
   extends Event
 {
   private final WorldRenderer context;
   private final MatrixStack matrix;
   private final Matrix4f projectionMatrix;
   private final Camera camera;
   private final float partialTicks;
   private final long finishTimeNano;
   
   public Game(WorldRenderer context, MatrixStack matrix, Matrix4f projectionMatrix, Camera camera, float partialTicks, long finishTimeNano) {
     this.context = context; this.matrix = matrix; this.projectionMatrix = projectionMatrix; this.camera = camera; this.partialTicks = partialTicks; this.finishTimeNano = finishTimeNano;
   public long getFinishTimeNano() { return this.finishTimeNano; }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\events\implement\EventRender$Game.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */