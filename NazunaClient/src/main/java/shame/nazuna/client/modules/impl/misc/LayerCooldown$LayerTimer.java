package shame.nazuna.client.modules.impl.misc;
 
 import net.minecraft.class_2338;
 import net.minecraft.class_243;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class LayerTimer
   extends Record
 {
   private final class_2338 blockPos;
   private final class_243 pos;
   private final long endTime;
   
   public final String toString() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> toString : (Lshame/astra/client/modules/impl/misc/LayerCooldown$LayerTimer;)Ljava/lang/String;
     //   6: areturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #289	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/client/modules/impl/misc/LayerCooldown$LayerTimer;
   }
   
   public final int hashCode() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> hashCode : (Lshame/astra/client/modules/impl/misc/LayerCooldown$LayerTimer;)I
     //   6: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #289	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/client/modules/impl/misc/LayerCooldown$LayerTimer;
   }
   
   public final boolean equals(Object o) {
     // Byte code:
     //   0: aload_0
     //   1: aload_1
     //   2: <illegal opcode> equals : (Lshame/astra/client/modules/impl/misc/LayerCooldown$LayerTimer;Ljava/lang/Object;)Z
     //   7: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #289	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	8	0	this	Lshame/astra/client/modules/impl/misc/LayerCooldown$LayerTimer;
     //   0	8	1	o	Ljava/lang/Object;
   }
   
   private LayerTimer(class_2338 blockPos, class_243 pos, long endTime) {
     this.blockPos = blockPos; this.pos = pos; this.endTime = endTime; } public class_2338 blockPos() { return this.blockPos; } public class_243 pos() { return this.pos; } public long endTime() { return this.endTime; }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\misc\LayerCooldown$LayerTimer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */