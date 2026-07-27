package shame.nazuna.api.utils.player;
 
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 public final class ViaProtocolUtils {
   private static final int MC_1_19_PROTOCOL = 759;
   private static final long CACHE_TIME_MS = 1500L;
   private static final Pattern VERSION_PATTERN = Pattern.compile("1\\.(\\d+)");
 
   
   private static long nextRefreshAt;
   
   private static boolean belowOneNineteen;
 
   
   public static boolean isTargetProtocolBelowOneNineteen() {
     long now = System.currentTimeMillis();
     if (now < nextRefreshAt) {
       return belowOneNineteen;
     }
     
     belowOneNineteen = resolveBelowOneNineteen();
     nextRefreshAt = now + 1500L;
     return belowOneNineteen;
   }
   
   private static boolean resolveBelowOneNineteen() {
     try {
       Class<?> viaFabricPlusClass = Class.forName("com.viaversion.viafabricplus.ViaFabricPlus");
       Object impl = viaFabricPlusClass.getMethod("getImpl", new Class[0]).invoke(null, new Object[0]);
       if (impl == null) {
         return false;
       }
       
       Object targetVersion = invokeNoArg(impl, "getTargetVersion");
       if (targetVersion == null) {
         return false;
       }
       
       Integer protocolId = readProtocolId(targetVersion);
       return (protocolId != null && protocolId.intValue() < 759);
     } catch (Throwable ignored) {
       return false;
     } 
   }
   
   private static Object invokeNoArg(Object instance, String methodName) {
     try {
       Method method = instance.getClass().getMethod(methodName, new Class[0]);
       if (!Modifier.isPublic(method.getModifiers()) || method.getParameterCount() != 0) {
         return null;
       }
       return method.invoke(instance, new Object[0]);
     } catch (Throwable ignored) {
       return null;
     } 
   }
   
   private static Integer readProtocolId(Object targetVersion) {
     try {
       Method getVersion = targetVersion.getClass().getMethod("getVersion", new Class[0]);
       Object value = getVersion.invoke(targetVersion, new Object[0]);
       if (value instanceof Number) { Number number = (Number)value;
         return Integer.valueOf(number.intValue()); }
     
     } catch (Throwable throwable) {}
 
     
     try {
       for (Method method : targetVersion.getClass().getMethods()) {
         if (Modifier.isPublic(method.getModifiers()) && method.getParameterCount() == 0) {
 
           
           Class<?> returnType = method.getReturnType();
           if (returnType == int.class || returnType == Integer.class)
           
           { 
             
             String name = method.getName().toLowerCase();
             if (name.contains("version") || name.contains("protocol") || name.contains("id"))
             
             { 
               
               Object value = method.invoke(targetVersion, new Object[0]);
               if (value instanceof Number) { Number number = (Number)value;
                 return Integer.valueOf(number.intValue()); }  }  } 
         } 
       } 
     } catch (Throwable throwable) {}
 
     
     Matcher matcher = VERSION_PATTERN.matcher(String.valueOf(targetVersion));
     if (matcher.find()) {
       int minor = Integer.parseInt(matcher.group(1));
       return Integer.valueOf((minor >= 19) ? 759 : 758);
     } 
     
     return null;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\player\ViaProtocolUtils.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */