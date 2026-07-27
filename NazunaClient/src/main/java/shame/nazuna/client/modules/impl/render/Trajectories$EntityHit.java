package shame.nazuna.client.modules.impl.render;
 
 import net.minecraft.class_1297;
 import net.minecraft.class_243;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class EntityHit
   extends Record
 {
   private final class_1297 entity;
   private final class_243 hitPos;
   
   public final String toString() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> toString : (Lshame/astra/client/modules/impl/render/Trajectories$EntityHit;)Ljava/lang/String;
     //   6: areturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #373	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/client/modules/impl/render/Trajectories$EntityHit;
   }
   
   public final int hashCode() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> hashCode : (Lshame/astra/client/modules/impl/render/Trajectories$EntityHit;)I
     //   6: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #373	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/client/modules/impl/render/Trajectories$EntityHit;
   }
   
   public final boolean equals(Object o) {
     // Byte code:
     //   0: aload_0
     //   1: aload_1
     //   2: <illegal opcode> equals : (Lshame/astra/client/modules/impl/render/Trajectories$EntityHit;Ljava/lang/Object;)Z
     //   7: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #373	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	8	0	this	Lshame/astra/client/modules/impl/render/Trajectories$EntityHit;
     //   0	8	1	o	Ljava/lang/Object;
   }
   
   private EntityHit(class_1297 entity, class_243 hitPos) {
     this.entity = entity; this.hitPos = hitPos; } public class_1297 entity() { return this.entity; } public class_243 hitPos() { return this.hitPos; }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\Trajectories$EntityHit.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */