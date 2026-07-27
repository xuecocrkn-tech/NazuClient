package shame.nazuna.client.modules.impl.misc;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class PlayerHit
   extends Record
 {
   private final String playerName;
   private final int percent;
   private final double distance;
   
   public final String toString() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> toString : (Lshame/astra/client/modules/impl/misc/PotionTracker$PlayerHit;)Ljava/lang/String;
     //   6: areturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #286	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/client/modules/impl/misc/PotionTracker$PlayerHit;
   }
   
   public final int hashCode() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> hashCode : (Lshame/astra/client/modules/impl/misc/PotionTracker$PlayerHit;)I
     //   6: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #286	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/client/modules/impl/misc/PotionTracker$PlayerHit;
   }
   
   public final boolean equals(Object o) {
     // Byte code:
     //   0: aload_0
     //   1: aload_1
     //   2: <illegal opcode> equals : (Lshame/astra/client/modules/impl/misc/PotionTracker$PlayerHit;Ljava/lang/Object;)Z
     //   7: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #286	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	8	0	this	Lshame/astra/client/modules/impl/misc/PotionTracker$PlayerHit;
     //   0	8	1	o	Ljava/lang/Object;
   }
   
   private PlayerHit(String playerName, int percent, double distance) {
     this.playerName = playerName; this.percent = percent; this.distance = distance; } public String playerName() { return this.playerName; } public int percent() { return this.percent; } public double distance() { return this.distance; }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\misc\PotionTracker$PlayerHit.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */