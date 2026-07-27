package shame.nazuna.api.utils.chat;
 import java.awt.Color;
 import net.minecraft.Text;
 import net.minecraft.Style;
 import net.minecraft.MinecraftClient;
 import net.minecraft.MutableText;
 import net.minecraft.TextColor;
 import shame.nazuna.api.utils.color.ColorUtils;
 
 public final class ChatUtils {
   private ChatUtils() {
     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
   public static void sendMessage(Object message) {
     MinecraftClient mc = MinecraftClient.method_1551();
     
     if (mc.field_1724 == null) {
       System.out.println("[astra] " + String.valueOf(message));
       
       return;
     } 
     MutableText text = Text.method_43470("");
     String prefix = "astra";
     for (int i = 0; i < prefix.length(); i++) {
       text.method_10852((Text)Text.method_43470(String.valueOf(prefix.charAt(i)))
           .method_10862(Style.field_24360
             .method_10982(Boolean.valueOf(true))
             .method_27703(TextColor.method_27717(ColorUtils.gradient(ColorUtils.getThemeColor(0), ColorUtils.getThemeColor(90), i / prefix.length())))));
     }
 
     
     text.method_10852((Text)Text.method_43470(" ⇨ ")
         .method_10862(Style.field_24360
           .method_10982(Boolean.valueOf(false))
           .method_27703(TextColor.method_27717((new Color(200, 200, 200)).getRGB()))));
 
     
     text.method_10852((Text)Text.method_43470(String.valueOf(message))
         .method_10862(Style.field_24360
           .method_10982(Boolean.valueOf(false))
           .method_27703(TextColor.method_27717((new Color(200, 200, 200)).getRGB()))));
 
     
     mc.field_1724.method_7353((Text)text, false);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\chat\ChatUtils.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */