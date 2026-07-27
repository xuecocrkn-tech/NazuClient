package shame.nazuna.client.modules.impl.render;
 
 import net.minecraft.Vec3d;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class ImpactPoint
   extends Record
 {
   private final Vec3d pos;
   private final float seconds;
   
   public final String toString() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> toString : (Lshame/astra/client/modules/impl/render/Projectile$ImpactPoint;)Ljava/lang/String;
     //   6: areturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #43	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/client/modules/impl/render/Projectile$ImpactPoint;
   }
   
   public final int hashCode() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> hashCode : (Lshame/astra/client/modules/impl/render/Projectile$ImpactPoint;)I
     //   6: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #43	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/client/modules/impl/render/Projectile$ImpactPoint;
   }
   
   public final boolean equals(Object o) {
     // Byte code:
     //   0: aload_0
     //   1: aload_1
     //   2: <illegal opcode> equals : (Lshame/astra/client/modules/impl/render/Projectile$ImpactPoint;Ljava/lang/Object;)Z
     //   7: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #43	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	8	0	this	Lshame/astra/client/modules/impl/render/Projectile$ImpactPoint;
     //   0	8	1	o	Ljava/lang/Object;
   }
   
   private ImpactPoint(Vec3d pos, float seconds) {
     this.pos = pos; this.seconds = seconds; } public Vec3d pos() { return this.pos; } public float seconds() { return this.seconds; }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\Projectile$ImpactPoint.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */