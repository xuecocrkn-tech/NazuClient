package shame.nazuna.api.events;
 
 import java.lang.reflect.Method;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class Invocation
   extends Record
 {
   private final Object listener;
   private final Method method;
   private final int priority;
   
   public final String toString() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> toString : (Lshame/astra/api/events/EventInvoker$Invocation;)Ljava/lang/String;
     //   6: areturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #201	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/api/events/EventInvoker$Invocation;
   }
   
   public final int hashCode() {
     // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> hashCode : (Lshame/astra/api/events/EventInvoker$Invocation;)I
     //   6: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #201	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/api/events/EventInvoker$Invocation;
   }
   
   public final boolean equals(Object o) {
     // Byte code:
     //   0: aload_0
     //   1: aload_1
     //   2: <illegal opcode> equals : (Lshame/astra/api/events/EventInvoker$Invocation;Ljava/lang/Object;)Z
     //   7: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #201	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	8	0	this	Lshame/astra/api/events/EventInvoker$Invocation;
     //   0	8	1	o	Ljava/lang/Object;
   }
   
   private Invocation(Object listener, Method method, int priority) {
     this.listener = listener; this.method = method; this.priority = priority; } public Object listener() { return this.listener; } public Method method() { return this.method; } public int priority() { return this.priority; }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\events\EventInvoker$Invocation.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */