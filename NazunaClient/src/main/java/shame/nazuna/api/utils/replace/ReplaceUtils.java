package shame.nazuna.api.utils.replace;
 import net.minecraft.Formatting;
 import net.minecraft.Text;
 import net.minecraft.MutableText;
 
 public class ReplaceUtils {
   public static Text replace(Text input, String target, String replacement) {
     if (input == null || target == null || replacement == null) return input; 
     MutableText result = Text.method_43473().method_10862(input.method_10866());
     appendReplaced(result, input, target, replacement);
     return (Text)result;
   }
   
   private static void appendReplaced(MutableText result, Text current, String target, String replacement) {
     TextContent content = current.method_10851();
     Style style = current.method_10866();
     
     if (content instanceof PlainTextContent.class_2585) { PlainTextContent.class_2585 literal = (PlainTextContent.class_2585)content;
       Pattern pattern = Pattern.compile(Pattern.quote(target), 2);
       String replaced = pattern.matcher(literal.comp_737()).replaceAll(replacement);
       result.method_10852((Text)Text.method_43470(replaced).method_10862(style)); }
 
     
     for (Text sibling : current.method_10855()) {
       appendReplaced(result, sibling, target, replacement);
     }
   }
   
   public static String replaceSymbols(String string) {
     return string
       .replaceAll("ꔗ", String.valueOf(Formatting.field_1078) + "MODER")
       .replaceAll("ꔥ", String.valueOf(Formatting.field_1078) + "ST.MODER")
       .replaceAll("ꔡ", String.valueOf(Formatting.field_1076) + "MODER+")
       .replaceAll("ꔀ", String.valueOf(Formatting.field_1080) + "PLAYER")
       .replaceAll("ꔉ", String.valueOf(Formatting.field_1054) + "HELPER")
       .replaceAll("◆", "@")
       .replaceAll("┃", "|")
       .replaceAll("ꕆ", String.valueOf(Formatting.field_1054) + "PEGAS")
       .replaceAll("ꔸ", String.valueOf(Formatting.field_1054) + "GOD")
       .replaceAll("ꔳ", String.valueOf(Formatting.field_1075) + "Ml.admin")
       .replaceAll("ꔅ", String.valueOf(Formatting.field_1061) + "Y" + String.valueOf(Formatting.field_1061) + "T")
       .replaceAll("ꔂ", String.valueOf(Formatting.field_1078) + "D.MODER")
       .replaceAll("ꕠ", String.valueOf(Formatting.field_1054) + "D.HELPER")
       .replaceAll("ꕄ", String.valueOf(Formatting.field_1061) + "VAMPIRE")
       .replaceAll("ꔖ", String.valueOf(Formatting.field_1075) + "OVERLORD")
       .replaceAll("ꕈ", String.valueOf(Formatting.field_1060) + "COBRA")
       .replaceAll("ꔨ", String.valueOf(Formatting.field_1076) + "DRAGON")
       .replaceAll("ꔤ", String.valueOf(Formatting.field_1061) + "IMPERATOR")
       .replaceAll("ꔠ", String.valueOf(Formatting.field_1065) + "MAGISTER")
       .replaceAll("ꔄ", String.valueOf(Formatting.field_1078) + "HERO")
       .replaceAll("ꔒ", String.valueOf(Formatting.field_1060) + "AVENGER")
       .replaceAll("ꕒ", String.valueOf(Formatting.field_1068) + "RABBIT")
       .replaceAll("ꔈ", String.valueOf(Formatting.field_1054) + "TITAN")
       .replaceAll("ꕀ", String.valueOf(Formatting.field_1077) + "HYDRA")
       .replaceAll("ꔶ", String.valueOf(Formatting.field_1065) + "TIGER")
       .replaceAll("ꔲ", String.valueOf(Formatting.field_1064) + "BULL")
       .replaceAll("ꕖ", String.valueOf(Formatting.field_1074) + "BUNNY")
       .replaceAll("ꕗꕘ", String.valueOf(Formatting.field_1054) + "SPONSOR")
       .replaceAll("🔥", "@")
       .replaceAll("ᴀ", "A")
       .replaceAll("ʙ", "B")
       .replaceAll("ᴄ", "C")
       .replaceAll("ᴅ", "D")
       .replaceAll("ᴇ", "E")
       .replaceAll("ғ", "F")
       .replaceAll("ɢ", "G")
       .replaceAll("ʜ", "H")
       .replaceAll("ɪ", "I")
       .replaceAll("ᴊ", "J")
       .replaceAll("ᴋ", "K")
       .replaceAll("ʟ", "L")
       .replaceAll("ᴍ", "M")
       .replaceAll("ɴ", "N")
       .replaceAll("ꜱ", "S")
       .replaceAll("s", "S")
       .replaceAll("ᴏ", "O")
       .replaceAll("ᴘ", "P")
       .replaceAll("ǫ", "Q")
       .replaceAll("ʀ", "R")
       .replaceAll("ᴛ", "T")
       .replaceAll("ᴜ", "U")
       .replaceAll("ᴠ", "V")
       .replaceAll("ᴡ", "W")
       .replaceAll("ꜰ", "F")
       .replaceAll("x", "X")
       .replaceAll("ʏ", "Y")
       .replaceAll("ᴢ", "Z");
   }
   
   public static Text replaceSymbols(Text text) {
     if (text.getString().contains("ꔗ")) text = replace(text, "ꔗ", String.valueOf(Formatting.field_1078) + "MODER"); 
     if (text.getString().contains("ꔥ")) text = replace(text, "ꔥ", String.valueOf(Formatting.field_1078) + "ST.MODER"); 
     if (text.getString().contains("ꔡ")) text = replace(text, "ꔡ", String.valueOf(Formatting.field_1076) + "MODER+"); 
     if (text.getString().contains("ꔀ")) text = replace(text, "ꔀ", String.valueOf(Formatting.field_1080) + "PLAYER"); 
     if (text.getString().contains("ꔉ")) text = replace(text, "ꔉ", String.valueOf(Formatting.field_1054) + "HELPER"); 
     if (text.getString().contains("◆")) text = replace(text, "◆", "@"); 
     if (text.getString().contains("┃")) text = replace(text, "┃", "|"); 
     if (text.getString().contains("ꔳ")) text = replace(text, "ꔳ", String.valueOf(Formatting.field_1075) + "Ml.admin"); 
     if (text.getString().contains("ꔅ")) text = replace(text, "ꔅ", String.valueOf(Formatting.field_1061) + "Y" + String.valueOf(Formatting.field_1061) + "T"); 
     if (text.getString().contains("ꔂ")) text = replace(text, "ꔂ", String.valueOf(Formatting.field_1078) + "D.MODER"); 
     if (text.getString().contains("ꕠ")) text = replace(text, "ꕠ", String.valueOf(Formatting.field_1054) + "D.HELPER"); 
     if (text.getString().contains("ꕄ")) text = replace(text, "ꕄ", String.valueOf(Formatting.field_1061) + "DRACULA"); 
     if (text.getString().contains("ꔖ")) text = replace(text, "ꔖ", String.valueOf(Formatting.field_1075) + "OVERLORD"); 
     if (text.getString().contains("ꕈ")) text = replace(text, "ꕈ", String.valueOf(Formatting.field_1060) + "COBRA"); 
     if (text.getString().contains("ꔨ")) text = replace(text, "ꔨ", String.valueOf(Formatting.field_1076) + "DRAGON"); 
     if (text.getString().contains("ꔤ")) text = replace(text, "ꔤ", String.valueOf(Formatting.field_1061) + "IMPERATOR"); 
     if (text.getString().contains("ꔠ")) text = replace(text, "ꔠ", String.valueOf(Formatting.field_1065) + "MAGISTER"); 
     if (text.getString().contains("ꔄ")) text = replace(text, "ꔄ", String.valueOf(Formatting.field_1078) + "HERO"); 
     if (text.getString().contains("ꔒ")) text = replace(text, "ꔒ", String.valueOf(Formatting.field_1060) + "AVENGER"); 
     if (text.getString().contains("ꕒ")) text = replace(text, "ꕒ", String.valueOf(Formatting.field_1068) + "RABBIT"); 
     if (text.getString().contains("ꔈ")) text = replace(text, "ꔈ", String.valueOf(Formatting.field_1054) + "TITAN"); 
     if (text.getString().contains("ꕀ")) text = replace(text, "ꕀ", String.valueOf(Formatting.field_1077) + "HYDRA"); 
     if (text.getString().contains("ꔶ")) text = replace(text, "ꔶ", String.valueOf(Formatting.field_1065) + "TIGER"); 
     if (text.getString().contains("ꔲ")) text = replace(text, "ꔲ", String.valueOf(Formatting.field_1064) + "BULL"); 
     if (text.getString().contains("ꕖ")) text = replace(text, "ꕖ", String.valueOf(Formatting.field_1074) + "BUNNY"); 
     if (text.getString().contains("ꕗꕘ")) text = replace(text, "ꕗꕘ", String.valueOf(Formatting.field_1054) + "SPONSOR"); 
     if (text.getString().contains("🔥")) text = replace(text, "🔥", "@");
     
     if (text.getString().contains("ᴀ")) text = replace(text, "ᴀ", "A"); 
     if (text.getString().contains("ʙ")) text = replace(text, "ʙ", "B"); 
     if (text.getString().contains("ᴄ")) text = replace(text, "ᴄ", "C"); 
     if (text.getString().contains("ᴅ")) text = replace(text, "ᴅ", "D"); 
     if (text.getString().contains("ᴇ")) text = replace(text, "ᴇ", "E"); 
     if (text.getString().contains("ғ")) text = replace(text, "ғ", "F"); 
     if (text.getString().contains("ɢ")) text = replace(text, "ɢ", "G"); 
     if (text.getString().contains("ʜ")) text = replace(text, "ʜ", "H"); 
     if (text.getString().contains("ɪ")) text = replace(text, "ɪ", "I"); 
     if (text.getString().contains("ᴊ")) text = replace(text, "ᴊ", "J"); 
     if (text.getString().contains("ᴋ")) text = replace(text, "ᴋ", "K"); 
     if (text.getString().contains("ʟ")) text = replace(text, "ʟ", "L"); 
     if (text.getString().contains("ᴍ")) text = replace(text, "ᴍ", "M"); 
     if (text.getString().contains("ɴ")) text = replace(text, "ɴ", "N"); 
     if (text.getString().contains("ꜱ")) text = replace(text, "ꜱ", "S"); 
     if (text.getString().contains("s")) text = replace(text, "s", "S"); 
     if (text.getString().contains("ᴏ")) text = replace(text, "ᴏ", "O"); 
     if (text.getString().contains("ᴘ")) text = replace(text, "ᴘ", "P"); 
     if (text.getString().contains("ǫ")) text = replace(text, "ǫ", "Q"); 
     if (text.getString().contains("ʀ")) text = replace(text, "ʀ", "R"); 
     if (text.getString().contains("ᴛ")) text = replace(text, "ᴛ", "T"); 
     if (text.getString().contains("ᴜ")) text = replace(text, "ᴜ", "U"); 
     if (text.getString().contains("ᴠ")) text = replace(text, "ᴠ", "V"); 
     if (text.getString().contains("ᴡ")) text = replace(text, "ᴡ", "W"); 
     if (text.getString().contains("ꜰ")) text = replace(text, "ꜰ", "F"); 
     if (text.getString().contains("x")) text = replace(text, "x", "X"); 
     if (text.getString().contains("ʏ")) text = replace(text, "ʏ", "Y"); 
     if (text.getString().contains("ᴢ")) text = replace(text, "ᴢ", "Z");
     
     return text;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\replace\ReplaceUtils.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */