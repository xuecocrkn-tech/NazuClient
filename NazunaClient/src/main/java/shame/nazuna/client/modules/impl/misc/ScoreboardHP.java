package shame.nazuna.client.modules.impl.misc;
 
 import java.util.LinkedHashSet;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import net.minecraft.class_1309;
 import net.minecraft.class_1657;
 import net.minecraft.class_2561;
 import net.minecraft.class_266;
 import net.minecraft.class_268;
 import net.minecraft.class_269;
 import net.minecraft.class_270;
 import net.minecraft.class_640;
 import net.minecraft.class_8646;
 import net.minecraft.class_9011;
 import net.minecraft.class_9013;
 import net.minecraft.class_9015;
 import net.minecraft.class_9022;
 import net.minecraft.class_9025;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 
 public class ScoreboardHP extends Module {
   public static final ScoreboardHP INSTANCE = new ScoreboardHP();
   
   private static final Pattern HP_NUMBER = Pattern.compile("(\\d+(?:[.,]\\d+)?)");
   
   private static final float MAX_REASONABLE_HP = 1024.0F;
   
   public static final float UNKNOWN_HP = -1.0F;
   
   private final BooleanSetting gulpvp = new BooleanSetting("GulpVP", false);
   
   public ScoreboardHP() {
     super("ScoreboardHP", "Обход показа HP для серверов", Module.ModuleCategory.MISC);
     addSettings(new Setting[] { (Setting)this.gulpvp });
   }
 
 
 
   
   private static boolean shouldHideHealth(class_1657 player) {
     if (!INSTANCE.isEnable() || !INSTANCE.gulpvp.isState()) {
       return false;
     }
 
     
     if (mc.method_1558() != null) {
       String serverAddress = (mc.method_1558()).field_3761;
       if (serverAddress != null && serverAddress.toLowerCase().contains("gulpvp.pw")) {
         return true;
       }
     } 
 
 
     
     if (isHealthHiddenOnServer(player)) {
       return true;
     }
     
     return false;
   }
 
 
 
   
   private static boolean isHealthHiddenOnServer(class_1657 player) {
     if (mc.field_1687 == null) return false;
     
     try {
       class_269 scoreboard = mc.field_1687.method_8428();
 
       
       class_266 belowName = scoreboard.method_1189(class_8646.field_45158);
       class_266 list = scoreboard.method_1189(class_8646.field_45156);
 
       
       if (belowName == null && list == null) {
         
         Float sidebarHp = getGulpVpSidebarHealth(player);
         return (sidebarHp == null);
       } 
       
       return false;
     } catch (Exception ignored) {
       return true;
     } 
   }
   public static float getHealth(class_1309 entity) {
     class_1657 player;
     if (entity == null) {
       return 0.0F;
     }
     
     if (!INSTANCE.isEnable()) {
       return entity.method_6032();
     }
     
     if (entity instanceof net.minecraft.class_746) {
       return entity.method_6032();
     }
     
     if (entity instanceof class_1657) { player = (class_1657)entity; }
     else { return entity.method_6032(); }
 
 
     
     if (shouldHideHealth(player)) {
       return -1.0F;
     }
     
     if (INSTANCE.gulpvp.isState()) {
       Float sidebarHp = getGulpVpSidebarHealth(player);
       if (sidebarHp != null) {
         return sidebarHp.floatValue();
       }
     } 
     
     if (mc.method_1558() == null) {
       return entity.method_6032();
     }
     
     return getObjectiveHealth(player);
   }
   public static float getHealthWithAbsorption(class_1309 entity) {
     class_1657 player;
     if (entity == null) {
       return 0.0F;
     }
     
     if (!INSTANCE.isEnable()) {
       return Math.max(0.0F, entity.method_6032() + entity.method_6067());
     }
     
     if (entity instanceof net.minecraft.class_746) {
       return Math.max(0.0F, entity.method_6032() + entity.method_6067());
     }
     
     if (entity instanceof class_1657) { player = (class_1657)entity; }
     else { return Math.max(0.0F, getHealth(entity) + entity.method_6067()); }
 
 
     
     if (shouldHideHealth(player)) {
       return -1.0F;
     }
     
     if (INSTANCE.gulpvp.isState() && entity instanceof class_1657) {
       Float sidebarHp = getGulpVpSidebarHealth(player);
       if (sidebarHp != null) {
         return Math.max(0.0F, sidebarHp.floatValue());
       }
     } 
     
     return Math.max(0.0F, getHealth(entity) + entity.method_6067());
   }
 
 
   
   public static boolean shouldShowUnknownInTargetHud(class_1309 entity) {
     class_1657 player;
     if (entity instanceof class_1657) { player = (class_1657)entity; }
     else { return false; }
     
     return shouldHideHealth(player);
   }
   
   private static float getObjectiveHealth(class_1657 player) {
     try {
       class_269 scoreboard = player.method_7327();
       class_266 objective = scoreboard.method_1189(class_8646.field_45158);
       if (objective == null) {
         objective = scoreboard.method_1189(class_8646.field_45156);
       }
       if (objective == null) {
         return player.method_6032();
       }
       
       class_9013 score = scoreboard.method_55430((class_9015)player, objective);
       if (score == null) {
         return player.method_6032();
       }
       
       return score.method_55397();
     } catch (Exception ignored) {
       return player.method_6032();
     } 
   }
   
   private static Float getGulpVpSidebarHealth(class_1657 player) {
     if (mc.field_1687 == null) {
       return null;
     }
     
     try {
       class_269 scoreboard = mc.field_1687.method_8428();
       class_266 sidebar = scoreboard.method_1189(class_8646.field_45157);
       if (sidebar == null) {
         return null;
       }
       
       class_9022 numberFormat = sidebar.method_55380((class_9022)class_9025.field_47567);
       String[] nameVariants = collectNameVariants(player);
       Float bestHp = null;
       int bestMatchScore = -1;
       
       for (class_9011 entry : scoreboard.method_1184(sidebar)) {
         if (entry.method_55385()) {
           continue;
         }
         
         class_268 team = scoreboard.method_1164(entry.comp_2127());
         String lineText = stripFormatting(class_268.method_1142((class_270)team, entry.method_55387()).getString());
         String ownerText = stripFormatting(entry.comp_2127());
         String scoreText = stripFormatting(entry.method_55386(numberFormat).getString());
         
         int matchScore = getNameMatchScore(lineText, ownerText, nameVariants);
         if (matchScore < 0) {
           continue;
         }
         
         Float hp = extractSidebarHp(entry, scoreText, lineText);
         if (hp == null) {
           continue;
         }
         
         if (matchScore > bestMatchScore) {
           bestMatchScore = matchScore;
           bestHp = hp;
         } 
       } 
       
       return bestHp;
     } catch (Exception ignored) {
       return null;
     } 
   }
   
   private static Float extractSidebarHp(class_9011 entry, String scoreText, String lineText) {
     Float fromScoreColumn = parseHpNumber(scoreText);
     if (fromScoreColumn != null) {
       return fromScoreColumn;
     }
     
     if (isReasonableHp(entry.comp_2128())) {
       return Float.valueOf(entry.comp_2128());
     }
     
     Float fromLine = parseHpFromLine(lineText);
     if (fromLine != null) {
       return fromLine;
     }
     
     return null;
   }
   
   private static String[] collectNameVariants(class_1657 player) {
     String plainName = stripFormatting(player.method_5477().getString());
     String scoreboardName = stripFormatting(player.method_5820());
 
     
     String profileName = (player.method_7334() != null) ? stripFormatting(player.method_7334().getName()) : "";
     
     String tabName = "";
     if (mc.method_1562() != null) {
       class_640 entry = mc.method_1562().method_2871(player.method_5667());
       if (entry != null) {
         class_2561 displayName = entry.method_2971();
         if (displayName != null) {
           tabName = stripFormatting(displayName.getString());
         }
       } 
     } 
     
     String protectedPlain = protectName(plainName);
     String protectedScoreboard = protectName(scoreboardName);
     String protectedProfile = protectName(profileName);
     String protectedTab = protectName(tabName);
     
     return dedupeNames(new String[] { plainName, protectedPlain, scoreboardName, protectedScoreboard, profileName, protectedProfile, tabName, protectedTab });
   }
 
 
 
 
 
 
 
 
 
   
   private static String[] dedupeNames(String... names) {
     LinkedHashSet<String> unique = new LinkedHashSet<>();
     for (String name : names) {
       if (name != null) {
 
         
         String trimmed = name.trim();
         if (!trimmed.isEmpty())
           unique.add(trimmed); 
       } 
     } 
     return (String[])unique.toArray(x$0 -> new String[x$0]);
   }
   
   private static String protectName(String input) {
     if (input == null || input.isEmpty()) {
       return "";
     }
     NameProtect nameProtect = (ModuleClass.INSTANCE != null) ? ModuleClass.nameProtect : null;
     if (nameProtect == null || !nameProtect.isEnable()) {
       return input;
     }
     return nameProtect.patch(input);
   }
   
   private static int getNameMatchScore(String lineText, String ownerText, String[] nameVariants) {
     int best = -1;
     
     for (String name : nameVariants) {
       if (name != null && !name.isEmpty()) {
 
 
         
         if (!ownerText.isEmpty()) {
           if (ownerText.equalsIgnoreCase(name)) {
             best = Math.max(best, 100);
           } else if (ownerText.contains(name)) {
             best = Math.max(best, 80);
           } 
         }
         
         if (!lineText.isEmpty())
         {
 
           
           if (lineText.equalsIgnoreCase(name)) {
             best = Math.max(best, 95);
 
           
           }
           else if (lineText.endsWith(name) || lineText.endsWith(" " + name)) {
             best = Math.max(best, 90);
           }
           else {
             
             int index = indexOfIgnoreCase(lineText, name);
             if (index >= 0)
               best = Math.max(best, 70 + Math.min(20, name.length())); 
           }  } 
       } 
     } 
     return best;
   }
   
   private static int indexOfIgnoreCase(String text, String search) {
     if (text == null || search == null || search.isEmpty()) {
       return -1;
     }
     int limit = text.length() - search.length();
     for (int i = 0; i <= limit; i++) {
       if (text.regionMatches(true, i, search, 0, search.length())) {
         return i;
       }
     } 
     return -1;
   }
   
   private static Float parseHpNumber(String text) {
     if (text == null || text.isEmpty()) {
       return null;
     }
 
 
 
 
 
 
     
     String cleaned = text.replace("❤", "").replace("♥", "").replace("HP", "").replace("hp", "").replace("хп", "").replace("Хп", "").trim();
     
     Matcher matcher = HP_NUMBER.matcher(cleaned);
     if (!matcher.find()) {
       return null;
     }
     
     try {
       float value = Float.parseFloat(matcher.group(1).replace(',', '.'));
       return isReasonableHp(value) ? Float.valueOf(value) : null;
     } catch (NumberFormatException ignored) {
       return null;
     } 
   }
   
   private static Float parseHpFromLine(String line) {
     if (line == null || line.isEmpty()) {
       return null;
     }
     
     Matcher matcher = HP_NUMBER.matcher(line);
     Float last = null;
     while (matcher.find()) {
       try {
         float value = Float.parseFloat(matcher.group(1).replace(',', '.'));
         if (isReasonableHp(value)) {
           last = Float.valueOf(value);
         }
       } catch (NumberFormatException numberFormatException) {}
     } 
     
     return last;
   }
   
   private static boolean isReasonableHp(float value) {
     return (value >= 0.0F && value <= 1024.0F);
   }
   
   private static boolean isReasonableHp(int value) {
     return (value >= 0 && value <= 1024);
   }
   
   private static String stripFormatting(String text) {
     if (text == null) {
       return "";
     }
     return text.replaceAll("§[0-9a-fk-orx]", "")
       .replaceAll("(?i)§x(§[0-9a-f]){6}", "")
       .trim();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\misc\ScoreboardHP.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */