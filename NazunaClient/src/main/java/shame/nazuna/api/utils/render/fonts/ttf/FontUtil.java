package shame.nazuna.api.utils.render.fonts.ttf;
 import java.awt.Font;
 import java.io.InputStream;
 import java.util.Optional;
 import net.minecraft.class_2960;
 import net.minecraft.class_310;
 import net.minecraft.class_3298;
 
 public class FontUtil {
   public static Font getFontFromTTF(class_2960 loc, float fontSize, int fontType) {
     try {
       class_310 client = class_310.method_1551();
       if (client == null) return null; 
       if (client.method_1478() == null) return null;
       
       Optional<class_3298> resource = client.method_1478().method_14486(loc);
       if (resource.isPresent()) {
         InputStream inputStream = ((class_3298)resource.get()).method_14482();
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