package shame.nazuna.api.events;
 
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.concurrent.ConcurrentHashMap;
 
 public class EventInvoker
 {
   private static final ConcurrentHashMap<Class<?>, Object> classRegistry = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<Class<? extends Event>, List<Invocation>> invocationCache = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<String, Long> slowHandlerWarnings = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<String, Long> slowEventWarnings = new ConcurrentHashMap<>();
   private static final boolean PERF_DEBUG = Boolean.parseBoolean(System.getProperty("astra.perf.debug", "false"));
   private static final long SLOW_HANDLER_NANOS = Long.getLong("astra.perf.handlerMs", 8L).longValue() * 1000000L;
   private static final long SLOW_EVENT_NANOS = Long.getLong("astra.perf.eventMs", 18L).longValue() * 1000000L;
   private static final long WARN_COOLDOWN_NANOS = Long.getLong("astra.perf.cooldownMs", 1000L).longValue() * 1000000L;
   
   private static volatile boolean cacheDirty = true;
   
   public static void register(Object obj) {
     classRegistry.putIfAbsent(obj.getClass(), obj);
     cacheDirty = true;
   }
 
   
   public static void unregister(Object obj) {
     classRegistry.remove(obj.getClass());
     cacheDirty = true;
   }
 
   
   public static void clean() {
     classRegistry.clear();
     invocationCache.clear();
     cacheDirty = false;
   }
 
   
   public static void invoke(Event event) throws IllegalAccessException, InvocationTargetException, InstantiationException {
     long eventStart = PERF_DEBUG ? System.nanoTime() : 0L;
     if (cacheDirty)
     {
       rebuildCache();
     }
     
     List<Invocation> invocations = invocationCache.get(event.getClass());
     if (invocations == null || invocations.isEmpty()) {
       return;
     }
 
     
     for (Invocation invocation : invocations) {
       
       if (!classRegistry.containsKey(invocation.listener().getClass())) {
         continue;
       }
 
       
       Method method = invocation.method();
       method.setAccessible(true);
       long handlerStart = PERF_DEBUG ? System.nanoTime() : 0L;
       
       try {
         method.invoke(invocation.listener(), new Object[] { event });
       }
       finally {
         
         if (PERF_DEBUG) {
           
           long elapsed = System.nanoTime() - handlerStart;
           if (elapsed >= SLOW_HANDLER_NANOS)
           {
             logSlowHandler(event, invocation, elapsed);
           }
         } 
       } 
     } 
     
     if (PERF_DEBUG) {
       
       long elapsed = System.nanoTime() - eventStart;
       if (elapsed >= SLOW_EVENT_NANOS)
       {
         logSlowEvent(event, elapsed, invocations.size());
       }
     } 
   }
 
   
   public static boolean hasListeners(Class<? extends Event> eventClass) {
     if (cacheDirty)
     {
       rebuildCache();
     }
     
     List<Invocation> invocations = invocationCache.get(eventClass);
     return (invocations != null && !invocations.isEmpty());
   }
 
   
   private static synchronized void rebuildCache() {
     if (!cacheDirty) {
       return;
     }
 
     
     ConcurrentHashMap<Class<? extends Event>, List<Invocation>> rebuilt = new ConcurrentHashMap<>();
     for (Object listener : classRegistry.values()) {
       
       for (Method method : listener.getClass().getDeclaredMethods()) {
         
         if (method.isAnnotationPresent((Class)EventLink.class)) {
 
 
 
           
           Class<?>[] parameters = method.getParameterTypes();
           if (parameters.length == 1 && Event.class.isAssignableFrom(parameters[0])) {
 
 
 
 
             
             Class<? extends Event> eventClass = (Class)parameters[0];
             method.setAccessible(true);
             ((List<Invocation>)rebuilt.computeIfAbsent(eventClass, key -> new ArrayList()))
               .add(new Invocation(listener, method, ((EventLink)method.<EventLink>getAnnotation(EventLink.class)).priority()));
           } 
         } 
       } 
     }  for (Iterator<List<Invocation>> iterator = rebuilt.values().iterator(); iterator.hasNext(); ) { List<Invocation> invocations = iterator.next();
       
       invocations.sort((a, b) -> {
             int priorityCompare = Integer.compare(b.priority(), a.priority());
             
             if (priorityCompare != 0) {
               return priorityCompare;
             }
             
             int classCompare = a.listener().getClass().getName().compareTo(b.listener().getClass().getName());
             return (classCompare != 0) ? classCompare : a.method().getName().compareTo(b.method().getName());
           }); }
     
     invocationCache.clear();
     invocationCache.putAll(rebuilt);
     cacheDirty = false;
   }
 
   
   private static void logSlowHandler(Event event, Invocation invocation, long elapsedNanos) {
     String listenerName = invocation.listener().getClass().getSimpleName();
     String methodName = invocation.method().getName();
     String eventName = event.getClass().getSimpleName();
     String key = "handler:" + eventName + ":" + listenerName + "#" + methodName;
     if (!canWarn(slowHandlerWarnings, key)) {
       return;
     }
 
     
     System.out.println(String.format(Locale.ROOT, "[PerfDebug] Slow handler: %s -> %s#%s took %.2f ms", new Object[] { eventName, listenerName, methodName, 
 
 
 
             
             Double.valueOf(elapsedNanos / 1000000.0D) }));
   }
 
   
   private static void logSlowEvent(Event event, long elapsedNanos, int invocationCount) {
     String eventName = event.getClass().getSimpleName();
     String key = "event:" + eventName;
     if (!canWarn(slowEventWarnings, key)) {
       return;
     }
 
     
     System.out.println(String.format(Locale.ROOT, "[PerfDebug] Slow event: %s took %.2f ms for %d handlers", new Object[] { eventName, 
 
             
             Double.valueOf(elapsedNanos / 1000000.0D), 
             Integer.valueOf(invocationCount) }));
   }
 
   
   private static boolean canWarn(ConcurrentHashMap<String, Long> warnings, String key) {
     long now = System.nanoTime();
     Long lastWarn = warnings.get(key);
     if (lastWarn != null && now - lastWarn.longValue() < WARN_COOLDOWN_NANOS)
     {
       return false;
     }
     
     warnings.put(key, Long.valueOf(now));
     return true;
   }
   private static final class Invocation extends Record { private final Object listener; private final Method method; private final int priority;
     private Invocation(Object listener, Method method, int priority) { this.listener = listener; this.method = method; this.priority = priority; } public final String toString() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> toString : (Lshame/astra/api/events/EventInvoker$Invocation;)Ljava/lang/String;
       //   6: areturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #201	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/api/events/EventInvoker$Invocation; } public Object listener() { return this.listener; } public final int hashCode() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> hashCode : (Lshame/astra/api/events/EventInvoker$Invocation;)I
       //   6: ireturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #201	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/api/events/EventInvoker$Invocation; } public final boolean equals(Object o) { // Byte code:
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
       //   0	8	1	o	Ljava/lang/Object; } public Method method() { return this.method; } public int priority() { return this.priority; }
      }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\events\EventInvoker.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */