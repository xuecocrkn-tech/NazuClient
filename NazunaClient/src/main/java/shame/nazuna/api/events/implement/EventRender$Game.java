package shame.nazuna.api.events.implement;
 
 import net.minecraft.class_4184;
 import net.minecraft.class_4587;
 import net.minecraft.class_761;
 import org.joml.Matrix4f;
 import shame.nazuna.api.events.Event;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Game
   extends Event
 {
   private final class_761 context;
   private final class_4587 matrix;
   private final Matrix4f projectionMatrix;
   private final class_4184 camera;
   private final float partialTicks;
   private final long finishTimeNano;
   
   public Game(class_761 context, class_4587 matrix, Matrix4f projectionMatrix, class_4184 camera, float partialTicks, long finishTimeNano) {
     this.context = context; this.matrix = matrix; this.projectionMatrix = projectionMatrix; this.camera = camera; this.partialTicks = partialTicks; this.finishTimeNano = finishTimeNano;
   public long getFinishTimeNano() { return this.finishTimeNano; }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\events\implement\EventRender$Game.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */