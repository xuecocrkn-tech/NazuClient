package shame.nazuna.api.events.implement;
 
 import net.minecraft.Window;
 import net.minecraft.DrawContext;
 import net.minecraft.Camera;
 import net.minecraft.MatrixStack;
 import net.minecraft.WorldRenderer;
 import org.joml.Matrix4f;
 import shame.nazuna.api.events.Event;
 
 public class EventRender extends Event {
   public static class Default extends Event { private final DrawContext context;
     private final float partialTicks;
     
     public Default(DrawContext context, float partialTicks) { this.context = context; this.partialTicks = partialTicks; }
     public float getPartialTicks() { return this.partialTicks; }
      }
   public static class World extends Event { private final Window scaledResolution; private final float partialTicks; private final Matrix4f matrix; private final MatrixStack matrixStack;
     
     public World(Window scaledResolution, float partialTicks, Matrix4f matrix, MatrixStack matrixStack) { this.scaledResolution = scaledResolution; this.partialTicks = partialTicks; this.matrix = matrix; this.matrixStack = matrixStack; }
     public MatrixStack getMatrixStack() { return this.matrixStack; }
      }
   
   public static class Game extends Event { private final WorldRenderer context; private final MatrixStack matrix; private final Matrix4f projectionMatrix;
     public WorldRenderer getContext() {
    return this.xxx;
  }
     public long getFinishTimeNano() { return this.finishTimeNano; }
      }
 
 }

