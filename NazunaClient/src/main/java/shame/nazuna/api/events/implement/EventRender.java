package shame.nazuna.api.events.implement;
 
 import net.minecraft.class_1041;
 import net.minecraft.class_332;
 import net.minecraft.class_4184;
 import net.minecraft.class_4587;
 import net.minecraft.class_761;
 import org.joml.Matrix4f;
 import shame.nazuna.api.events.Event;
 
 public class EventRender extends Event {
   public static class Default extends Event { private final class_332 context;
     private final float partialTicks;
     
     public Default(class_332 context, float partialTicks) { this.context = context; this.partialTicks = partialTicks; }
     public float getPartialTicks() { return this.partialTicks; }
      }
   public static class World extends Event { private final class_1041 scaledResolution; private final float partialTicks; private final Matrix4f matrix; private final class_4587 matrixStack;
     
     public World(class_1041 scaledResolution, float partialTicks, Matrix4f matrix, class_4587 matrixStack) { this.scaledResolution = scaledResolution; this.partialTicks = partialTicks; this.matrix = matrix; this.matrixStack = matrixStack; }
     public class_4587 getMatrixStack() { return this.matrixStack; }
      }
   
   public static class Game extends Event { private final class_761 context; private final class_4587 matrix; private final Matrix4f projectionMatrix;
     public class_761 getContext() {
     public long getFinishTimeNano() { return this.finishTimeNano; }
      }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\events\implement\EventRender.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */