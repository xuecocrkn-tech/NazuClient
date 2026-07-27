package shame.nazuna.client.modules.impl.render.base.implement;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class ModuleEntry
   extends Record
 {
   private final String lowerName;
   private final float width;
   private final float anim;
   
   public final String toString() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> toString : (Lshame/astra/client/modules/impl/render/base/implement/ArrayListHud$ModuleEntry;)Ljava/lang/String;
     //   6: areturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #158	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/client/modules/impl/render/base/implement/ArrayListHud$ModuleEntry;
   }
   
   public final int hashCode() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> hashCode : (Lshame/astra/client/modules/impl/render/base/implement/ArrayListHud$ModuleEntry;)I
     //   6: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #158	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/client/modules/impl/render/base/implement/ArrayListHud$ModuleEntry;
   }
   
   public final boolean equals(Object o) {
     // Byte code:
     //   0: aload_0
     //   1: aload_1
     //   2: <illegal opcode> equals : (Lshame/astra/client/modules/impl/render/base/implement/ArrayListHud$ModuleEntry;Ljava/lang/Object;)Z
     //   7: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #158	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	8	0	this	Lshame/astra/client/modules/impl/render/base/implement/ArrayListHud$ModuleEntry;
     //   0	8	1	o	Ljava/lang/Object;
   }
   
   private ModuleEntry(String lowerName, float width, float anim) {
     this.lowerName = lowerName; this.width = width; this.anim = anim; } public String lowerName() { return this.lowerName; } public float width() { return this.width; } public float anim() { return this.anim; }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\base\implement\ArrayListHud$ModuleEntry.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */