package shame.nazuna.api.events.implement;
 
 import net.minecraft.Camera;
 import net.minecraft.MatrixStack;
 import org.joml.Matrix4f;
 import shame.nazuna.api.events.Event;
 
 public class Event3DRender extends Event {
   private final MatrixStack matrices;
   private final Matrix4f positionMatrix;
   private final Matrix4f projectionMatrix;
   private final Camera camera;
   private final float tickDelta;
   
   public MatrixStack getMatrices() {
     return this.matrices;
   }
   
   public Matrix4f getPositionMatrix() {
     return this.positionMatrix;
   }
   
   public Matrix4f getProjectionMatrix() {
     return this.projectionMatrix;
   }
   
   public Camera getCamera() {
     return this.camera;
   }
   
   public float getTickDelta() {
     return this.tickDelta;
   }
   
   public Event3DRender(MatrixStack matrices, Matrix4f positionMatrix, Matrix4f projectionMatrix, Camera camera, float tickDelta) {
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