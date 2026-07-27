package shame.nazuna.client.modules.impl.misc;
 
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import net.minecraft.class_2561;
 import net.minecraft.class_342;
 import net.minecraft.class_408;
 import net.minecraft.class_437;
 import shame.nazuna.api.utils.replace.ReplaceUtils;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.TextSetting;
 import shame.nazuna.mixin.ChatScreenAccessor;
 
 public class NameProtect extends Module {
   public static final NameProtect INSTANCE = new NameProtect();
   private final BooleanSetting friends = new BooleanSetting("Скрывать друзей", true);
   private final BooleanSetting grief = new BooleanSetting("Скрывать гриф", false);
   private final TextSetting nickname = new TextSetting("Никнейм", "astra", 32);
   private static final int PATCH_CACHE_LIMIT = 512;
   
   private final Map<String, String> patchCache = new LinkedHashMap<String, String>(512, 0.75F, true)
     {
       protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
         return (size() > 512);
       }
     };
   
   private NameProtect() {
     super("NameProtect", "Скрывает никнеймы", Module.ModuleCategory.MISC);
     addSettings(new Setting[] { (Setting)this.friends, (Setting)this.grief, (Setting)this.nickname });
   }
   
   public String patch(String text) {
     if (text == null) {
       return null;
     }
     if (!shouldPatch()) {
       return text;
     }
     
     String cacheKey = getPatchCacheKey(text);
     String cached = this.patchCache.get(cacheKey);
     if (cached != null) {
       return cached;
     }
     
     String out = text;
     String replacement = getReplacementName();
     out = replaceIgnoreCase(out, mc.method_1548().method_1676(), replacement);
     if (this.friends.isState() && astra.INSTANCE != null && astra.INSTANCE.friendStorage != null) {
       for (String friend : astra.INSTANCE.friendStorage.getFriends()) {
         out = replaceIgnoreCase(out, friend, replacement);
       }
     }
     out = patchGrief(out);
     this.patchCache.put(cacheKey, out);
     return out;
   }
   
   public String patchIncomingText(String text) {
     return patch(text);
   }
   
   public class_2561 patchText(class_2561 text) {
     if (text == null) {
       return null;
     }
     
     if (!shouldPatch()) {
       return text;
     }
     
     class_2561 output = text;
     String replacement = getReplacementName();
     output = ReplaceUtils.replace(output, mc.method_1548().method_1676(), replacement);
     if (this.friends.isState() && astra.INSTANCE != null && astra.INSTANCE.friendStorage != null) {
       for (String friend : astra.INSTANCE.friendStorage.getFriends()) {
         output = ReplaceUtils.replace(output, friend, replacement);
       }
     }
     return output;
   }
   
   public String getReplacementName() {
     String value = this.nickname.get();
     return (value == null || value.isBlank()) ? "astra" : value;
   }
   
   public boolean shouldHideGrief() {
     return this.grief.isState();
   }
   
   private String replaceIgnoreCase(String text, String target, String replacement) {
     if (text == null || target == null || target.isEmpty()) {
       return text;
     }
     int firstIndex = indexOfIgnoreCase(text, target, 0);
     if (firstIndex < 0) {
       return text;
     }
     
     StringBuilder out = new StringBuilder(text.length() + replacement.length());
     int from = 0;
     int index = firstIndex;
     while (index >= 0) {
       out.append(text, from, index).append(replacement);
       from = index + target.length();
       index = indexOfIgnoreCase(text, target, from);
     } 
     out.append(text, from, text.length());
     return out.toString();
   }
   
   private int indexOfIgnoreCase(String text, String target, int from) {
     int max = text.length() - target.length();
     for (int i = Math.max(0, from); i <= max; i++) {
       if (text.regionMatches(true, i, target, 0, target.length())) {
         return i;
       }
     } 
     return -1;
   }
   
   private String patchGrief(String text) {
     if (text == null || !this.grief.isState()) {
       return text;
     }
     
     String out = text.replaceAll("Анархия-\\d+", "AstraBETA.fun");
     out = out.replaceAll("ГРИФ #\\d+", "AstraBETA.fun");
     return out;
   }
   
   private String getPatchCacheKey(String text) {
     String username = (mc != null && mc.method_1548() != null) ? mc.method_1548().method_1676() : "";
     int friendsHash = 0;
     if (this.friends.isState() && astra.INSTANCE != null && astra.INSTANCE.friendStorage != null) {
       List<String> friendList = astra.INSTANCE.friendStorage.getFriends();
       friendsHash = friendList.hashCode();
     } 
     return username + "\002" + username + "\002" + 
       getReplacementName() + "\002" + this.friends
       .isState() + "\002" + this.grief
       .isState() + "\002" + friendsHash;
   }
 
 
   
   private boolean shouldPatch() {
     return (isEnable() && mc != null && mc.field_1724 != null && mc.field_1687 != null && !isFriendRemoveInputActive());
   }
   private boolean isFriendRemoveInputActive() {
     class_408 chatScreen;
     class_437 class_437 = mc.field_1755; if (class_437 instanceof class_408) { chatScreen = (class_408)class_437; }
     else { return false; }
 
     
     class_342 chatField = ((ChatScreenAccessor)chatScreen).astra$getChatField();
     if (chatField == null) {
       return false;
     }
     
     String input = chatField.method_1882();
     if (input == null) {
       return false;
     }
     
     String normalized = input.trim().toLowerCase();
 
     
     String prefix = (astra.INSTANCE != null && astra.INSTANCE.commandStorage != null) ? astra.INSTANCE.commandStorage.getPrefix().toLowerCase() : ".";
     return normalized.startsWith(prefix + "friend remove");
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\misc\NameProtect.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */