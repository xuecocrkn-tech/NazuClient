package shame.nazuna.client.modules.impl.render;
 
 import net.minecraft.Vec3d;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class HitBubble
   extends Record
 {
   private final Vec3d pos;
   private final long spawnTime;
   private final float spinSeed;
   private final float sideYaw;
   
   public final String toString() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> toString : (Lshame/astra/client/modules/impl/render/HitBubbles$HitBubble;)Ljava/lang/String;
     //   6: areturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #146	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/client/modules/impl/render/HitBubbles$HitBubble;
   }
   
   public final int hashCode() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> hashCode : (Lshame/astra/client/modules/impl/render/HitBubbles$HitBubble;)I
     //   6: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #146	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/client/modules/impl/render/HitBubbles$HitBubble;
   }
   
   public final boolean equals(Object o) {
     // Byte code:
     //   0: aload_0
     //   1: aload_1
     //   2: <illegal opcode> equals : (Lshame/astra/client/modules/impl/render/HitBubbles$HitBubble;Ljava/lang/Object;)Z
     //   7: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #146	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	8	0	this	Lshame/astra/client/modules/impl/render/HitBubbles$HitBubble;
     //   0	8	1	o	Ljava/lang/Object;
   }
   
   private HitBubble(Vec3d pos, long spawnTime, float spinSeed, float sideYaw) {
     this.pos = pos; this.spawnTime = spawnTime; this.spinSeed = spinSeed; this.sideYaw = sideYaw; } public Vec3d pos() { return this.pos; } public long spawnTime() { return this.spawnTime; } public float spinSeed() { return this.spinSeed; } public float sideYaw() { return this.sideYaw; }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\HitBubbles$HitBubble.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */