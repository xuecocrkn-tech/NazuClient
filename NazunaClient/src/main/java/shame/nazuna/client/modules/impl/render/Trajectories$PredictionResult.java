package shame.nazuna.client.modules.impl.render;
 
 import net.minecraft.class_1297;
 import net.minecraft.class_243;
 import net.minecraft.class_3965;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class PredictionResult
   extends Record
 {
   private final class_243[] points;
   private final class_3965 blockHit;
   private final class_243 hitPos;
   private final class_1297 entityHit;
   private final class_243 entityHitPos;
   
   public final String toString() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> toString : (Lshame/astra/client/modules/impl/render/Trajectories$PredictionResult;)Ljava/lang/String;
     //   6: areturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #376	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/client/modules/impl/render/Trajectories$PredictionResult;
   }
   
   public final int hashCode() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> hashCode : (Lshame/astra/client/modules/impl/render/Trajectories$PredictionResult;)I
     //   6: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #376	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/client/modules/impl/render/Trajectories$PredictionResult;
   }
   
   public final boolean equals(Object o) {
     // Byte code:
     //   0: aload_0
     //   1: aload_1
     //   2: <illegal opcode> equals : (Lshame/astra/client/modules/impl/render/Trajectories$PredictionResult;Ljava/lang/Object;)Z
     //   7: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #376	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	8	0	this	Lshame/astra/client/modules/impl/render/Trajectories$PredictionResult;
     //   0	8	1	o	Ljava/lang/Object;
   }
   
   private PredictionResult(class_243[] points, class_3965 blockHit, class_243 hitPos, class_1297 entityHit, class_243 entityHitPos) {
     this.points = points; this.blockHit = blockHit; this.hitPos = hitPos; this.entityHit = entityHit; this.entityHitPos = entityHitPos; } public class_243[] points() { return this.points; } public class_3965 blockHit() { return this.blockHit; } public class_243 hitPos() { return this.hitPos; } public class_1297 entityHit() { return this.entityHit; } public class_243 entityHitPos() { return this.entityHitPos; }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\Trajectories$PredictionResult.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */