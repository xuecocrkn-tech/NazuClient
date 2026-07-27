package shame.nazuna.api.utils.render.fonts.ttf;
 import java.awt.Font;
 import java.io.InputStream;
 import java.util.Optional;
 import net.minecraft.Identifier;
 import net.minecraft.MinecraftClient;
 import net.minecraft.Resource;
 
 public class FontUtil {
   public static Font getFontFromTTF(Identifier loc, float fontSize, int fontType) {
     try {
       MinecraftClient client = MinecraftClient.method_1551();
       if (client == null) return null; 
       if (client.method_1478() == null) return null;
       
       Optional<Resource> resource = client.method_1478().method_14486(loc);
       if (resource.isPresent()) {
         InputStream inputStream = ((Resource)resource.get()).method_14482();
         Font output = Font.createFont(fontType, inputStream);
         output = output.deriveFont(fontSize);
         inputStream.close();
         return output;
       } 
       return null;
     } catch (Exception e) {
       e.printStackTrace();
       return null;
     } 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\render\fonts\ttf\FontUtil.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */