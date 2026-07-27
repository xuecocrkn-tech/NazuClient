package shame.nazuna.api.events.implement;
 
 import net.minecraft.class_4184;
 import net.minecraft.class_4587;
 import org.joml.Matrix4f;
 import shame.nazuna.api.events.Event;
 
 public class Event3DRender extends Event {
   private final class_4587 matrices;
   private final Matrix4f positionMatrix;
   private final Matrix4f projectionMatrix;
   private final class_4184 camera;
   private final float tickDelta;
   
   public class_4587 getMatrices() {
     return this.matrices;
   }
   
   public Matrix4f getPositionMatrix() {
     return this.positionMatrix;
   }
   
   public Matrix4f getProjectionMatrix() {
     return this.projectionMatrix;
   }
   
   public class_4184 getCamera() {
     return this.camera;
   }
   
   public float getTickDelta() {
     return this.tickDelta;
   }
   
   public Event3DRender(class_4587 matrices, Matrix4f positionMatrix, Matrix4f projectionMatrix, class_4184 camera, float tickDelta) {
     this.matrices = matrices;
     this.positionMatrix = positionMatrix;
     this.projectionMatrix = projectionMatrix;
     this.camera = camera;
     this.tickDelta = tickDelta;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\events\implement\Event3DRender.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */