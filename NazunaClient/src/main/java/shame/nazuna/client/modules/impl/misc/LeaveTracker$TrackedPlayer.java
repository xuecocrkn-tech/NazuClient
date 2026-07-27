package shame.nazuna.client.modules.impl.misc;
 
 import net.minecraft.class_2338;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class TrackedPlayer
   extends Record
 {
   private final String name;
   private final class_2338 pos;
   
   public final String toString() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> toString : (Lshame/astra/client/modules/impl/misc/LeaveTracker$TrackedPlayer;)Ljava/lang/String;
     //   6: areturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #96	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/client/modules/impl/misc/LeaveTracker$TrackedPlayer;
   }
   
   public final int hashCode() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> hashCode : (Lshame/astra/client/modules/impl/misc/LeaveTracker$TrackedPlayer;)I
     //   6: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #96	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/client/modules/impl/misc/LeaveTracker$TrackedPlayer;
   }
   
   public final boolean equals(Object o) {
     // Byte code:
     //   0: aload_0
     //   1: aload_1
     //   2: <illegal opcode> equals : (Lshame/astra/client/modules/impl/misc/LeaveTracker$TrackedPlayer;Ljava/lang/Object;)Z
     //   7: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #96	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	8	0	this	Lshame/astra/client/modules/impl/misc/LeaveTracker$TrackedPlayer;
     //   0	8	1	o	Ljava/lang/Object;
   }
   
   private TrackedPlayer(String name, class_2338 pos) {
     this.name = name; this.pos = pos; } public String name() { return this.name; } public class_2338 pos() { return this.pos; }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\misc\LeaveTracker$TrackedPlayer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */