package shame.nazuna.client.modules.impl.misc;
 
 import net.minecraft.Item;
 import shame.nazuna.client.modules.settings.implement.BindSetting;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class HelperBind
   extends Record
 {
   private final String name;
   private final Item item;
   private final BindSetting bind;
   
   public final String toString() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> toString : (Lshame/astra/client/modules/impl/misc/ServerHelper$HelperBind;)Ljava/lang/String;
     //   6: areturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #104	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/client/modules/impl/misc/ServerHelper$HelperBind;
   }
   
   public final int hashCode() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> hashCode : (Lshame/astra/client/modules/impl/misc/ServerHelper$HelperBind;)I
     //   6: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #104	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/client/modules/impl/misc/ServerHelper$HelperBind;
   }
   
   public final boolean equals(Object o) {
     // Byte code:
     //   0: aload_0
     //   1: aload_1
     //   2: <illegal opcode> equals : (Lshame/astra/client/modules/impl/misc/ServerHelper$HelperBind;Ljava/lang/Object;)Z
     //   7: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #104	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	8	0	this	Lshame/astra/client/modules/impl/misc/ServerHelper$HelperBind;
     //   0	8	1	o	Ljava/lang/Object;
   }
   
   public HelperBind(String name, Item item, BindSetting bind) {
     this.name = name; this.item = item; this.bind = bind; } public String name() { return this.name; } public Item item() { return this.item; } public BindSetting bind() { return this.bind; }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\misc\ServerHelper$HelperBind.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */