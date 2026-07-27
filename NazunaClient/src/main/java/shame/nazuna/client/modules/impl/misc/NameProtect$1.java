package shame.nazuna.client.modules.impl.misc;
 
 import java.util.LinkedHashMap;
 import java.util.Map;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 class null
   extends LinkedHashMap<String, String>
 {
   null(int initialCapacity, float loadFactor, boolean accessOrder) {
     super(initialCapacity, loadFactor, accessOrder);
   }
   protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
     return (size() > 512);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\misc\NameProtect$1.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */