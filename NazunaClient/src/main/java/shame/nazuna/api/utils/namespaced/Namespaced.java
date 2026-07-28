package shame.nazuna.api.utils.namespaced;
 
 import net.minecraft.Identifier;
 
 public class Namespaced
 {
   public static Identifier of(String path) {
     return Identifier.method_60655("astra", path);
   }
 }

